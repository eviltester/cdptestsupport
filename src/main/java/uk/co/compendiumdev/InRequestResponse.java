package uk.co.compendiumdev;

import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Network;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.protocol.types.debugger.SearchMatch;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import com.google.devtools.common.options.OptionsParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InRequestResponse {

    // Protocol Docs:
    // https://chromedevtools.github.io/devtools-protocol/
    // Library Docs:
    // https://github.com/kklisura/chrome-devtools-java-client

    public static void main(String[] args) {

        ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

        //File propertiesFileName =
        OptionsParser parser = OptionsParser.newOptionsParser(CliOptions.class);
        parser.parseAndExitUponError(args);
        CliOptions options = parser.getOptions(CliOptions.class);
        if (options.configFile.trim().length()==0) {
            printUsage(parser);
            return;
        }

        File propertiesFile = new File(options.configFile);
        if(!propertiesFile.exists()){
            System.out.println("Cannot find properties file " + propertiesFile.getAbsolutePath());
            return;
        }

        final Map<String, Set<String>> config = loadProperties(propertiesFile);

        System.out.println("Starting: Use Control+C to stop execution");

        System.out.println("Monitoring For:");
        for(String entityType : config.keySet()){
            for(String entityValue : config.get(entityType)) {
                    System.out.println(entityType + " valued at: " + entityValue);
            }
        }

        final ChromeLauncher launcher = new ChromeLauncher();
        final ChromeService chromeService = launcher.launch(false);

        ChromeTab tab = chromeService.createTab();

        final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);
        final Page page = devToolsService.getPage();
        final Network network = devToolsService.getNetwork();

        if(options.clearCache.equalsIgnoreCase("true")) {
            System.out.println("Please Wait: Clearing Cache");
            network.clearBrowserCache();
            System.out.println("Cleared Cache");
        }

        // Log requests with onRequestWillBeSent event handler.
        network.onRequestWillBeSent(
                event ->{
                    if(event.getRequest().getHasPostData()){
                        String body = event.getRequest().getPostData();
                        findIn(config, body, event.getRequest().getUrl(),  "request", messages);
                    }
                });


        network.onResponseReceived(
                event ->{
                    String body = network.getResponseBody(event.getRequestId()).getBody();
                    findIn(config, body, event.getResponse().getUrl(),  "response received", messages);
                }
        );

        // TODO: toggle granularity of event checking with option e.g. --onData --onLoaded --onReceived
//        network.onDataReceived(
//                event ->{
//                    if(event.getDataLength()>0){
//                        String body = network.getResponseBody(event.getRequestId()).getBody();
//                        findIn(config, body, "",  "data received", messages);
//                    }
//                }
//        );

        network.onLoadingFinished(
                event ->{

                    String body = network.getResponseBody(event.getRequestId()).getBody();
                    findIn(config, body, "",  "loading finished", messages);
                }
        );


        // Enable network events.
        network.enable();

        page.navigate(options.defaultUrl);

        // infinite loop to keep it all going - need some keyboard monitoring to exit
        while(true){
                //System.out.println("thread messages " + messages.size());
                String messageQ = messages.poll();
                while(messageQ!=null){
                    System.out.println(messageQ);
                    messageQ = messages.poll();
                }
                //System.out.println("threaded messages " + messages.size());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static void findIn(final Map<String, Set<String>> config, final String body, final String url, final String inName, final ConcurrentLinkedQueue<String> messages) {
        for (String entityType : config.keySet()) {
            for (String entityValue : config.get(entityType)) {
                if(body.contains(entityValue)) {
                    messages.add(inName + ": " + url);
                    messages.add("Found " + entityType + " valued at: " + entityValue + " in " + inName);
                }
            }
        }
    }

    private static void printUsage(OptionsParser parser) {
        System.out.println("Usage: java -jar app-name-here.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),
                OptionsParser.HelpVerbosity.LONG));
    }

    private static Map<String, Set<String>> loadProperties(File properties) {

        Map<String, Set<String>> config = new HashMap<>();

        Properties findWhat = new Properties();
        try {
            findWhat.load(new FileReader(properties));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String name : findWhat.stringPropertyNames()){
            String parsed[] = name.split("_");
            String entityType = parsed[0];
            if(entityType.trim().length()>0){
                entityType = entityType.trim();
                Set values;
                if(config.containsKey(entityType)){
                    values = config.get(entityType);
                }else{
                    values = new HashSet();
                    config.put(entityType, values);
                }
                String value = findWhat.getProperty(name);
                if(value != null & value.trim().length()>0){
                    values.add(value);
                }
            }
        }
        return config;
    }
}

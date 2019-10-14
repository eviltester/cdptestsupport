package uk.co.compendiumdev;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MonitorConfig {

    // TODO: this should store 'values' not external objects
    CliOptions cli;
    Map<String, Set<String>> config;

    MonitorConfig(){
        cli = new CliOptions();
        config = new HashMap<>();
    }

    public MonitorConfig setFromOptions(final CliOptions clioptions) {
         cli = clioptions;
        return this;
    }

    public void setFromPropertiesFile(final File propertiesFile) {

        loadProperties(propertiesFile);

        System.out.println("Monitoring For:");
        for(String entityType : config.keySet()){
            for(String entityValue : config.get(entityType)) {
                System.out.println(entityType + " valued at: " + entityValue);
            }
        }
    }

    private MonitorConfig loadProperties(File properties) {

        config = new HashMap<>();

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
        return this;
    }

    public boolean shouldClearCache() {
        return cli.clearCache.equalsIgnoreCase("true");
    }

    public String getConfigFile() {
        return cli.configFile;
    }

    public String getDefaultUrl() {
        return cli.defaultUrl;
    }

    public Set<String> getEntityNames() {
        return config.keySet();
    }

    public Set<String> getEntityValues(final String entityType) {
        return config.get(entityType);
    }
}

package uk.co.compendiumdev;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class CliOptions extends OptionsBase {

    @Option(
            name = "config",
            abbrev = 'c',
            help = "filename of properties config",
            category = "config",
            allowMultiple = false,
            defaultValue = ""
    )
    public String configFile;

    @Option(
            name = "url",
            abbrev = 'u',
            help = "default url to open",
            category = "config",
            allowMultiple = false,
            defaultValue = "https://eviltester.com"
    )
    public String defaultUrl;

    @Option(
            name = "clearcache",
            help = "clear cache if true",
            category = "config",
            allowMultiple = false,
            defaultValue = "false"
    )
    public String clearCache;
}

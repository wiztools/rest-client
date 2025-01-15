package org.wiztools.restclient.ui;

import java.util.List;
import javax.swing.SwingUtilities;

import com.google.devtools.common.options.OptionsParser;
import org.wiztools.restclient.HTTPClientRequestExecuter;
import org.wiztools.restclient.ServiceLocator;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

/**
 *
 * @author subwiz
 */
public class Main {
    public static class CliOptions extends OptionsBase{
        @Option(
                name = "debug",
                abbrev = 'd',
                help = "Print debug information of the module selected: ServiceLocator, HttpExecutor",
                defaultValue = "help",
                allowMultiple = true,
                valueHelp = "Can be: ServiceLocator, HttpExecutor"
        )
        public List<String> debug;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        // Cli parsing:
        OptionsParser parser = OptionsParser.newOptionsParser(CliOptions.class);
        parser.parseAndExitUponError(args);
        CliOptions options = parser.getOptions(CliOptions.class);
        if(!options.debug.isEmpty()) {
            for(String opt: options.debug) {
                switch (opt) {
                    case "ServiceLocator":
                        ServiceLocator.traceLog = true;
                        break;
                    case "HttpExecutor":
                        HTTPClientRequestExecuter.traceLog = true;
                        break;
                    default:
                        System.err.println("Unknown debug option: "+opt);
                        System.exit(1);
                }
            }
        }

        if(Native.isMac) {
            FlatLightLaf.setup(new FlatMacLightLaf());
        } else {
            FlatLightLaf.setup();
        }

        // Work on the UI:
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ServiceLocator.getInstance(RESTUserInterface.class);
            }
        });
    }
}

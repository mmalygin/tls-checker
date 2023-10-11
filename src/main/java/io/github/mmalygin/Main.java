package io.github.mmalygin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Option OPT_HELP = Option.builder().option("h").longOpt("help").desc("Print help.").build();
    private static final Option OPT_TIMEOUT = Option.builder()
            .option("t")
            .longOpt("timeout")
            .hasArg()
            .type(Integer.class)
            .argName("num-seconds")
            .desc("Connect timeout in seconds (default is 10 seconds).")
            .build();
    private static final Option OPT_DEBUG = Option.builder()
            .option("d")
            .longOpt("debug")
            .hasArg()
            .argName("value")
            .desc("Value for javax.net.debug property. For example: all or ssl,handshake,verbose,trustmanager")
            .build();
    private static final Options OPTIONS = new Options().addOption(OPT_HELP)
            .addOption(OPT_TIMEOUT)
            .addOption(OPT_DEBUG);
    private static final Pattern HOSTPORT_PATTERN = Pattern.compile("^([\\w.]+):(\\d+)$");

    private static void usage() {
        new HelpFormatter().printHelp("tls-checker [options] <hostname:port>", OPTIONS);
    }

    public static void main(String[] args) {
        String hostname;
        int port;
        int timeoutSecs = 10;
        //System.setProperty("javax.net.debug", "ssl,handshake,verbose,trustmanager");

        try {
            // parse the command line arguments
            DefaultParser parser = new DefaultParser();
            CommandLine line = parser.parse(OPTIONS, args);
            if (line.hasOption(OPT_HELP) || line.getArgList().isEmpty()) {
                usage();
                return;
            }
            if (line.hasOption(OPT_TIMEOUT)) {
                timeoutSecs = (int) line.getParsedOptionValue(OPT_TIMEOUT);
                if (timeoutSecs < 1) {
                    System.out.println("Error: bad timeout");
                    usage();
                    return;
                }
            }
            if (line.hasOption(OPT_DEBUG)) {
                String value = line.getOptionValue(OPT_DEBUG);
                if (value != null) {
                    System.setProperty("javax.net.debug", value);
                }
            }
            String arg = line.getArgList().get(0);
            Matcher m = HOSTPORT_PATTERN.matcher(arg);
            if (!m.find()) {
                System.out.println("Error: bad hostname:port");
                usage();
                return;
            }
            hostname = m.group(1);
            try {
                port = Integer.parseInt(m.group(2));
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: bad port");
                usage();
                return;
            }

            Checker checker = new Checker(hostname, port, timeoutSecs);
            checker.check();

        } catch (ParseException exp) {
            System.out.println("Unexpected exception: " + exp.getMessage());
        }
    }
}

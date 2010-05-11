package wikinet.mapping;

import org.apache.commons.cli.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import wikinet.Service;

/**
 * @author shyiko
 * @since May 8, 2010
 */
public class Main {

    private static final String CLIENT = "client";
    private static final String SERVER = "server";

    private static void printUsage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth( 80 );
        helpFormatter.printHelp("mapper", options);
    }

    public static void main(String[] args) {
        CommandLineParser cmdParser = new BasicParser();
        Options options = new Options();
        Option option = new Option("m", "mode", true, CLIENT + "|" + SERVER);
        option.setRequired(true);
        options.addOption(option);

        CommandLine commandLine = null;
        try {
            commandLine = cmdParser.parse(options, args);
        } catch (ParseException ex) {
            printUsage(options);
            System.exit(0);
        }

        String mode = commandLine.getOptionValue("m");
        if (!mode.equals(CLIENT) && !mode.equals(SERVER)) {
            printUsage(options);
            System.exit(0);
        }

        String bean = mode.equals(CLIENT) ? "synsetConsumerService" : "synsetProducerService";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-mapper-module.xml");

        Service service = (Service) context.getBean(bean);
        service.start();
    }

}
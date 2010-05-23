package wikinet.wordnet2db;

import org.apache.commons.cli.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author shyiko
 * @since Mar 3, 2010
 */
public class Main {

    private static void printUsage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth( 80 );
        helpFormatter.printHelp("wordnet2db", options);
    }

    public static void main(String[] args) {
        CommandLineParser cmdParser = new BasicParser();
        Options options = new Options();
        Option option = new Option("d", "directory", true, "wordnet dict directory");
        option.setRequired(true);
        options.addOption(option);

        CommandLine commandLine = null;
        try {
            commandLine = cmdParser.parse(options, args);
        } catch (ParseException ex) {
            printUsage(options);
            System.exit(0);
        }

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-w2db-mudule.xml");

        WordNet2DB wordNet2DB = (WordNet2DB) context.getBean("wordNet2DB");
        try {
            wordNet2DB.importDirectory(commandLine.getOptionValue("d"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}

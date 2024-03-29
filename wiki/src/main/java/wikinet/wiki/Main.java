package wikinet.wiki;

import org.apache.commons.cli.*;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import wikinet.db.DBController;
import wikinet.db.MySQLDBController;
import wikinet.wiki.parser.ParserSettings;
import wikinet.wiki.parser.WikiXMLParser;

/**
 * @author shyiko
 * @since Apr 10, 2010
 */
public class Main {

    private static void printUsage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth( 80 );
        helpFormatter.printHelp("wiki", options);
    }

    public static void main(String[] args) {
        CommandLineParser cmdParser = new BasicParser();
        Options options = new Options();
        Option option = new Option("f", "file", true, "wiki xml dump bz2 archive");
        option.setRequired(true);
        options.addOption(option);
        options.addOption(new Option("s", "start", true, "skip all pages before this one"));
        options.addOption(new Option("c", "cleandb", false, "clean database before import"));

        CommandLine commandLine = null;
        try {
            commandLine = cmdParser.parse(options, args);
        } catch (ParseException ex) {
            printUsage(options);
            System.exit(0);
        }

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-wiki-module.xml");

        if (commandLine.hasOption("c")) {
            DBController dbController = (DBController) context.getBean("dBController");
            dbController.cleanDB();
        }

        WikiXMLParser wikiParser = (WikiXMLParser) context.getBean("wikiXMLParser");
        try {
            wikiParser.importFile(commandLine.getOptionValue("f"),
                    new ParserSettings(commandLine.getOptionValue("s")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

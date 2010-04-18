package wikinet.wiki;

import org.apache.commons.cli.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import wikinet.wiki.parser.WikiXMLParser;

/**
 * @author shyiko
 * @since Apr 10, 2010
 */
public class Main {

    private static void printUsage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter( );
        helpFormatter.setWidth( 80 );
        helpFormatter.printHelp("[-f <wiki-dump-bz2-archive>]", options);
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser cmdParser = new BasicParser();
        Options options = new Options();
        Option option = new Option("f", "file", true, "wiki xml dump bz2 archive");
        option.setRequired(true);
        options.addOption(option);

        CommandLine commandLine = null;
        try {
            commandLine = cmdParser.parse(options, args);
        } catch (ParseException ex) {
            printUsage(options);
            System.exit(0);
        }

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-wiki-module.xml");
        WikiXMLParser wikiParser = (WikiXMLParser) context.getBean("wikiXMLParser");
        wikiParser.importFile(commandLine.getOptionValue("f"));
    }
}

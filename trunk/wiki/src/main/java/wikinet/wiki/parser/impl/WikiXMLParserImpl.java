package wikinet.wiki.parser.impl;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;
import org.apache.tools.bzip2.CBZip2InputStream;
import wikinet.wiki.parser.PageProcessor;
import wikinet.wiki.parser.ParserSettings;
import wikinet.wiki.parser.WikiXMLParser;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;

/**
 * @author shyiko
 * @since Mar 29, 2010
 */
public class WikiXMLParserImpl implements WikiXMLParser {

    private static Logger logger = Logger.getLogger(WikiXMLParserImpl.class);

    private PageProcessor pageProcessor;

    public WikiXMLParserImpl(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    @Override
    public void importFile(String fileName) throws Exception {
        importFile(new File(fileName), null);
    }

    @Override
    public void importFile(File file) throws Exception {
        importFile(file, null);
    }

    @Override
    public void importFile(String fileName, ParserSettings parserSettings) throws Exception {
        importFile(new File(fileName), parserSettings);
    }

    @Override
    public void importFile(File file, ParserSettings parserSettings) throws Exception {
        if (parserSettings == null)
            parserSettings = ParserSettings.DEFAULT;
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        FileInputStream fileInputStream = new FileInputStream(file);
        long totalFileSize = file.length();
        // read first 2 bytes "BZ" because of bug in CBZip2InputStream.
        fileInputStream.read();
        fileInputStream.read();
        CBZip2InputStream bz2InputStream = new CBZip2InputStream(fileInputStream);
        CountingInputStream inputStream = new CountingInputStream(bz2InputStream);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream, "utf8");
        if (logger.isInfoEnabled()) {
            logger.info("Importing file " + file.getCanonicalPath() + "...");
            if (parserSettings.getStartWord() != null)
                logger.info("Skipping all pages before \"" + parserSettings.getStartWord() + "\"...");
        }
        long beginTime = System.currentTimeMillis();
        try {
            int i = 1;
            long pagesCount = 0;
            boolean startWordMet = false;
            while (reader.hasNext()) {
                if (!findOpeningTag(reader, "page"))
                    break;
                i++;
                pagesCount++;
                if (i > 1000) {
                    i = 1;
                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("Imported %1$d mb (%2$d pages) in %3$.2f minutes.",
                                inputStream.getByteCount() >> 20,
                                pagesCount,
                                ((System.currentTimeMillis() - beginTime) / 1000 / 60.0)));
                    }
                }
                if (!findOpeningTag(reader, "title")) {
                    logger.warn("Couldn't find a title inside page tag. Skipping page...");
                    continue;
                }
                String title = reader.getElementText();
                if (title == null || title.isEmpty()) {
                    logger.warn("Title inside page tag is empty. Skipping page...");
                }

                if (!startWordMet && parserSettings.getStartWord() != null) {
                    if (!parserSettings.getStartWord().equals(title)) {
                        continue;
                    }
                    startWordMet = true;
                }

                findOpeningTag(reader, "text");
                String text = reader.getElementText();

                if (logger.isDebugEnabled())
                    logger.debug("Processing page \"" + title + "\"");

                pageProcessor.process(title, text);

                findClosingTag(reader, "page");
            }
        } finally {
            try {
                reader.close();
            } finally {
                inputStream.close();
            }
        }
        //todo: check whether categories graph has cycles  
    }

    private boolean findOpeningTag(XMLStreamReader reader, String tagName) throws XMLStreamException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement()) {
                if (reader.getLocalName().equals(tagName))
                    return true;
            }
        }
        return false;
    }

    private void findClosingTag(XMLStreamReader reader, String tagName) throws XMLStreamException, ParseException {
        while (reader.hasNext()) {
            reader.next();
            if (reader.isEndElement()) {
                if (reader.getLocalName().equals(tagName))
                    return;
            }
        }
        throw new ParseException("Couldn't find closing tag for \"" + tagName + "\".", 0);
    }

}

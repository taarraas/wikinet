package wikinet.wiki.parser.impl;

import org.testng.annotations.Test;
import wikinet.wiki.parser.PageBuilder;
import wikinet.wiki.parser.WikiXMLParser;

import static org.mockito.Mockito.*;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class WikiXMLParserTest {

    @Test
    public void testImportFile() throws Exception {
        PageBuilder pageBuilderMock = mock(PageBuilder.class);

        WikiXMLParser wikiXMLParser = new WikiXMLParserImpl(pageBuilderMock);
        wikiXMLParser.importFile("src/test/resources/wiki-dump.xml.bz2");

        verify(pageBuilderMock).importPage(eq("AccessibleComputing"), eq("#REDIRECT [[Computer accessibility]] {{R from CamelCase}}"));
    }

}

package wikinet.wiki;

import org.testng.annotations.Test;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.impl.WikiXMLParserImpl;

import static org.mockito.Mockito.*;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class WikiXMLParserTest {

    @Test
    public void testImportFile() throws Exception {
        PageDao pageDao = mock(PageDao.class);

        WikiXMLParserImpl parser = new WikiXMLParserImpl(pageDao);
        parser.importFile("wiki/src/test/resources/wiki-dump.xml.bz2");

        verify(pageDao, times(1)).save(any(Page.class));
    }
}

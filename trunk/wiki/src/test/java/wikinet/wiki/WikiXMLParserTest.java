package wikinet.wiki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.WikiXMLParser;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */

@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class WikiXMLParserTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WikiXMLParser parser;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private LocalizedPageDao localizedPageDao;

    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void testImportFile() throws Exception {
        try {
            parser.importFile("wiki/src/test/resources/wiki-dump.xml.bz2");
        } finally {
            for (Page page : pageDao.findAll()) {
                pageDao.delete(page);
            }
            for (LocalizedPage page : localizedPageDao.findAll()) {
                localizedPageDao.delete(page);
            }
            for (Category category : categoryDao.findAll()) {
                categoryDao.delete(category);
            }
        }
    }

}

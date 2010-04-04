package wikinet.wiki.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import wikinet.db.model.Locale;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.domain.LocalizedPage;

/**
 * @author shyiko
 * @since Apr 4, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class LocalizedPageDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private LocalizedPageDao localizedPageDao;

    @Test
    public void testSave() throws Exception {
        LocalizedPage localizedPage = new LocalizedPage("title", Locale.POL);
        localizedPageDao.save(localizedPage);
        LocalizedPage byId = localizedPageDao.findById("title");
        Assert.assertNotNull(byId);
        Assert.assertEquals(byId.getTitle(), "title");
        Assert.assertEquals(byId.getLocale(), Locale.POL);
        Assert.assertEquals(localizedPageDao.findAll().size(), 1);
    }

    @Test
    public void testDelete() throws Exception {
        LocalizedPage localizedPage = new LocalizedPage("title", Locale.POL);
        localizedPageDao.save(localizedPage);
        localizedPageDao.delete(localizedPage);
        Assert.assertEquals(localizedPageDao.findAll().size(), 0);
    }

}

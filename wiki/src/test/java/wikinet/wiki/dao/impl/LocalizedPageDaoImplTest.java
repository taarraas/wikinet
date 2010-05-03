package wikinet.wiki.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wikinet.db.model.Locale;
import wikinet.testng.SpringDaoTest;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.LocalizedPagePK;
import wikinet.wiki.domain.Page;

/**
 * @author shyiko
 * @since Apr 4, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class LocalizedPageDaoImplTest extends SpringDaoTest {

    @Autowired
    private LocalizedPageDao localizedPageDao;

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        sessionFactory.getCurrentSession().beginTransaction();
        for (LocalizedPage localizedPage : localizedPageDao.findAll()) {
            localizedPageDao.delete(localizedPage);
        }
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    @Test
    public void testSave() throws Exception {
        LocalizedPage localizedPage = new LocalizedPage("title", Locale.POL);
        localizedPageDao.save(localizedPage);
        LocalizedPage byId = localizedPageDao.findById(new LocalizedPagePK("title", Locale.POL));
        Assert.assertNotNull(byId);
        Assert.assertEquals(byId.getTitle(), "title");
        Assert.assertEquals(byId.getLocale(), Locale.POL);
        localizedPageDao.findAll().size();
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

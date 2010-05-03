package wikinet.wiki.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wikinet.testng.SpringDaoTest;
import wikinet.wiki.dao.LinkedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.Page;

/**
 * @author shyiko
 * @since May 2, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class LinkedPageDaoImplTest extends SpringDaoTest {

    @Autowired
    private PageDao pageDao;

    @Autowired
    private LinkedPageDao linkedPageDao;

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        sessionFactory.getCurrentSession().beginTransaction();
        for (LinkedPage linkedPage : linkedPageDao.findAll()) {
            linkedPageDao.delete(linkedPage);
        }
        for (Page page : pageDao.findAll()) {
            pageDao.delete(page);
        }
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    @Test
    public void testFindById() throws Exception {
        Page page = pageDao.saveOrUpdate("word", "disam");
        LinkedPage linkedPage = new LinkedPage(1, 2, page);
        linkedPageDao.save(linkedPage);
        LinkedPage lp = linkedPageDao.findById(linkedPage.getId());
        Assert.assertNotNull(lp);
        Assert.assertEquals(lp.getStartPos(), 1);
        Assert.assertEquals(lp.getLength(), 2);
        Assert.assertEquals(lp.getPage().getId(), page.getId());
    }

    @Test
    public void testFindAll() throws Exception {
        Page page = pageDao.saveOrUpdate("word", "disam");
        linkedPageDao.save(new LinkedPage(1, 2, page));
        linkedPageDao.save(new LinkedPage(2, 3, page));
        Assert.assertEquals(linkedPageDao.findAll().size(), 2);
    }

    @Test
    public void testDelete() throws Exception {
        Page page = pageDao.saveOrUpdate("word", "disam");
        LinkedPage lp = linkedPageDao.save(new LinkedPage(1, 2, page));
        linkedPageDao.delete(lp);
        Assert.assertEquals(linkedPageDao.findAll().size(), 0);
    }
    
}

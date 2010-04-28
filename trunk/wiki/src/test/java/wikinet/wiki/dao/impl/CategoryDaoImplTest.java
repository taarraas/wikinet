package wikinet.wiki.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.domain.Category;

/**
 * @author shyiko
 * @since Apr 4, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class CategoryDaoImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private SessionFactory sessionFactory;

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        Session session = sessionFactory.openSession();
        for (Category category : categoryDao.findAll()) {
            categoryDao.delete(category);
        }
//        session.flush();
        session.close();
    }

//    @Test
    public void testSave() throws Exception {
        Category category = new Category("title");
        categoryDao.save(category);
        Category byId = categoryDao.findById(category.getId());
        Assert.assertNotNull(byId);
        Assert.assertEquals(byId.getName(), "title");
        Assert.assertEquals(categoryDao.findAll().size(), 1);
    }

    @Test
    public void testDelete() throws Exception {
        Category category = new Category("title");
        categoryDao.save(category);
        categoryDao.delete(category);
        Assert.assertEquals(categoryDao.findAll().size(), 0);
    }


}

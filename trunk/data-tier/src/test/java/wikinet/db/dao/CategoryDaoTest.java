package wikinet.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wikinet.db.domain.Category;

/**
 * @author shyiko
 * @since Apr 4, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-dao-module-test.xml"})
public class CategoryDaoTest extends SpringDaoTest {

    @Autowired
    private CategoryDao categoryDao;

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        sessionFactory.getCurrentSession().beginTransaction();
        for (Category category : categoryDao.findAll()) {
            categoryDao.delete(category);
        }
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    @Test
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
package wikinet.wiki.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.domain.Category;

/**
 * @author shyiko
 * @since Apr 4, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class CategoryDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void testSave() throws Exception {
        Category category = new Category("title");
        categoryDao.save(category);
        Category byId = categoryDao.findById("title");
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

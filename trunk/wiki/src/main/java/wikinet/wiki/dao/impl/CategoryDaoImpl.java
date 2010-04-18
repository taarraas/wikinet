package wikinet.wiki.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import wikinet.db.dao.impl.GenericDaoImpl;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.domain.Category;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class CategoryDaoImpl extends GenericDaoImpl<Category, Long> implements CategoryDao {

    @Override
    public Category findByName(String name) {
        Session session = getSession(true);
        Query queryObject = session.getNamedQuery("Category.findByName");
        queryObject.setString("name", name);
        Category category = (Category) queryObject.uniqueResult();
        session.close();
        return category;
    }

    @Override
    public Category createIfNotExist(String name) {
        Category category = findByName(name);
        if (category == null) {
            category = new Category(name);
            Session session = getSession(true);
            session.save(category);
            session.close();
        }
        return category;
    }

}


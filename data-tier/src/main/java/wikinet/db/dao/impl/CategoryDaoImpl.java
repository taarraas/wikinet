package wikinet.db.dao.impl;

import wikinet.db.dao.CategoryDao;
import wikinet.db.domain.Category;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class CategoryDaoImpl extends GenericDaoImpl<Category, Long> implements CategoryDao {

    @Override
    public Category findByName(String name) {
        return (Category) getSession().getNamedQuery("Category.findByName")
                .setString("name", name).uniqueResult();
    }

    @Override
    public Category saveOrUpdate(String name) {
        Category category = findByName(name);
        if (category == null) {
            category = new Category(name);
            getSession().save(category);
        }
        return category;
    }

}


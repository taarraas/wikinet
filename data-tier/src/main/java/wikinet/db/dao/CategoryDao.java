package wikinet.db.dao;

import wikinet.db.domain.Category;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public interface CategoryDao extends GenericDao<Category, Long> {

    Category findByName(String name);
    Category saveOrUpdate(String name);
    void addSubcategory(Category parent, Category subcategory);

}


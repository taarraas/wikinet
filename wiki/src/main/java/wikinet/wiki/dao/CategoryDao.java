package wikinet.wiki.dao;

import wikinet.db.dao.GenericDao;
import wikinet.wiki.domain.Category;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public interface CategoryDao extends GenericDao<Category, Long> {

    Category findByName(String name);
    Category saveOrUpdate(String name);

}


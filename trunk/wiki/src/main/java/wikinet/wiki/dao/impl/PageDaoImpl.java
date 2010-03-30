package wikinet.wiki.dao.impl;

import wikinet.db.dao.impl.GenericDaoImpl;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Page;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class PageDaoImpl extends GenericDaoImpl<Page, String> implements PageDao {

    @Override
    public Page findById(String id) {
        return super.findById(id.toLowerCase());
    }

    @Override
    public void save(Page obj) {
        obj.setTitle(obj.getTitle().toLowerCase());
        super.save(obj);
    }
}

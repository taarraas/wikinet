package wikinet.wiki.dao;

import wikinet.db.dao.GenericDao;
import wikinet.db.model.Locale;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.LocalizedPagePK;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public interface LocalizedPageDao extends GenericDao<LocalizedPage, LocalizedPagePK> {

    public LocalizedPage createIfNotExist(String title, Locale locale);

}


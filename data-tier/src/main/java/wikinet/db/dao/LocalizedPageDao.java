package wikinet.db.dao;

import wikinet.db.domain.LocalizedPage;
import wikinet.db.domain.LocalizedPagePK;
import wikinet.db.model.Locale;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public interface LocalizedPageDao extends GenericDao<LocalizedPage, LocalizedPagePK> {

    LocalizedPage saveOrUpdate(String title, Locale locale);
    
}


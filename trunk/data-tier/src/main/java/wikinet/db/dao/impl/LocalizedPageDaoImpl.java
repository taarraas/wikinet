package wikinet.db.dao.impl;

import wikinet.db.dao.LocalizedPageDao;
import wikinet.db.domain.LocalizedPage;
import wikinet.db.domain.LocalizedPagePK;
import wikinet.db.model.Locale;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class LocalizedPageDaoImpl extends GenericDaoImpl<LocalizedPage, LocalizedPagePK> implements LocalizedPageDao {

    @Override
    public LocalizedPage saveOrUpdate(String title, Locale locale) {
        LocalizedPage localizedPage = findById(new LocalizedPagePK(title, locale));
        if (localizedPage == null) {
            localizedPage = new LocalizedPage(title, locale);
            getSession().save(localizedPage);
        }
        return localizedPage;
    }

}

package wikinet.wiki.dao.impl;

import wikinet.db.dao.impl.GenericDaoImpl;
import wikinet.db.model.Locale;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.LocalizedPagePK;

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

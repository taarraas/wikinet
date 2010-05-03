package wikinet.wiki.dao;

import wikinet.db.dao.GenericDao;
import wikinet.db.model.Locale;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;

import java.util.List;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public interface PageDao extends GenericDao<Page, Long> {

    List<Page> findByWord(String word);
    Page findByWordAndDisambiguation(String word, String disambiguation);
    Page saveOrUpdate(String word, String disambiguation);
    void addRedirect(Page page, Page redirect);
    void addLinkedPage(Page page, LinkedPage linkedPage);
    void addCategory(Page page, Category category);
    void addLocalizedPage(Page page, LocalizedPage localizedPage);

}

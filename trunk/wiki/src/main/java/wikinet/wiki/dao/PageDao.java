package wikinet.wiki.dao;

import wikinet.db.dao.GenericDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;

import java.util.List;
import wikinet.db.model.Locale;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public interface PageDao extends GenericDao<Page, Long> {

    List<Page> findByWord(String word);
    Page findByWordAndDisambiguation(String word, String disambiguation);
    Page createIfNotExist(String word, String disambiguation);
    boolean addRedirect(Page page, Page redirect);
    boolean addLinkedPage(Page page, LinkedPage linkedPage);
    boolean addCategory(Page page, Category category);
    boolean addLocalizedPage(Page page, String title, Locale locale);

}

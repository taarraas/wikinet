package wikinet.db.dao;

import wikinet.db.domain.Category;
import wikinet.db.domain.LinkedPage;
import wikinet.db.domain.LocalizedPage;
import wikinet.db.domain.Page;

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
    List<Long> findWithoutSynsets();

}

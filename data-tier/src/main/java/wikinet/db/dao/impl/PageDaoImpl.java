package wikinet.db.dao.impl;

import wikinet.db.dao.PageDao;
import wikinet.db.domain.Category;
import wikinet.db.domain.LinkedPage;
import wikinet.db.domain.LocalizedPage;
import wikinet.db.domain.Page;

import java.util.List;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class PageDaoImpl extends GenericDaoImpl<Page, Long> implements PageDao {

    @Override
    public List<Page> findByWord(String word) {
        return getSession().getNamedQuery("Page.findByWord")
                .setString("word", word).list();
    }

    @Override
    public List<Long> findWithoutSynsets() {
        return getSession().getNamedQuery("Page.findWithoutSynsets").list();
    }

    @Override
    public Page findByWordAndDisambiguation(String word, String disambiguation) {
        if (disambiguation == null) {
            return (Page) getSession().getNamedQuery("Page.findByWordWithNullDisambiguation")
                    .setString("word", word).uniqueResult();
        }
        return (Page) getSession().getNamedQuery("Page.findByWordAndDisambiguation")
                .setString("word", word).setString("disambiguation", disambiguation).uniqueResult();
    }

    @Override
    public Page saveOrUpdate(String word, String disambiguation) {
        Page page = findByWordAndDisambiguation(word, disambiguation);
        if (page == null) {
            page = new Page(word, disambiguation);
            getSession().save(page);
        }
        return page;
    }

    @Override
    public void addRedirect(Page page, Page redirect) {
        getSession().createSQLQuery("insert into Page_Redirect values (?, ?)")
                .setLong(0, page.getId()).setLong(1, redirect.getId()).executeUpdate();
    }

    @Override
    public void addLinkedPage(Page page, LinkedPage linkedPage) {
        getSession().createSQLQuery("insert into Page_LinkedPage values (?, ?)")
                .setLong(0, page.getId()).setLong(1, linkedPage.getId()).executeUpdate();
    }

    @Override
    public void addCategory(Page page, Category category) {
        getSession().createSQLQuery("insert into Page_Category values (?, ?)")
                .setLong(0, page.getId()).setLong(1, category.getId()).executeUpdate();
    }

    @Override
    public void addLocalizedPage(Page page, LocalizedPage localizedPage) {
        getSession().createSQLQuery("insert into Page_LocalizedPage values (?, ?, ?)")
                .setLong(0, page.getId())
                .setString(1, localizedPage.getTitle())
                .setString(2, localizedPage.getLocale().toString())
                .executeUpdate();
    }

}

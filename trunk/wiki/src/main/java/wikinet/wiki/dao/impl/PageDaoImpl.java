package wikinet.wiki.dao.impl;

import wikinet.db.dao.impl.GenericDaoImpl;
import wikinet.wiki.dao.PageDao;
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
public class PageDaoImpl extends GenericDaoImpl<Page, Long> implements PageDao {

    @Override
    public List<Page> findByWord(String word) {
        return getSession().getNamedQuery("Page.findByWord")
                .setString("word", word).list();
    }

    @Override
    public Page findByWordAndDisambiguation(String word, String disambiguation) {
        if (disambiguation==null) {
            return (Page) getSession().getNamedQuery("Page.findByWordAndDisambiguationNull")
                .setString("word", word).uniqueResult();
        } else {
            return (Page) getSession().getNamedQuery("Page.findByWordAndDisambiguation")
                .setString("word", word).setString("disambiguation", disambiguation).uniqueResult();
        }
    }

    @Override
    public Page createIfNotExist(String word, String disambiguation) {
        Page page = findByWordAndDisambiguation(word, disambiguation);
        if (page == null) {
            page = new Page(word, disambiguation);
            getSession().save(page);
        }
        return page;
    }

    @Override
    public boolean addRedirect(Page page, Page redirect) {
        boolean notExist = getSession().createSQLQuery("select page_id from Page_Redirect where page_id = ? and redirect_page_id = ?")
                .setLong(0, page.getId()).setLong(1, redirect.getId()).uniqueResult() == null;
        if (notExist) {
            getSession().createSQLQuery("insert into Page_Redirect values (?, ?)")
                .setLong(0, page.getId()).setLong(1, redirect.getId()).executeUpdate();
        }
        return notExist;
    }

    @Override
    public boolean addLinkedPage(Page page, LinkedPage linkedPage) {
        boolean notExist = getSession().createSQLQuery("select page_id from Page_LinkedPage where page_id = ? and linked_page_id = ?")
                .setLong(0, page.getId()).setLong(1, linkedPage.getId()).uniqueResult() == null;
        if (notExist) {
            getSession().createSQLQuery("insert into Page_LinkedPage values (?, ?)")
                .setLong(0, page.getId()).setLong(1, linkedPage.getId()).executeUpdate();
        }
        return notExist;
    }

    @Override
    public boolean addCategory(Page page, Category category) {
        boolean notExist = getSession().createSQLQuery("select page_id from Page_Category where page_id = ? and category_id = ?")
                .setLong(0, page.getId()).setLong(1, category.getId()).uniqueResult() == null;
        if (notExist) {
            getSession().createSQLQuery("insert into Page_Category values (?, ?)")
                .setLong(0, page.getId()).setLong(1, category.getId()).executeUpdate();
        }
        return notExist;
    }

    @Override
    public boolean addLocalizedPage(Page page, String title, Locale locale) {
        boolean notExist = getSession().createSQLQuery("select page_id from LocalizedPage where " +
                "title = ? and locale = ?")
                .setString(0, title)
                .setString(1, locale.toString())
                .uniqueResult() == null;
        
        if (notExist) {
            getSession().createSQLQuery("insert into LocalizedPage values (?, ?)")
                .setString(0, title)
                .setString(1, locale.toString())
                .executeUpdate();
        }
        getSession().createSQLQuery("insert into Page_LocalizedPage values (?, ?, ?)")
            .setLong(0, page.getId())
            .setString(1, title)
            .setString(2, locale.toString())
            .executeUpdate();
        return notExist;
    }

}

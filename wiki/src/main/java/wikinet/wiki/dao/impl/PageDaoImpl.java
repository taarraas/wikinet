package wikinet.wiki.dao.impl;

import org.hibernate.Session;
import wikinet.db.dao.impl.GenericDaoImpl;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;

import java.util.List;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public class PageDaoImpl extends GenericDaoImpl<Page, Long> implements PageDao {

    @Override
    public List<Page> findByWord(String word) {
        List<Page> result = getHibernateTemplate().findByNamedQueryAndNamedParam("Page.findByWord", "word", word);
        return result;
    }

    @Override
    public Page findByWordAndDisambiguation(String word, String disambiguation) {
        List result = getHibernateTemplate().findByNamedQueryAndNamedParam("Page.findByWordAndDisambiguation",
                new String[]{"word", "disambiguation"},
                new Object[]{word, disambiguation});
        Page page = (Page) (result.isEmpty() ? null : result.get(0));        
/*
        Session session = getSession(true);
        Query queryObject = session.getNamedQuery("Page.findByWordAndDisambiguation");
        queryObject.setString("word", word);
        queryObject.setString("disambiguation", disambiguation);
        Page page = (Page) queryObject.uniqueResult();
        session.close();
*/
        return page;
    }

    @Override
    public Page createIfNotExist(String word, String disambiguation) {
        Page page = findByWordAndDisambiguation(word, disambiguation);
        if (page == null) {
            page = new Page(word, disambiguation);
            Session session = getSession(true);
            session.save(page);
            session.close();
        }
        return page;
    }

    @Override
    public boolean addRedirect(Page page, Page redirect) {
        Session session = getSession(true);
        boolean notExist = session.createSQLQuery("select page_id from Page_Redirect where page_id = ? and redirect_page_id = ?")
                .setLong(0, page.getId()).setLong(1, redirect.getId()).uniqueResult() == null;
        if (notExist) {
            session.createSQLQuery("insert into Page_Redirect values (?, ?)")
                .setLong(0, page.getId()).setLong(1, redirect.getId()).executeUpdate();
        }
        session.close();
        return notExist;
    }

    @Override
    public boolean addLinkedPage(Page page, LinkedPage linkedPage) {
        Session session = getSession(true);
        boolean notExist = session.createSQLQuery("select page_id from Page_LinkedPage where page_id = ? and linked_page_id = ?")
                .setLong(0, page.getId()).setLong(1, linkedPage.getId()).uniqueResult() == null;
        if (notExist) {
            session.createSQLQuery("insert into Page_LinkedPage values (?, ?)")
                .setLong(0, page.getId()).setLong(1, linkedPage.getId()).executeUpdate();
        }
        session.close();
        return notExist;
    }

    @Override
    public boolean addCategory(Page page, Category category) {
        Session session = getSession(true);
        boolean notExist = session.createSQLQuery("select page_id from Page_Category where page_id = ? and category_id = ?")
                .setLong(0, page.getId()).setLong(1, category.getId()).uniqueResult() == null;
        if (notExist) {
            session.createSQLQuery("insert into Page_Category values (?, ?)")
                .setLong(0, page.getId()).setLong(1, category.getId()).executeUpdate();
        }
        session.close();
        return notExist;
    }

    @Override
    public boolean addLocalizedPage(Page page, LocalizedPage localizedPage) {
        Session session = getSession(true);
        boolean notExist = session.createSQLQuery("select page_id from Page_LocalizedPage where page_id = ? and " +
                "localizedPage_title = ? and localizedPage_locale = ?")
                .setLong(0, page.getId())
                .setString(1, localizedPage.getTitle())
                .setString(2, localizedPage.getLocale().toString())
                .uniqueResult() == null;
        if (notExist) {
            session.createSQLQuery("insert into Page_LocalizedPage values (?, ?, ?)")
                .setLong(0, page.getId())
                .setString(1, localizedPage.getTitle())
                .setString(2, localizedPage.getLocale().toString())
                .executeUpdate();
        }
        session.close();
        return notExist;
    }

}

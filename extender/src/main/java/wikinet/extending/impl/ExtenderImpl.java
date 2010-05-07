package wikinet.extending.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.PageDao;
import wikinet.db.dao.SynsetDao;
import wikinet.db.dao.WordDao;
import wikinet.db.domain.Page;
import wikinet.db.domain.Synset;
import wikinet.db.model.SynsetType;
import wikinet.extending.ConnectionsMaker;
import wikinet.extending.Extender;

import java.util.List;

/**
 * @author taras, shyiko
 */
public class ExtenderImpl implements Extender {

    @Autowired
    private List<ConnectionsMaker> connectionsMakers;

    @Autowired
    private WordDao wordDao;

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private SessionFactory sessionFactory;

    public void setConnectionsMakers(List<ConnectionsMaker> connectionsMakers) {
        this.connectionsMakers = connectionsMakers;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setWordDao(WordDao wordDao) {
        this.wordDao = wordDao;
    }

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void extend(long pageId) {
        sessionFactory.getCurrentSession().beginTransaction();
        Page page = pageDao.findById(pageId);
        SynsetType synsetType = null; // todo: taras
        Synset synset = new Synset(page.getFirstParagraph(), synsetType);
        synset.addPage(page);
        for (Page rp : page.getRedirects()) {
            synset.addWord(wordDao.findById(rp.getWord()));
        }
        for (ConnectionsMaker connectionsMaker : connectionsMakers) {
            connectionsMaker.addConnections(synset, page);
        }
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

}

package wikinet.mapping;

import org.hibernate.SessionFactory;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.db.dao.PageDao;
import wikinet.db.domain.Page;

import java.util.*;

/**
 * @author taras, shyiko
 */
public class MapperImpl implements Mapper {

    private double minTrustLevel = 0.6;
    private double minTrustDif = 1;

    private SynsetDao synsetDao;

    private PageDao pageDao;

    private SessionFactory sessionFactory;

    private Collection<Voter> voters;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setVoters(Collection<Voter> voters) {
        this.voters = voters;
    }

    public void setMinTrustLevel(double minTrustLevel) {
        this.minTrustLevel = minTrustLevel;
    }

    public void setMinTrustDif(double minTrustDif) {
        this.minTrustDif = minTrustDif;
    }

    /**
     * Automatic synset mapping
     *
     * @param synsetId
     * @return true, if mapped to wiki article
     */
    public boolean map(long synsetId) {
        Map<Page, Double> bestRelatedArticles = getBestRelatedArticles(synsetId);
        Iterator<Map.Entry<Page, Double>> it = bestRelatedArticles.entrySet().iterator();
        Map.Entry<Page, Double> first = it.next();
        if (first.getValue() < minTrustLevel) {
            return false;
        }
        if (!it.hasNext()) {
            mapSynset(synsetId, first.getKey());
            return true;
        }
        Map.Entry<Page, Double> second = it.next();
        if (first.getValue() - second.getValue() < minTrustDif) {
            return false;
        }
        mapSynset(synsetId, first.getKey());
        return true;
    }

    /**
     * Manually map sysnet to given article
     *
     * @param synsetId
     * @param article
     */
    private void mapSynset(long synsetId, Page article) {
        sessionFactory.getCurrentSession().beginTransaction();
        try {
            Synset synset = synsetDao.findById(synsetId);
            Page page = new Page(article.getWord(), article.getDisambiguation());
            synset.addPage(page);
            synsetDao.save(synset);
            sessionFactory.getCurrentSession().getTransaction().commit();
        } finally {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

    /**
     * @param synsetId
     * @return sorted collection of pairs article name and it's mark. The first one is with best mark
     */
    private Map<Page, Double> getBestRelatedArticles(long synsetId) {
        Synset synset = synsetDao.findById(synsetId);
        Set<Page> articles = new TreeSet<Page>();
        for (Word word : synset.getWords()) {
            List<Page> list = pageDao.findByWord(word.getWord());
            for (Page page : list) {
                articles.add(new Page(page.getWord(), page.getDisambiguation()));
            }

        }
        Map<Page, Double> map = new HashMap<Page, Double>();
        for (Page pagePrototype : articles) {
            map.put(pagePrototype, getOverallVote(synsetId, pagePrototype));
        }

        SortedMap<Page, Double> sortedMap = new TreeMap<Page, Double>(new Comparator<Page>() {

            private Map<Page, Double> map;

            public Comparator<? super Page> setMap(Map<Page, Double> map) {
                this.map = map;
                return this;
            }

            @Override
            public int compare(Page o1, Page o2) {
                return -1 * map.get(o1).compareTo(map.get(o2));
            }

        }.setMap(map));
        sortedMap.putAll(map);
        return sortedMap;
    }

    private double getOverallVote(long synsetId, Page page) {
        sessionFactory.getCurrentSession().beginTransaction();
        Synset synset = synsetDao.findById(synsetId);
        try {
            double total = 0.0;
            for (Voter voter : voters) {
                total = Math.max(voter.getVote(synset, page), total);
                if (total >= 1) {
                    return 1;
                }
            }
            return total;
        } finally {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }
}

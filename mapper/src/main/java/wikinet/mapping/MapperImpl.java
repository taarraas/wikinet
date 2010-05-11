package wikinet.mapping;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.Mapper;
import wikinet.mapping.SynsetArticleVoter;
import wikinet.db.dao.PageDao;
import wikinet.db.domain.Page;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.*;

/**
 * @author taras, shyiko
 */
public class MapperImpl implements Mapper {

    private double minTrustLevel = 0.6;
    private double minTrustDif =1;

    private SynsetDao synsetDao;

    private PageDao pageDao;

    private SessionFactory sessionFactory;

    private Collection<SynsetArticleVoter> voters;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setVoters(Collection<SynsetArticleVoter> voters) {
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
        Map<PagePrototype, Double> bestRelatedArticles = getBestRelatedArticles(synsetId);
        Iterator<Map.Entry<PagePrototype,Double>> it = bestRelatedArticles.entrySet().iterator();
        Map.Entry<PagePrototype, Double> first = it.next();
        if (first.getValue() < minTrustLevel) {
            return false;
        }
        if (!it.hasNext()) {
            mapSynset(synsetId, first.getKey());
            return true;
        }
        Map.Entry<PagePrototype, Double> second = it.next();
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
    private void mapSynset(long synsetId, PagePrototype article) {
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
    private Map<PagePrototype, Double> getBestRelatedArticles(long synsetId) {
        Synset synset = synsetDao.findById(synsetId);
        Set<PagePrototype> articles = new TreeSet<PagePrototype>();
        for (Word word : synset.getWords()) {
            List<Page> list = pageDao.findByWord(word.getWord());
            for (Page page : list) {
                articles.add(new PagePrototype(page.getWord(), page.getDisambiguation()));
            }

        }
        Map<PagePrototype, Double> map = new HashMap<PagePrototype, Double>();
        for (PagePrototype pagePrototype : articles) {
            map.put(pagePrototype, getOverallVote(synsetId, pagePrototype));
        }

        SortedMap<PagePrototype, Double> sortedMap = new TreeMap<PagePrototype, Double>(new Comparator<PagePrototype>() {

            private Map<PagePrototype, Double> map;

            public Comparator<? super PagePrototype> setMap(Map<PagePrototype, Double> map) {
                this.map = map;
                return this;
            }

            @Override
            public int compare(PagePrototype o1, PagePrototype o2) {
                return -1 * map.get(o1).compareTo(map.get(o2));
            }

        }.setMap(map));
        sortedMap.putAll(map);
        return sortedMap;
    }

    private double getOverallVote(long synsetId, PagePrototype article) {
        sessionFactory.getCurrentSession().beginTransaction();
        try {
            double total = 0.0;
            for (SynsetArticleVoter voter : voters) {
                total = Math.max(voter.getVote(synsetId, article), total);
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

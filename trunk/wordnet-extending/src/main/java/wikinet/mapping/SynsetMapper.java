package wikinet.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Article;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.*;

/**
 * @author taras, shyiko
 */
public abstract class SynsetMapper {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private Collection<SynsetArticleVoter> voters;

    private double minLevel = 0.6;
    private double minDif=1;

    public void setMinimalTrust(double minLevel, double minDif) {
        this.minLevel = minLevel;
        this.minDif = minDif;
    }

    /**
     * Automatic synset mapping
     *
     * @param synsetId
     * @return true, if mapped to wiki article
     */
    public boolean mapSynset(long synsetId) {
        Map<PagePrototype, Double> bestRelatedArticles = getBestRelatedArticles(synsetId);
        Iterator<Map.Entry<PagePrototype,Double>> it = bestRelatedArticles.entrySet().iterator();
        Map.Entry<PagePrototype, Double> first = it.next();
        if (first.getValue() < minLevel) {
            return false;
        }
        if (!it.hasNext()) {
            mapSynset(synsetId, first.getKey());
            return true;
        }
        Map.Entry<PagePrototype, Double> second = it.next();
        if (first.getValue() - second.getValue() < minDif) {
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
    public void mapSynset(long synsetId, PagePrototype article) {
        Synset synset = synsetDao.findById(synsetId);
        String link = null; // TODO:taras
        Article articleEntity = new Article(article.getWord(), link);
        articleEntity.setDisambiguation(article.getDisambiguation());
        synset.addArticle(articleEntity);
        synsetDao.save(synset);
    }

    /**
     * @param synsetId
     * @return sorted collection of pairs article name and it's mark. The first one is with best mark
     */
    public Map<PagePrototype, Double> getBestRelatedArticles(long synsetId) {
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
        double total = 0.0;
        for (SynsetArticleVoter voter : voters) {
            total = Math.max(voter.getVote(synsetId, article), total);
            if (total >= 1) {
                return 1;
            }
        }
        return total;
    }
}

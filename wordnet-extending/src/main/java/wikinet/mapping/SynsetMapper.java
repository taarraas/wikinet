package wikinet.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Article;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.ArticleReference;
import wikinet.wiki.WikiDao;

import java.util.*;

/**
 * @author taras, shyiko
 */
public abstract class SynsetMapper {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WikiDao wikiDao;

    @Autowired
    private Collection<SynsetArticleVoter> voters;

    private double minLevel;
    private double minDif;

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
        Map<ArticleReference, Double> bestRelatedArticles = getBestRelatedArticles(synsetId);
        Iterator<Map.Entry<ArticleReference,Double>> it = bestRelatedArticles.entrySet().iterator();
        Map.Entry<ArticleReference, Double> first = it.next();
        if (first.getValue() < minLevel) {
            return false;
        }
        if (!it.hasNext()) {
            mapSynset(synsetId, first.getKey());
            return true;
        }
        Map.Entry<ArticleReference, Double> second = it.next();
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
    public void mapSynset(long synsetId, ArticleReference article) {
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
    public Map<ArticleReference, Double> getBestRelatedArticles(long synsetId) {
        Synset synset = synsetDao.findById(synsetId);
        Set<ArticleReference> articles = new TreeSet<ArticleReference>();
        for (Word word : synset.getWords()) {
            articles.addAll(wikiDao.disambiguate(word.getWord()));
        }
        Map<ArticleReference, Double> map = new HashMap<ArticleReference, Double>();
        for (ArticleReference articleReference : articles) {
            map.put(articleReference, getOverallVote(synsetId, articleReference));
        }

        SortedMap<ArticleReference, Double> sortedMap = new TreeMap<ArticleReference, Double>(new Comparator<ArticleReference>() {

            private Map<ArticleReference, Double> map;

            public Comparator<? super ArticleReference> setMap(Map<ArticleReference, Double> map) {
                this.map = map;
                return this;
            }

            @Override
            public int compare(ArticleReference o1, ArticleReference o2) {
                return -1 * map.get(o1).compareTo(map.get(o2));
            }

        }.setMap(map));
        sortedMap.putAll(map);
        return sortedMap;
    }

    private double getOverallVote(long synsetId, ArticleReference article) {
        double total = 0.0;
        for (SynsetArticleVoter voter : voters) {
            total += voter.getVote(synsetId, article);
        }
        return total / voters.size();
    }
}

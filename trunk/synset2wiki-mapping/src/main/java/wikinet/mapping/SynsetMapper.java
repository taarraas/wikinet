/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.mapping;

import java.util.*;
import wikinet.wiki.WikiQueries;

/**
 *
 * @author taras
 */
public abstract class SynsetMapper {
    double minimallevel,
            minimaldifference;

    public void setMinimalTrust(double minimallevel, double minimaldifference) {
        this.minimallevel = minimallevel;
        this.minimaldifference = minimaldifference;
    }

    SynsetArticleVoter voter;

    public SynsetMapper(SynsetArticleVoter voter) {
        this.voter = voter;
    }

    /**
     * Automatic synset mapping
     * @param synsetId 
     * @return true, if mapped to wiki article
     */
    public boolean mapSynset(long synsetId) {
        Collection<Map.Entry<String, Double>> best = getBestArticles(synsetId);
        Iterator<Map.Entry<String, Double>> it=best.iterator();
        Map.Entry<String, Double> first=it.next();
        if (first.getValue()<minimallevel) {
            return false;
        }
        if (!it.hasNext()) {
            mapSynset(synsetId, first.getKey());
            return true;
        }
        Map.Entry<String, Double> second=it.next();
        if (first.getValue()-second.getValue()<minimaldifference) {
            return false;
        }
        mapSynset(synsetId, first.getKey());
        return true;
    }

    /**
     * Manually map sysnet to given article
     * @param synsetId
     * @param article
     */
    public void mapSynset(long synsetId, String article) {
        //TODO
    }

    /**
     *
     * @param synsetId
     * @return sorted collection of pairs article name and it's mark. The first one is with best mark
     */
    public Collection<Map.Entry<String, Double>> getBestArticles(long synsetId) {
        Collection<String> wordsOfSynset = null; // TODO
        WikiQueries wikiQueries = null;     // TODO
        Set<String> articles=new TreeSet<String>();
        for (String string : wordsOfSynset) {
            articles.addAll(wikiQueries.disambiguate(string));
        }
        TreeSet<Map.Entry<String, Double>> ret = new TreeSet<Map.Entry<String, Double>>(new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o1.getValue()==o2.getValue()) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return (o1.getValue()<o2.getValue())?-1:1;
            }
        });
        for (String string : articles) {
            ret.add(new AbstractMap.SimpleEntry<String, Double>(string,
                    voter.getVote(synsetId, string)));
        }
        return ret;
    }
}

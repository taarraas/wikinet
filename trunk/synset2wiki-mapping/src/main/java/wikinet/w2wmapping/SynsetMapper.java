/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.w2wmapping;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
        Collection<String> wordsOfSynset;
    }
}

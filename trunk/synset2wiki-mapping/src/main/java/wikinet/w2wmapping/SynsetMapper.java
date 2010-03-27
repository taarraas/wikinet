/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.w2wmapping;

/**
 *
 * @author taras
 */
public interface SynsetMapper {
    /**
     * Automatic synset mapping
     * @param synsetId 
     * @return true, if mapped to wiki article
     */
    public boolean mapSynset(long synsetId);

    
}

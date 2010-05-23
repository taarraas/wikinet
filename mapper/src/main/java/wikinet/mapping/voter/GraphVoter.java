/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.mapping.voter;

import wikinet.db.domain.Page;
import wikinet.db.domain.Synset;
import wikinet.mapping.Voter;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.Set;

/**
 *
 * @author taras
 */
public class GraphVoter implements Voter {

    /**
     * Get synsets linked with Antonym, Hypernym, Instance Hypernym, 
     * Member holonym, Substance holonym, Part holonym connection type
     * and wordsTo == 0
     */
    Set<Synset> getLinkedSynsets(Synset synset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get words linked with Antonym, Hypernym, Instance Hypernym,
     * Member holonym, Substance holonym, Part holonym connection type
     * Synset.words[Connection.wordsTo-1];
     */
    Set<String> getLinkedWords(Synset synset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    int countWordsInNearContext(Set<String> words, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    int countAllWords(Set<String> words, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    int countWordsInArticle(Set<String> words, Page page) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getVote(Synset synset, Page page) {
        Set<String> words = getLinkedWords(synset);
        double wordsInArticle=(double)countWordsInArticle(words, page)/words.size();
        
        Set<Synset> synsets = getLinkedSynsets(synset);
        return wordsInArticle;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.mapping.voters.impl;

import java.util.Set;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.ArticleReference;

/**
 *
 * @author taras
 */
public class GraphVoter implements SynsetArticleVoter {

    /**
     * Get synsets linked with Antonym, Hypernym, Instance Hypernym, 
     * Member holonym, Substance holonym, Part holonym connection type
     * and wordsTo == 0
     * @param synsetId
     * @return
     */
    Set<Synset> getLinkedSynsets(long synsetId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get words linked with Antonym, Hypernym, Instance Hypernym,
     * Member holonym, Substance holonym, Part holonym connection type
     * Synset.words[Connection.wordsTo-1];
     * @param synsetId
     * @return
     */
    Set<String> getLinkedWords(long synsetId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    int countWordsInNearContext(Set<String> words, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    int countAllWords(Set<String> words, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    int countWordsInArticle(Set<String> words, ArticleReference article) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getVote(long synsetId, ArticleReference article) {
        Set<String> words = getLinkedWords(synsetId);
        double wordsInArticle=(double)countWordsInArticle(words, article)/words.size();
        
        Set<Synset> synsets = getLinkedSynsets(synsetId);
        return wordsInArticle;
    }

}

package wikinet.mapping.voters.impl;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.StringReader;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.mapping.MappingUtils;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.ArticleReference;
import wikinet.wiki.dao.WikiDao;

import java.util.*;
import wikinet.mapping.MappingConstants;

/**
 * @author taras, shyiko
 */
public class DescriptionVoter implements SynsetArticleVoter {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WikiDao wikiDao;

    @Autowired
    private MappingUtils mappingUtils;

    private static final double MINIMALWEIGHT = 2.;
    private static final int PARSEONLYFIRSTMSENTENCES = 2;
    public final static Map<String, Double> WEIGHTS = new TreeMap<String, Double>();
    static {
        WEIGHTS.put("NN", 0.7);
        WEIGHTS.put("NNP", 1.4);
        WEIGHTS.put("NNS", 0.7);
        
        WEIGHTS.put("ADJ", 0.4);

        WEIGHTS.put("JJS", 0.4);
        WEIGHTS.put("JJ", 0.4);

        WEIGHTS.put("VBD", 1.1);
        WEIGHTS.put("VBS", 0.7);
    }

    Map<String, Double> getWords(String text) {
        Map<String, Double> ret = new TreeMap<String, Double>();
        List<Sentence<? extends HasWord>> sentences =
            MaxentTagger.tokenizeText(new StringReader(text));
        int cnt=0;
        for (Sentence<? extends HasWord> sentence : sentences) {
            Sentence<TaggedWord> tSentence = MaxentTagger.tagSentence(sentence);
            for (TaggedWord taggedWord : tSentence) {
                String word = taggedWord.word(),
                        value = taggedWord.value(),
                        tag = taggedWord.tag();
                if (WEIGHTS.containsKey(tag)) {
                    ret.put(word.toLowerCase(), WEIGHTS.get(tag));
                }
            }
            if (++cnt == PARSEONLYFIRSTMSENTENCES) {
                break;
            }
        }
        ret.remove("is");
        ret.remove("are");
        return ret;
    }
    
    @Override
    public double getVote(long synsetId, ArticleReference article) {
        Synset synset = synsetDao.findById(synsetId);
        String descriptionSynset = synset.getDescription();
        String descriptionWiki = wikiDao.getDescription(article);
        Map<String, Double> synsetWords = getWords(descriptionSynset),
                wikiWords = getWords(descriptionWiki);
        double wordsInCommon = mappingUtils.sizeOfSet(mappingUtils.intersect(synsetWords, wikiWords));
        //double wordsTotal = mappingUtils.union(synsetWords, wikiWords).size();
        return wordsInCommon/MINIMALWEIGHT*MappingConstants.MINIMALLEVEL;
    }

}

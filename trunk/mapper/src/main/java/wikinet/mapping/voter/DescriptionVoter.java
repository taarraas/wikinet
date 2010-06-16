package wikinet.mapping.voter;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.PageDao;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Page;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.Voter;
import wikinet.mapping.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author shyiko, taras
 */
public class DescriptionVoter implements Voter {
    static {
        try {
            new MaxentTagger("/home/taras/j2dev/wikinet/libs/left3words-wsj-0-18.tagger");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private Utils utils;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
    }

    private static final double MINIMALWEIGHT = 2.;
    private static final int PARSEONLYFIRSTMSENTENCES = 2;
    private static final double MINIMALLEVEL = 2./3;
    
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

    @Override
    public double getVote(Synset synset, Page page) {
        String descriptionSynset = synset.getDescription();
        String descriptionWiki = page.getFirstParagraph();
        Map<String, Double> synsetWords = getWords(descriptionSynset),
                wikiWords = getWords(descriptionWiki);
        for (Word word:synset.getWords()) {
            synsetWords.remove(word.getWord());
        }
        double wordsInCommon = utils.sizeOfSet(utils.intersect(synsetWords, wikiWords));
        return wordsInCommon/MINIMALWEIGHT*MINIMALLEVEL;
    }

    private Map<String, Double> getWords(String text) {
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

}

package wikinet.mapping.voters.impl;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.mapping.MappingConstants;
import wikinet.mapping.MappingUtils;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author taras, shyiko
 */
public class DescriptionVoter implements SynsetArticleVoter {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private MappingUtils mappingUtils;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setMappingUtils(MappingUtils mappingUtils) {
        this.mappingUtils = mappingUtils;
    }

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
    public double getVote(long synsetId, PagePrototype pagePrototype) {
        Synset synset = synsetDao.findById(synsetId);
        String descriptionSynset = synset.getDescription();
        Page page = pageDao.findByWordAndDisambiguation(pagePrototype.getWord(), pagePrototype.getDisambiguation());
        String descriptionWiki = page.getFirstParagraph();
        Map<String, Double> synsetWords = getWords(descriptionSynset),
                wikiWords = getWords(descriptionWiki);
        double wordsInCommon = mappingUtils.sizeOfSet(mappingUtils.intersect(synsetWords, wikiWords));
        //double wordsTotal = mappingUtils.union(synsetWords, wikiWords).size();
        return wordsInCommon/MINIMALWEIGHT*MappingConstants.MINIMALLEVEL;
    }

}

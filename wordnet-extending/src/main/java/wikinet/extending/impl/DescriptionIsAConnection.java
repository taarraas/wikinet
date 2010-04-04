package wikinet.extending.impl;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.StringReader;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.extending.ConnectionsMaker;
import wikinet.wiki.ArticleReference;
import wikinet.wiki.dao.WikiDao;

/**
 * First sentence of article says, that * %title% is a %something% *
 * Find what means %something%, synset for it, and add connection.
 *
 * @author taras, shyiko
 */
public class DescriptionIsAConnection implements ConnectionsMaker {
    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WikiDao wikiDao;
    private enum State{
        beforeIs, afterIs, inPG, resultFound
    }
    static MaxentTagger tagger;
    static {
        try {
            tagger = new MaxentTagger("src/main/resources/bidirectional-distsim-wsj-0-18.tagger");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public static String getMainWord(String text) {
        Sentence<TaggedWord> res=new Sentence<TaggedWord>(),
                of=new Sentence<TaggedWord>();
        List<Sentence<? extends HasWord>> sentences =
        MaxentTagger.tokenizeText(new StringReader(text));
        Sentence sentence=sentences.iterator().next();
        Sentence<TaggedWord> tSentence = MaxentTagger.tagSentence(sentence);
        State state=State.beforeIs;
        for (TaggedWord taggedWord : tSentence) {
            switch(state) {
                case beforeIs: {
                    if (taggedWord.word().equalsIgnoreCase("is")) {
                        state = State.afterIs;
                    }
                    break;
                }
                case afterIs: {
                    if (taggedWord.tag().equals("IN")) {
                        state = State.inPG;
                        break;
                    }
                    res.add(taggedWord);
                    if (taggedWord.tag().equals("NN")) {
                        state = State.resultFound;
                    }
                    break;
                }
                case inPG: {
                    if (taggedWord.tag().equals("NN")) {
                        state = State.afterIs;
                        res.clear();
                    }
                    break;
                }
                case resultFound: {
                    if (taggedWord.word().equalsIgnoreCase("of")) {
                        
                    }
                    break;
                }
            }
        }
        return res.toString();
    }
    /**
     * 
     * @param synset
     * @param article
     */
    @Override
    public void addConnections(Synset synset, ArticleReference article) {
        String descriptionText="LOL, an abbreviation for laughing out loud, laugh out " +
                "loud or sometimes lots of laughs, is a common element of Internet slang.";
    
    }

}

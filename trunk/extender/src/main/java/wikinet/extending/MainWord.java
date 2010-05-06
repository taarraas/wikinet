package wikinet.extending;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.extending.Utils;

/**
 * First sentence of article says, that * %title% is a %something% *
 * Find what means %something%, synset for it, and add connection.
 *
 * @author taras, shyiko
 */
public class MainWord {

    @Autowired
    private SynsetDao synsetDao;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    private enum State{
        before, afterIs, inPG, resultFound, resultAndOfFound
    }
    public static final String MAINWORD="MAINWORD",
            PARTOF="PARTOF";
    static MaxentTagger tagger;
    static {
        try {
            tagger = new MaxentTagger("src/main/resources/bidirectional-distsim-wsj-0-18.tagger");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * returns main word
     * @param text
     * @return
     */
    public Map<String, String> getInfo(String text) {
        text = text.replaceAll("\\([^\\)]+\\)", "");
        Sentence<TaggedWord> mainWord=new Sentence<TaggedWord>(),
                of=new Sentence<TaggedWord>();
        List<Sentence<? extends HasWord>> sentences =
        MaxentTagger.tokenizeText(new StringReader(text));
        Sentence sentence=sentences.iterator().next();
        Map<String, String> info = new HashMap<String, String>();
        Sentence<TaggedWord> tSentence = MaxentTagger.tagSentence(sentence);
        State state=State.before;
        for (TaggedWord taggedWord : tSentence) {
            switch(state) {
                case before: {
                    if (Utils.TOBE.contains(taggedWord.word().toLowerCase())) {
                        state = State.afterIs;
                    } else if (taggedWord.tag().equals("VB")){
                        return info;
                    }
                    break;
                }
                case afterIs: {
                    if (taggedWord.tag().equals("IN")) {
                        state = State.inPG;
                        break;
                    }
                    mainWord.add(taggedWord);
                    if (taggedWord.tag().startsWith("NN")) {
                        state = State.resultFound;
                    }
                    break;
                }
                case inPG: {
                    if (taggedWord.tag().equals("NN")) {
                        state = State.afterIs;
                        mainWord.clear();
                    }
                    break;
                }
                case resultFound: {
                    info.put(MAINWORD, mainWord.toString());
                    if (taggedWord.word().equalsIgnoreCase("of")) {
                        state = State.resultAndOfFound;
                    } else {
                        return info;
                    }
                    break;
                }
                case resultAndOfFound: {
                    of.add(taggedWord);
                    if (taggedWord.tag().startsWith("NN")) {
                        info.put(PARTOF, of.toString());
                        return info;
                    }
                }
            }
        }
        return info;
    }    
}

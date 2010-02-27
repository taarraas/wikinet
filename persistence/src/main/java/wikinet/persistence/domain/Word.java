package wikinet.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
public class Word {

    @Id
    private String word;
    @ManyToMany
    private List<Synset> synsets;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}

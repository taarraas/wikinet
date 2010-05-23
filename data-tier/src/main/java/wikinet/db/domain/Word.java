package wikinet.db.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
public class Word {

    @Id
    private String word;

    @ManyToMany(mappedBy = "words")
    private Set<Synset> synsets = new HashSet<Synset>();

    protected Word() {
    }

    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public Set<Synset> getSynsets() {
        return synsets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word1 = (Word) o;

        if (!word.equals(word1.word)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                '}';
    }
}

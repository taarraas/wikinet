package wikinet.persistence.domain;

import wikinet.persistence.model.SynsetType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
public class Synset {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SynsetType type;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Word> words = new HashSet<Word>();

    @OneToOne
    private Article article;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SynsetType getType() {
        return type;
    }

    public void setType(SynsetType type) {
        this.type = type;
    }

    public Set<Word> getWords() {
        return words;
    }

    public void addWord(Word word) {
        this.words.add(word);
        word.getSynsets().add(this);
    }

    public void removeWord(Word word) {
        this.words.remove(word);
        word.getSynsets().remove(this);
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Synset synset = (Synset) o;

        if (id != synset.id) return false;
        if (!description.equals(synset.description)) return false;
        if (type != synset.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + description.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Synset{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }
}

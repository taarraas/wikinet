package wikinet.persistence.domain;

import wikinet.persistence.model.SynsetType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = "Synset.getConnected",
                    query = "select c.secondSynset from Connection c where c.firstSynsetId = :synsetId")
})

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

    private String offset;
    private String lexFileNum;

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

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLexFileNum() {
        return lexFileNum;
    }

    public void setLexFileNum(String lexFileNum) {
        this.lexFileNum = lexFileNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Synset synset = (Synset) o;

        if (id != synset.id) return false;
        if (description != null ? !description.equals(synset.description) : synset.description != null) return false;
        if (lexFileNum != null ? !lexFileNum.equals(synset.lexFileNum) : synset.lexFileNum != null) return false;
        if (offset != null ? !offset.equals(synset.offset) : synset.offset != null) return false;
        if (type != synset.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (offset != null ? offset.hashCode() : 0);
        result = 31 * result + (lexFileNum != null ? lexFileNum.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Synset{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", offset='" + offset + '\'' +
                ", lexFileNum='" + lexFileNum + '\'' +
                '}';
    }
}

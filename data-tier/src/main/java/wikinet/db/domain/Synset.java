package wikinet.db.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import wikinet.db.model.SynsetType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = "Synset.getConnected",
                    query = "select c.secondSynset from Connection c where c.firstSynsetId = :synsetId"),
        @NamedQuery(name = "Synset.getConnectedWithConnectionType",
                    query = "select c.secondSynset from Connection c where c.firstSynsetId = :synsetId " +
                            "and c.connectionType = :connectionType")
})

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
public class Synset {

    @Id
    @GenericGenerator(name = "SynsetIdGenerator", strategy = "wikinet.db.domain.SynsetIdGenerator")
    @GeneratedValue(generator = "SynsetIdGenerator")
    private long id;
    transient long idPreInit;

    @Lob
    @Column(nullable = false)
    @Basic(fetch = FetchType.LAZY)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SynsetType type;

    @ManyToMany
    @IndexColumn(name = "wordOrder", base = 1)
    @JoinTable(
            name = "SynsetWord",
            joinColumns = {@JoinColumn(name = "id")},
            inverseJoinColumns = {@JoinColumn(name = "word")}
    )
    private List<Word> words = new LinkedList<Word>();

    @ManyToMany
    private Set<Page> pages = new HashSet<Page>();

    private String lexFileNum;

    protected Synset() {
    }

    public Synset(String description, SynsetType type) {
        this.description = description;
        this.type = type;
    }

    public Synset(long id, String description, SynsetType type) {
        this.idPreInit = id;
        this.description = description;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public SynsetType getType() {
        return type;
    }

    public List<Word> getWords() {
        return this.words;
    }

    public void addWord(Word word) {
        this.words.add(word);
    }

    public void removeWord(Word word) {
        this.words.remove(word);
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public Set<Page> getPages() {
        return pages;
    }

    public void addPage(Page page) {
        this.pages.add(page);
    }

    public void removePage(Page page) {
        this.pages.remove(page);
    }

    public void setPages(Set<Page> pages) {
        this.pages = pages;
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
        if (lexFileNum != null ? !lexFileNum.equals(synset.lexFileNum) : synset.lexFileNum != null) return false;
        if (type != synset.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (lexFileNum != null ? lexFileNum.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Synset{" +
                "id=" + id +
                ", type=" + type +
                ", lexFileNum='" + lexFileNum + '\'' +
                '}';
    }
}

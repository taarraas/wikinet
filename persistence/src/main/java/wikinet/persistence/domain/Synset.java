package wikinet.persistence.domain;

import wikinet.persistence.model.SynsetType;

import javax.persistence.*;
import java.util.List;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
public class Synset {

    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private SynsetType type;
    @Column(nullable = false)
    @ManyToMany(mappedBy = "synsets")
    private List<Word> words;
    @OneToMany(mappedBy = "id")
    private List<Connection> connections;
    @OneToOne(optional = false)
    private Article article;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}

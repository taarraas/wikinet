package wikinet.persistence.domain;

import wikinet.persistence.domain.LocalizedArticle;

import javax.persistence.*;
import java.util.List;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
public class Article {

    @Id
    private int id;
    @Column(nullable = false)
    private String word;
    private String disambiguation;
    @Column(nullable = false)
    private String link;
    @OneToMany(cascade = CascadeType.ALL)
    private List<LocalizedArticle> localizedArticles; 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

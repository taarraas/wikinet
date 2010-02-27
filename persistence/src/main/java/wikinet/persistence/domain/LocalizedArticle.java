package wikinet.persistence.domain;

import wikinet.persistence.model.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
public class LocalizedArticle {

    @Id
    private int id;
    @ManyToOne(optional = false)
    private Article article;
    @Column(nullable = false)
    private Locale locale;
    @Column(nullable = false)
    private String word;
    private String disambiguation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
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
}

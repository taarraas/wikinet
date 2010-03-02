package wikinet.persistence.domain;

import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
public class Article {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String word;

    private String disambiguation;

    @Column(nullable = false)
    private String link;

    @Embedded
    @CollectionOfElements
    private Set<LocalizedArticle> localizedArticles = new HashSet<LocalizedArticle>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Set<LocalizedArticle> getLocalizedArticles() {
        return localizedArticles;
    }

    public void addLocalizedArticles(LocalizedArticle localizedArticle) {
        this.localizedArticles.add(localizedArticle);
    }

    public void removeLocalizedArticles(LocalizedArticle localizedArticle) {
        this.localizedArticles.remove(localizedArticle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (disambiguation != null ? !disambiguation.equals(article.disambiguation) : article.disambiguation != null)
            return false;
        if (!link.equals(article.link)) return false;
        if (!word.equals(article.word)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = word.hashCode();
        result = 31 * result + (disambiguation != null ? disambiguation.hashCode() : 0);
        result = 31 * result + link.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}

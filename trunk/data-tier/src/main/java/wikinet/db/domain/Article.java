package wikinet.db.domain;

import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@NamedQueries(value = {
        @NamedQuery(name = "Article.getByWordAndDisambiguation", 
        query = "select a from Article a where a.word = :word and a.disambiguation = :disambiguation")
})

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"word", "disambiguation"})
})
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

    @ManyToMany(mappedBy = "articles")
    private Set<Synset> synsets = new HashSet<Synset>();

    protected Article() {
    }

    public Article(String word, String link) {
        this.word = word;
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public String getWord() {
        return word;
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

    public Set<LocalizedArticle> getLocalizedArticles() {
        return localizedArticles;
    }

    public void addLocalizedArticles(LocalizedArticle localizedArticle) {
        this.localizedArticles.add(localizedArticle);
    }

    public void removeLocalizedArticles(LocalizedArticle localizedArticle) {
        this.localizedArticles.remove(localizedArticle);
    }

    public void setLocalizedArticles(Set<LocalizedArticle> localizedArticles) {
        this.localizedArticles = localizedArticles;
    }

    public Set<Synset> getSynsets() {
        return synsets;
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

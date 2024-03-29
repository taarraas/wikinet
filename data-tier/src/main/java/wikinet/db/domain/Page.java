package wikinet.db.domain;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = "Page.findByWord",
        query = "select p from Page p where p.word = :word"),
        @NamedQuery(name = "Page.findByWordAndDisambiguation",
        query = "select p from Page p where p.word = :word and p.disambiguation = :disambiguation"),
        @NamedQuery(name = "Page.findByWordWithNullDisambiguation",
        query = "select p from Page p where p.word = :word and p.disambiguation is null"),
        @NamedQuery(name = "Page.findWithoutSynsets",
        query = "select p.id from Page p where p.synsets is empty")
})

/**
 * @author shyiko
 * @since Mar 29, 2010
 */
@Entity
/*
@org.hibernate.annotations.Table(appliesTo = "Page",
        indexes = @Index(name="PageWordDisambiguationIDX", columnNames = {"word", "disambiguation"}))
*/
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"word", "disambiguation"}))
public class Page {

    @Id
    @GeneratedValue
    private long id;

    @Index(name = "PageWordIDX")
    @Column(nullable = false, length = 120)
    private String word;

    @Column(length = 120)
    private String disambiguation;

    @Lob
    private String firstParagraph;

    /**
     * Text without first paragraph
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 16777215) // use mediumtext column definition in mysql
    private String text;

    /**
     * All pages that redirect to this one
     */
    @OneToMany
    @JoinTable(name = "Page_Redirect",
        joinColumns = @JoinColumn(name = "page_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "redirect_page_id", referencedColumnName = "id"))
    private Set<Page> redirects = new HashSet<Page>();

    /**
     * All linked pages
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Page_LinkedPage",
        joinColumns = @JoinColumn(name = "page_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "linked_page_id", referencedColumnName = "id"))
    private Set<LinkedPage> linkedPages = new HashSet<LinkedPage>();

    /**
     * Page categories
     */
    @ManyToMany
    @JoinTable(name = "Page_Category",
        joinColumns = @JoinColumn(name = "page_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private List<Category> categories = new LinkedList<Category>();

    /**
     * Same page but in the other language
     */
    // NOTE: Theoretically, here should be OneToMany connection, but due to wiki ambiguity using ManyToMany 
    @ManyToMany
    @JoinTable(name = "Page_LocalizedPage",
        joinColumns = @JoinColumn(name = "page_id", referencedColumnName = "id"),
        inverseJoinColumns = {
                @JoinColumn(name = "localizedPage_title", referencedColumnName = "title"),
                @JoinColumn(name = "localizedPage_locale", referencedColumnName = "locale")
        })
    private List<LocalizedPage> localizedPages = new LinkedList<LocalizedPage>();

    @ManyToMany(mappedBy = "pages")
    private Set<Synset> synsets = new HashSet<Synset>();

    protected Page() {
    }

    public Page(String word, String disambiguation) {
        this.word = word;
        this.disambiguation = disambiguation;
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

    public String getFirstParagraph() {
        return firstParagraph;
    }

    public void setFirstParagraph(String firstParagraph) {
        this.firstParagraph = firstParagraph;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addRedirect(Page redirect) {
        this.redirects.add(redirect);
    }

    public void addLinkedPage(LinkedPage page) {
        this.linkedPages.add(page);
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public void addLocalizedPage(LocalizedPage localizedPage) {
        this.localizedPages.add(localizedPage);
    }

    public Set<Page> getRedirects() {
        return redirects;
    }

    public Set<LinkedPage> getLinkedPages() {
        return linkedPages;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<LocalizedPage> getLocalizedPages() {
        return localizedPages;
    }

    public Set<Synset> getSynsets() {
        return synsets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        if (disambiguation != null ? !disambiguation.equals(page.disambiguation) : page.disambiguation != null)
            return false;
        if (!word.equals(page.word)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = word.hashCode();
        result = 31 * result + (disambiguation != null ? disambiguation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                '}';
    }
}

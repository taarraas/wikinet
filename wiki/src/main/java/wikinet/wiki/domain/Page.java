package wikinet.wiki.domain;

import org.hibernate.annotations.Index;
import org.hibernate.lob.ClobImpl;
import wikinet.db.Utils;

import javax.persistence.*;
import java.sql.Clob;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = "Page.findByWord",
        query = "select p from Page p where p.word = :word"),
        @NamedQuery(name = "Page.findByWordAndDisambiguation",
        query = "select p from Page p where p.word = :word and p.disambiguation = :disambiguation"),
        @NamedQuery(name = "Page.findByWordAndDisambiguationNull",
        query = "select p from Page p where p.word = :word and isNull(disambiguation)")
})

/**
 * @author shyiko
 * @since Mar 29, 2010
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"word", "disambiguation"}))
public class Page {

    @Id
    @GeneratedValue
    private long id;

    @Index(name = "wordIndex")
    @Column(nullable = false, length = 120)
    private String word;

    @Column(length = 120)
    private String disambiguation;

    @Column(columnDefinition = "text")
    private String firstParagraph;

    /**
     * Text without first paragraph
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "longblob")
    private Clob text;

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
    @OneToMany
    @JoinTable(name = "Page_LocalizedPage",
        joinColumns = @JoinColumn(name = "page_id", referencedColumnName = "id"),
        inverseJoinColumns = {
                @JoinColumn(name = "localizedPage_title", referencedColumnName = "title"),
                @JoinColumn(name = "localizedPage_locale", referencedColumnName = "locale")
        })
    private List<LocalizedPage> localizedPages = new LinkedList<LocalizedPage>();

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
        return Utils.getInstance().getStringFromClob(text);
    }

    public void setText(String text) {
        if (text == null)
            this.text = null;
        else
            this.text = new ClobImpl(text);
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
}

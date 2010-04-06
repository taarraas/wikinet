package wikinet.wiki.domain;

import org.hibernate.lob.ClobImpl;
import wikinet.db.Utils;

import javax.persistence.*;
import java.sql.Clob;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author shyiko
 * @since Mar 29, 2010
 */
@Entity
public class Page {

    /**
     * Text between &lt;title&gt;&lt;/title&gt;
     */
    @Id
    private String title;

    /**
     * First paragraph of text between tags &lt;text&gt;&lt;/text&gt;
     */
    private String paragraph;

    /**
     * Text between tags &lt;text&gt;&lt;/text&gt; without first paragraph
     */
    @Basic(fetch = FetchType.LAZY)
    private Clob text;

    /**
     * All pages that redirect to this one
     */
    @OneToMany
    @JoinTable(name = "Page_Redirect",
        joinColumns = @JoinColumn(name = "page_title", referencedColumnName = "title"),
        inverseJoinColumns = @JoinColumn(name = "redirect_page_title", referencedColumnName = "title"))
    private Set<Page> redirects = new HashSet<Page>();

    /**
     * All linked pages being gotten from<br>
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Page_LinkedPage",
        joinColumns = @JoinColumn(name = "page_title", referencedColumnName = "title"),
        inverseJoinColumns = @JoinColumn(name = "linked_page_title", referencedColumnName = "id"))
    private Set<LinkedPage> linkedPages = new HashSet<LinkedPage>();

    /**
     * Page categories
     */
    @ManyToMany
    private List<Category> categories = new LinkedList<Category>();

    /**
     * Same page but in some other language
     */
    @OneToMany
    private List<LocalizedPage> localizedPages = new LinkedList<LocalizedPage>();

    protected Page() {
    }

    public Page(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public String getText() {
        return Utils.getInstance().getStringFromClob(text);
    }

    public void setText(String text) {
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

        if (paragraph != null ? !paragraph.equals(page.paragraph) : page.paragraph != null) return false;
        if (title != null ? !title.equals(page.title) : page.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (paragraph != null ? paragraph.hashCode() : 0);
        return result;
    }
}

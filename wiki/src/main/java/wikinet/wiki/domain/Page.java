package wikinet.wiki.domain;

import javax.persistence.*;
import java.sql.Clob;
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
     * All linked pages being gotten from<br>
     * {{for|the anthology of anarchist writings|Anarchism: A Documentary History of Libertarian Ideas}}<br>
     * {{redirect|Anarchist|the fictional character|Anarchist (comics)}}<br>
     * {{redirect|Anarchists}}.<br>
     * Last parts stands for links.
     */
    @OneToMany
    private Set<Page> redirects;

    /**
     * All linked pages being gotten from<br>
     * &lt;!--FOOTERS--&gt;<br>
     * {{Philosophy topics}}<br>
     * ...
     */
    @OneToMany
    private Set<Page> footers;

    /**
     * Page linked categories being gotten from<br>
     * &lt;!--CATEGORIES--&gt;<br>
     * [[Category:Anarchism]]<br>
     * ...
     */
    @ManyToMany
    private List<Category> categories;

    /**
     * Pages in other languages being gotten from<br>
     * &lt;!--LANGUAGES--&gt;<br>
     * [[pl:Anarchizm]]<br>
     * ...
     */
    @OneToMany
    private List<LocalizedPage> localizedPages;

    public Page(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public Clob getText() {
        return text;
    }

    public void setText(Clob text) {
        this.text = text;
    }

    public void addRedirect(Page redirect) {
        this.redirects.add(redirect);
    }

    public void addFooter(Page footer) {
        this.footers.add(footer);
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

    public Set<Page> getFooters() {
        return footers;
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

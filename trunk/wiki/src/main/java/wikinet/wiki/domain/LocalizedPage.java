package wikinet.wiki.domain;

import wikinet.db.model.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
@Entity
public class LocalizedPage {

    @Id
    private String title;

    @Column(nullable = false)
    private Locale locale;

    protected LocalizedPage() {
    }

    public LocalizedPage(String title, Locale locale) {
        this.title = title;
        this.locale = locale;
    }

    public String getTitle() {
        return title;
    }

    public Locale getLocale() {
        return locale;
    }
}

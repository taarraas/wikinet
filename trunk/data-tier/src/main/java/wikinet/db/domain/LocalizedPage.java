package wikinet.db.domain;

import wikinet.db.model.Locale;

import javax.persistence.*;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
@Entity
@IdClass(LocalizedPagePK.class)
public class LocalizedPage {

    @Id
    private String title;

    @Id
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalizedPage that = (LocalizedPage) o;

        if (locale != that.locale) return false;
        if (!title.equals(that.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + locale.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LocalizedPage{" +
                "title='" + title + '\'' +
                ", locale=" + locale +
                '}';
    }
}

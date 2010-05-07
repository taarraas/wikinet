package wikinet.db.domain;

import wikinet.db.model.Locale;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * @author shyiko
 * @since Apr 11, 2010
 */
public class LocalizedPagePK implements Serializable {

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Locale locale;

    protected LocalizedPagePK() {
    }

    public LocalizedPagePK(String title, Locale locale) {
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

        LocalizedPagePK that = (LocalizedPagePK) o;

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
}

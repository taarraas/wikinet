package wikinet.db.domain;

import wikinet.db.model.Locale;

import javax.persistence.*;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Embeddable
public class LocalizedArticle {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Locale locale;

    @Column(nullable = false)
    private String word;

    private String disambiguation;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalizedArticle that = (LocalizedArticle) o;

        if (disambiguation != null ? !disambiguation.equals(that.disambiguation) : that.disambiguation != null)
            return false;
        if (locale != that.locale) return false;
        if (!word.equals(that.word)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locale.hashCode();
        result = 31 * result + word.hashCode();
        result = 31 * result + (disambiguation != null ? disambiguation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocalizedArticle{" +
                "locale=" + locale +
                ", word='" + word + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                '}';
    }
}

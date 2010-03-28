package wikinet.wiki;

/**
 * @author shyiko
 * @since Mar 28, 2010
 */
public class ArticleReference implements Comparable<ArticleReference> {

    private String word;
    private String disambiguation;

    public ArticleReference(String word, String disambiguation) {
        this.word = word;
        this.disambiguation = disambiguation;
    }

    public String getWord() {
        return word;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArticleReference that = (ArticleReference) o;

        if (disambiguation != null ? !disambiguation.equals(that.disambiguation) : that.disambiguation != null)
            return false;
        if (word != null ? !word.equals(that.word) : that.word != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = word != null ? word.hashCode() : 0;
        result = 31 * result + (disambiguation != null ? disambiguation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return word + '\n' + disambiguation;
    }

    @Override
    public int compareTo(ArticleReference o) {
        return this.toString().compareTo(o.toString());
    }
    
}

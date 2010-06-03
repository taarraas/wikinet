package wikinet.wiki.parser.prototype;

/**
 * @author shyiko
 * @since Apr 16, 2010
 */
public class PagePrototype implements Comparable<PagePrototype> {

    private String word;
    private String disambiguation;

    public PagePrototype(String title) {
        title = title.trim();
        int start = title.indexOf("(");
        if (start > -1) {
            int end = title.lastIndexOf(")");
            if (end > -1) {
                word = title.substring(0, start).trim();
                disambiguation = title.substring(start + 1, end).trim();
            } else
                word = title;
        } else
            word = title;
        if (!word.isEmpty()) {
            word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            word = word.replace("_", " ");
        }
    }

    public PagePrototype(String word, String disambiguation) {
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
    public int compareTo(PagePrototype o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PagePrototype that = (PagePrototype) o;

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
        return word + (disambiguation == null ? "" : " (" + disambiguation + ")");
    }

}

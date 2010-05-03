package wikinet.wiki.parser;

/**
 * @author shyiko
 * @since Apr 30, 2010
 */
public class ParserSettings {

    public static final ParserSettings DEFAULT = new ParserSettings();    

    private String startWord;

    public ParserSettings() {
    }

    public ParserSettings(String startWord) {
        this.startWord = startWord;
    }

    public String getStartWord() {
        return startWord;
    }
    
}

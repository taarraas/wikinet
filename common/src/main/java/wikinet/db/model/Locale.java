package wikinet.db.model;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
public enum Locale {
    RUS("ru"), UKR("uk"), POL("pl");

    private String text;

    Locale(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
}

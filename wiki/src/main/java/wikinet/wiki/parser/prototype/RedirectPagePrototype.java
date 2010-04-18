package wikinet.wiki.parser.prototype;

/**
 * @author shyiko
 * @since Apr 16, 2010
 */
public class RedirectPagePrototype extends PagePrototype {

    private PagePrototype redirectedPage;

    public RedirectPagePrototype(String title, PagePrototype redirectedPage) {
        super(title);
        this.redirectedPage = redirectedPage;
    }

    public PagePrototype getRedirectedPage() {
        return redirectedPage;
    }
    
}

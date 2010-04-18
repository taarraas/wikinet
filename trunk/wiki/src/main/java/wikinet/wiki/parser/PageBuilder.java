package wikinet.wiki.parser;

import wikinet.wiki.parser.prototype.PagePrototype;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public interface PageBuilder {

    PagePrototype buildPagePrototype(String title, String text);

}

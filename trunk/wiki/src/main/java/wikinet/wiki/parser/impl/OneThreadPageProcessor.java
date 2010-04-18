package wikinet.wiki.parser.impl;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.wiki.parser.PageBuilder;
import wikinet.wiki.parser.PageProcessor;
import wikinet.wiki.parser.PagePrototypeSaver;
import wikinet.wiki.parser.prototype.PagePrototype;

/**
 * @author shyiko
 * @since Apr 18, 2010
 */
public class OneThreadPageProcessor implements PageProcessor {

    @Autowired
    private PageBuilder pageBuilder;

    @Autowired
    private PagePrototypeSaver pagePrototypeSaver;

    public OneThreadPageProcessor(PageBuilder pageBuilder, PagePrototypeSaver pagePrototypeSaver) {
        this.pageBuilder = pageBuilder;
        this.pagePrototypeSaver = pagePrototypeSaver;
    }

    @Override
    public void process(String title, String text) {
        PagePrototype prototype = pageBuilder.buildPagePrototype(title, text);
        if (prototype != null)
            pagePrototypeSaver.save(prototype);
    }

}

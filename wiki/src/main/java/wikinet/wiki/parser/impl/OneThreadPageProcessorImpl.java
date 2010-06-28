package wikinet.wiki.parser.impl;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.wiki.parser.PagePrototypeBuilder;
import wikinet.wiki.parser.PageProcessor;
import wikinet.wiki.parser.PagePrototypeSaver;
import wikinet.wiki.parser.prototype.PagePrototype;

/**
 * @author shyiko
 * @since Apr 18, 2010
 */
public class OneThreadPageProcessorImpl implements PageProcessor {

    @Autowired
    private PagePrototypeBuilder pagePrototypeBuilder;

    @Autowired
    private PagePrototypeSaver pagePrototypeSaver;

    public OneThreadPageProcessorImpl(PagePrototypeBuilder pagePrototypeBuilder, PagePrototypeSaver pagePrototypeSaver) {
        this.pagePrototypeBuilder = pagePrototypeBuilder;
        this.pagePrototypeSaver = pagePrototypeSaver;
    }

    @Override
    public void process(String title, String text) {
        PagePrototype prototype = pagePrototypeBuilder.build(title, text);
        if (prototype != null)
            pagePrototypeSaver.save(prototype);
    }

}

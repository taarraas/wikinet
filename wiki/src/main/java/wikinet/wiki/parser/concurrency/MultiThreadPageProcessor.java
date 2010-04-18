package wikinet.wiki.parser.concurrency;

import org.apache.log4j.Logger;
import wikinet.wiki.parser.PageBuilder;
import wikinet.wiki.parser.PageProcessor;
import wikinet.wiki.parser.PagePrototypeSaver;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author shyiko
 * @since Apr 18, 2010
 */
public class MultiThreadPageProcessor implements PageProcessor {

    private static Logger logger = Logger.getLogger(MultiThreadPageProcessor.class);

    private PageBuilder pageBuilder;
    private BlockingQueue<PagePrototype> blockingQueue;
    private Thread consumer;

    public MultiThreadPageProcessor(PageBuilder pageBuilder, PagePrototypeSaver pagePrototypeSaver, int queueCapacity) {
        this.pageBuilder = pageBuilder;
        this.blockingQueue = new LinkedBlockingQueue<PagePrototype>(queueCapacity);
        this.consumer = new Thread(new Consumer(blockingQueue, pagePrototypeSaver));
        this.consumer.start();
    }

    @Override
    public void process(String title, String text) {
        PagePrototype prototype = pageBuilder.buildPagePrototype(title, text);
        if (prototype != null) {
            try {
                blockingQueue.put(prototype);
            } catch (InterruptedException e) {
                logger.debug(e);
            }
        }
    }

}

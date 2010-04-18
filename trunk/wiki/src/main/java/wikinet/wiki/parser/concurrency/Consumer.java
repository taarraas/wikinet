package wikinet.wiki.parser.concurrency;

import org.apache.log4j.Logger;
import wikinet.wiki.parser.PagePrototypeSaver;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.concurrent.BlockingQueue;

/**
 * @author shyiko
 * @since Apr 18, 2010
 */
public class Consumer implements Runnable {

    private static Logger logger = Logger.getLogger(Consumer.class);

    private final BlockingQueue<PagePrototype> queue;
    private final PagePrototypeSaver pagePrototypeSaver;

    public Consumer(BlockingQueue<PagePrototype> queue, PagePrototypeSaver pagePrototypeSaver) {
        this.queue = queue;
        this.pagePrototypeSaver = pagePrototypeSaver;
    }

    @Override
    public void run() {
        try {
            while (true) {
                pagePrototypeSaver.save(queue.take());
            }
        } catch (InterruptedException ex) {
            logger.debug(ex);
        }
    }
    
}

package wikinet.wiki.parser.impl;

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
public class MultiThreadPageProcessorImpl implements PageProcessor {

    private static Logger logger = Logger.getLogger(MultiThreadPageProcessorImpl.class);

    private PageBuilder pageBuilder;
    private BlockingQueue<PagePrototype> blockingQueue;
    private Thread consumer;

    public MultiThreadPageProcessorImpl(PageBuilder pageBuilder, PagePrototypeSaver pagePrototypeSaver, int queueCapacity) {
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

    public static class Consumer implements Runnable {

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

}

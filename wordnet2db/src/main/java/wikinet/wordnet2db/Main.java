package wikinet.wordnet2db;

import java.io.IOException;

/**
 * @author shyiko
 * @since Mar 3, 2010
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("WordNet DB file should be specified as argument.");
            System.exit(0);
        }

        WordNet2DB wordDao = (WordNet2DB) ContextFactory.getContext().getBean("wordNet2DB");
        wordDao.importIt(args[0]);
    }

}

package wikinet.w2wmapping;

import java.io.IOException;

/**
 * @author shyiko
 * @since Mar 3, 2010
 */
public class Main {

    public static void main(String[] args) throws IOException {
        SynsetMapper mapper = (SynsetMapper) ContextFactory.getContext().getBean("wordNet2DB");        
    }

}

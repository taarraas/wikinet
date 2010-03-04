package wikinet.wordnet2db;

import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author shyiko
 * @since Mar 3, 2010
 */
public class ContextFactory {

    private static final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]
            {"spring-module.xml", "spring-w2db-mudule.xml"});

    private ContextFactory() {}
    
    public static AbstractRefreshableConfigApplicationContext getContext() {
        return context;
    }

}

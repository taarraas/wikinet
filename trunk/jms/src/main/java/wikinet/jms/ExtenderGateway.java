package wikinet.jms;

/**
 * @author shyiko
 * @since May 7, 2010
 */
public interface ExtenderGateway {

    void sendPage(long id);
    long receivePage();

}

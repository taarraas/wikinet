package wikinet.jms;

/**
 * @author shyiko
 * @since May 7, 2010
 */
public interface MapperGateway {

    void sendSynset(long id);
    long receiveSynset();

}

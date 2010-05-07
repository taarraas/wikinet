package wikinet;

import wikinet.db.dao.PageDao;
import wikinet.extending.Extender;
import wikinet.jms.ExtenderGateway;

/**
 * @author shyiko
 * @since May 7, 2010
 */
public class PageConsumerService implements Service {

    private ExtenderGateway extenderGateway;

    private Extender extender;

    public void setExtenderGateway(ExtenderGateway extenderGateway) {
        this.extenderGateway = extenderGateway;
    }

    public void setExtender(Extender extender) {
        this.extender = extender;
    }

    @Override
    public void start() {
        long pageId;
        while ((pageId = extenderGateway.receivePage()) != -1) {
            extender.extend(pageId);
        }

    }
    
}

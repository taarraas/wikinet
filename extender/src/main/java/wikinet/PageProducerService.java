package wikinet;

import wikinet.db.dao.PageDao;
import wikinet.db.dao.SynsetDao;
import wikinet.jms.ExtenderGateway;

/**
 * @author shyiko
 * @since May 7, 2010
 */
public class PageProducerService implements Service {

    private ExtenderGateway extenderGateway;

    private PageDao pageDao;

    public void setExtenderGateway(ExtenderGateway extenderGateway) {
        this.extenderGateway = extenderGateway;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    @Override
    public void start() {
        for (Long pageId : pageDao.findWithoutSynsets()) {
            extenderGateway.sendPage(pageId);
        }
    }
    
}

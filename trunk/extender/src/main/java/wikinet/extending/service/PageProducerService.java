package wikinet.extending.service;

import org.hibernate.SessionFactory;
import wikinet.Service;
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

    private SessionFactory sessionFactory;

    public void setExtenderGateway(ExtenderGateway extenderGateway) {
        this.extenderGateway = extenderGateway;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void start() {
        sessionFactory.getCurrentSession().beginTransaction();
        try {
            for (Long pageId : pageDao.findWithoutSynsets()) {
                extenderGateway.sendPage(pageId);
            }
        } finally {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }
    
}

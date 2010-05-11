package wikinet.mapping.service;

import org.hibernate.SessionFactory;
import wikinet.Service;
import wikinet.db.dao.SynsetDao;
import wikinet.jms.MapperGateway;

/**
 * @author shyiko
 * @since May 7, 2010
 */
public class SynsetProducerService implements Service {

    private MapperGateway mapperGateway;

    private SynsetDao synsetDao;

    private SessionFactory sessionFactory;

    public void setMapperGateway(MapperGateway mapperGateway) {
        this.mapperGateway = mapperGateway;
    }

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void start() {
        sessionFactory.getCurrentSession().beginTransaction();
        try {
            for (Long synsetId : synsetDao.findAllId()) {
                mapperGateway.sendSynset(synsetId);
            }
        } finally {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }
}

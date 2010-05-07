package wikinet;

import wikinet.db.dao.SynsetDao;
import wikinet.jms.MapperGateway;

/**
 * @author shyiko
 * @since May 7, 2010
 */
public class SynsetProducerService implements Service {

    private MapperGateway mapperGateway;

    private SynsetDao synsetDao;

    public void setMapperGateway(MapperGateway mapperGateway) {
        this.mapperGateway = mapperGateway;
    }

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    @Override
    public void start() {
        for (Long synsetId : synsetDao.findAllId()) {
            mapperGateway.sendSynset(synsetId);
        }
    }
}

package wikinet;

import wikinet.jms.MapperGateway;
import wikinet.mapping.Mapper;

/**
 * @author shyiko
 * @since May 7, 2010
 */
public class SynsetConsumerService implements Service {

    private MapperGateway mapperGateway;

    private Mapper mapper;

    public void setMapperGateway(MapperGateway mapperGateway) {
        this.mapperGateway = mapperGateway;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void start() {
        long synsetId;
        while ((synsetId = mapperGateway.receiveSynset()) != -1) {
            mapper.map(synsetId);
        }
    }
    
}

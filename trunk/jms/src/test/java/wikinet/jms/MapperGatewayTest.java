package wikinet.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

/**
 * @author shyiko
 * @since May 7, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-jms-module.xml"})
public class MapperGatewayTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MapperGateway mapperGateway;

    public void testSendReceive() throws Exception {
        mapperGateway.sendSynset(2L);
        long value = mapperGateway.receiveSynset();
        Assert.assertEquals(value, 2L);
    }
}
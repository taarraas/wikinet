package wikinet.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author shyiko
 * @since May 7, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-jms-module.xml"})
public class ExtenderGatewayTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ExtenderGateway extenderGateway;

    @Test
    public void testSendReceive() throws Exception {
        extenderGateway.sendPage(1L);
        long value = extenderGateway.receivePage();
        Assert.assertEquals(value, 1L);
    }
}

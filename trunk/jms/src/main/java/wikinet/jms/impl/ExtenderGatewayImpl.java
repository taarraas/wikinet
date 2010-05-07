package wikinet.jms.impl;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsUtils;
import wikinet.jms.ExtenderGateway;

import javax.jms.*;

/**
 * @author shyiko
 * @since May 7, 2010
 */
public class ExtenderGatewayImpl implements ExtenderGateway {

    private static final Logger logger = Logger.getLogger(ExtenderGatewayImpl.class);

    private JmsTemplate jmsTemplate;

    private Destination destination;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    @Override
    public void sendPage(final long id) {
        jmsTemplate.send(destination,
          new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage message = session.createObjectMessage();
                message.setLongProperty("page", id);
                return message;
            }
          }
        );
    }

    @Override
    public long receivePage() {
        try {
            ObjectMessage message = (ObjectMessage) jmsTemplate.receive(destination);
            try {
                return message.getLongProperty("page");
            } catch (JMSException e) {
                throw JmsUtils.convertJmsAccessException(e);
            }
        } catch (org.springframework.jms.JmsException e) {
            logger.warn(e);
            return -1;
        }
    }

}
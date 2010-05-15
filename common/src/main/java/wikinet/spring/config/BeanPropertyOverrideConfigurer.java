package wikinet.spring.config;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Properties;

/**
 * @author shyiko
 * @since May 14, 2010
 */
public class BeanPropertyOverrideConfigurer implements BeanFactoryPostProcessor, PriorityOrdered {

    private static final Logger logger = Logger.getLogger(BeanPropertyOverrideConfigurer.class);

    private String propertiesFile;
    private Properties properties;

    public BeanPropertyOverrideConfigurer(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    private int order = Ordered.LOWEST_PRECEDENCE;

    public void setOrder(int order) {
      this.order = order;
    }

    @Override
    public int getOrder() {
      return this.order;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String filePath = System.getProperty(propertiesFile);
        if (loadFile(filePath)) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                String[] strings = key.split("\\.");
                if (strings.length != 2) {
                    logger.warn("Couldn't apply \"" + key + "\" property.");
                    continue;
                }
                applyProperty(beanFactory, strings[0].trim(), strings[1].trim(), value.trim());
            }
        }
    }

    private boolean loadFile(String fileName) {
        if (fileName == null || fileName.isEmpty())
            return false;

        File file = new File(fileName);
        if (!file.exists())
            return false;

        properties = new Properties();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            try {
                properties.load(bufferedReader);
            } finally {
                bufferedReader.close();
            }
        } catch (Exception ex) {
            logger.error(ex);
            return false;
        }

        return true;
    }

    protected void applyProperty(ConfigurableListableBeanFactory factory, String beanName, String property, String value) {
        if (!factory.containsBeanDefinition(beanName)) {
            logger.warn("Bean with name" + beanName + " was not found.");
            return;
        }
        if (logger.isDebugEnabled())
            logger.debug(beanName + "." + property + " changed to \"" + value + "\"");
        BeanDefinition bd = factory.getBeanDefinition(beanName);
        while (bd.getOriginatingBeanDefinition() != null) {
            bd = bd.getOriginatingBeanDefinition();
        }
        bd.getPropertyValues().addPropertyValue(property, value);
    }

}

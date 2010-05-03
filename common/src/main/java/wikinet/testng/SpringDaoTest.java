package wikinet.testng;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

/**
 * @author shyiko
 * @since May 1, 2010
 */
public abstract class SpringDaoTest extends AbstractTestNGSpringContextTests implements IHookable {

    @Autowired
    protected SessionFactory sessionFactory;

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        sessionFactory.getCurrentSession().beginTransaction();
        try {
            callBack.runTestMethod(testResult);
        } finally {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

}

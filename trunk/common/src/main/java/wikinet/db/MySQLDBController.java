package wikinet.db;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import java.util.List;


/**
 * @author shyiko
 * @since May 24, 2010
 */
public class MySQLDBController implements DBController {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void cleanDB(String... tablesToSkip) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        try {
            String dbName = (String) session.createSQLQuery("select DATABASE();").uniqueResult();
            session.createSQLQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate();
            List<String> list = session.createSQLQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + dbName + "';").list();
            rootCycle:
            for (String tableName : list) {
                for (String tableToSkip : tablesToSkip) {
                    if (tableName.equals(tableToSkip))
                        continue rootCycle;
                }
                session.createSQLQuery("delete from " + tableName + ";").executeUpdate();
            }
            session.createSQLQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate();
            session.getTransaction().commit();
        } finally {
            if (session.isOpen() && session.getTransaction().isActive())
                session.getTransaction().rollback();
        }
    }
    
}

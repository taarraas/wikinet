package wikinet.db.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import wikinet.db.dao.GenericDao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public abstract class GenericDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK> {

    private Class domainClass = (Class) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];

    protected SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public T findById(PK id) {
        return (T) getSession().get(domainClass, id);
    }

    public List<T> findAll() {
        return findByCriteria();
    }

    public T save(T obj) {
        getSession().saveOrUpdate(obj);
        return obj;
    }

    public void delete(T obj) {
        getSession().delete(obj);
    }

    protected List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(domainClass);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        List list = crit.list();
        return list;
   }

   protected Session getSession() {
       return sessionFactory.getCurrentSession();
   }

}

package wikinet.persistence.dao.impl;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import wikinet.persistence.dao.GenericDao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public abstract class GenericDaoImpl<T, PK extends Serializable>
        extends HibernateDaoSupport implements GenericDao<T, PK> {

    private Class domainClass = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public T findById(PK id) {
        return (T) getHibernateTemplate().get(domainClass, id);
    }

    public List<T> findAll() {
        return (List<T>) getHibernateTemplate().loadAll(domainClass);
    }

    public void save(T obj) {
        getHibernateTemplate().saveOrUpdate(obj);
    }

    public void delete(T obj) {
        getHibernateTemplate().delete(obj);
    }

}

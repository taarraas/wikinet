package wikinet.db.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public interface GenericDao<T, PK extends Serializable> {
        T findById(PK id);
        List<T> findAll();
        T save(T obj);
        void delete(T obj);
}

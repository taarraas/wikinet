package wikinet.persistence.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public interface GenericDao<T, PK extends Serializable> {

        T findById(PK id);

        List<T> findAll();

        void save(T obj);

        void delete(T obj);

}

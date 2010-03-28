package wikinet.db.domain;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author shyiko
 * @since Mar 28, 2010
 */
public class SynsetIdGenerator implements IdentifierGenerator {

    private static final String sql = "select max(id) from Synset";

    @Override
    public Serializable generate(SessionImplementor session, Object o) throws HibernateException {
        Synset synset = (Synset) o;
        if (synset.idPreInit != 0)
            return synset.idPreInit;
        session.flush();
        long next = 0;

        try {
            PreparedStatement st = session.getBatcher().prepareSelectStatement(sql);
            try {
                ResultSet rs = st.executeQuery();
                try {
                    if (rs.next() && !rs.wasNull()) {
                        next = rs.getLong(1) + 1;
                    }
                } finally {
                    rs.close();
                }

            } finally {
                session.getBatcher().closeStatement(st);
            }
        } catch (SQLException ex) {
                throw JDBCExceptionHelper.convert(
                        session.getFactory().getSQLExceptionConverter(),
                        ex,
                        "could not fetch initial value for \"SynsetIdGenerator\" generator",
                        sql
                    );
        }
        return next;
    }
}

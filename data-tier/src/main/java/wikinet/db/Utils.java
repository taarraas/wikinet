package wikinet.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * @author shyiko
 * @since Apr 3, 2010
 */
public class Utils {

    private static Utils utils;

    private Utils() {
    }

    public static Utils getInstance() {
        if (utils == null)
            utils = new Utils();
        return utils;
    }

    public String getStringFromClob(Clob clob) {
        return getStringBuilderFromClob(clob).toString();
    }

    /**
     * @param clob
     * @return empty StringBuilder if <i>clob</i> is null or <i>clob</i> text otherwise
     */
    public StringBuilder getStringBuilderFromClob(Clob clob) {
        try {
            StringBuilder sb = new StringBuilder();
            if (clob == null)
                return sb;
            BufferedReader reader = new BufferedReader(clob.getCharacterStream());
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            // do not close reader. Otherwise you'll get "could not reset reader" exception
            return sb;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}

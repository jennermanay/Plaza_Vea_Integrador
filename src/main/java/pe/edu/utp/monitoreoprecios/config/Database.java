package pe.edu.utp.monitoreoprecios.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public final class Database {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Database.class.getResourceAsStream("/application.properties")) {
            if (in == null) {
                throw new IllegalStateException("No existe application.properties");
            }
            PROPS.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() {}

    public static Connection getConnection() throws Exception {
        String url = PROPS.getProperty("db.url");
        String user = PROPS.getProperty("db.user");
        String password = PROPS.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}

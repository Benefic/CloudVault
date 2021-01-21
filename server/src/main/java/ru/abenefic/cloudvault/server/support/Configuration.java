package ru.abenefic.cloudvault.server.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final Logger LOG = LogManager.getLogger(Configuration.class);

    private static Configuration instance;

    private String dbHost;
    private String dbLogin;
    private String dbPassword;
    private int srvPort;
    private String srvRootDirectory;

    private Configuration() {
        Properties property = new Properties();
        try (InputStream fis = getClass().getResourceAsStream("config.properties")) {
            property.load(fis);
            dbHost = property.getProperty("db.host");
            dbLogin = property.getProperty("db.login");
            dbPassword = property.getProperty("db.password");
            srvPort = Integer.parseInt(property.getProperty("srv.port"));
            srvRootDirectory = property.getProperty("srv.rootDirectory");
        } catch (IOException e) {
            LOG.error("Read property", e);
        }
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbLogin() {
        return dbLogin;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public int getSrvPort() {
        return srvPort;
    }

    public String getSrvRootDirectory() {
        return srvRootDirectory;
    }
}

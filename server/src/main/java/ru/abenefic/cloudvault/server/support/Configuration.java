package ru.abenefic.cloudvault.server.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Configuration {

    private static final Logger LOG = LogManager.getLogger(Configuration.class);

    private static Configuration instance;

    private int srvPort;
    private String srvRootDirectory;

    private Configuration() {
        Properties property = new Properties();
        try (InputStream fis = getClass().getResourceAsStream("config.properties")) {
            property.load(fis);
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

    public int getSrvPort() {
        return srvPort;
    }

    public String getSrvRootDirectory() {
        return srvRootDirectory;
    }
}

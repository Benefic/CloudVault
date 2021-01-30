package ru.abenefic.cloudvault.client.support;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Data
public class Context {

    private static final Logger LOG = LogManager.getLogger(Context.class);

    private static final String homeProperty = "user.home";

    private static Context current;

    private int serverPort = 8189;
    private String serverHost = "localhost";
    private String login;
    private String password;
    private String token;
    private Path clientHome;
    private Path userHome;
    private boolean savePassword;

    private Context() {
        // получим домашний каталог пользователя.
        // в нём храним файл настроек и тут же каталог для синхронизации
        // в файле настроек можно переопределить каталог и прочие настройки
        String path = System.getProperty(homeProperty);
        LOG.info("User home: " + path);

        // создаём служебные каталоги, если ещё нет
        clientHome = Paths.get(path, ".cloudVault");
        checkDirectory(clientHome);
        userHome = clientHome.resolve("storage");
        checkDirectory(userHome);

        // создаём файл для хранения настроек
        // без расширения, чтобы криворукие не испортили.
        // А кто сможет открыть - тот знает, что делает
        Path configPath = clientHome.resolve("config");
        if (Files.notExists(configPath)) {
            try {
                Files.createFile(configPath);
            } catch (IOException e) {
                LOG.error(e);
                // тут падаем
                System.exit(-1);
            }
        }
        Properties property = new Properties();
        try (InputStream fis = Files.newInputStream(configPath)) {
            // если файл пустой, то настройки все по дефолту.
            // их хранить в файле не будем, пока пользователь не попросит
            property.load(fis);
            serverPort = Integer.parseInt(property.getProperty("serverPort", "8189"));
            serverHost = property.getProperty("serverHost", "localhost");
            login = property.getProperty("login", "");
            password = property.getProperty("password", password);
            userHome = Path.of(property.getProperty("userHome", userHome.toString()));
            savePassword = Boolean.parseBoolean(property.getProperty("savePassword", "false"));
        } catch (IOException e) {
            LOG.error("Read property", e);
            // не смогли прочитать, будут значения по умолчанию
        }
    }

    // синглтон
    public static Context current() {
        if (current == null) {
            current = new Context();
        }
        return current;
    }

    private void checkDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                LOG.error(e);
                // тут падаем
                System.exit(-1);
            }
        }
    }

    public void saveSettings() {
        Properties property = new Properties();
        property.setProperty("serverPort", String.valueOf(serverPort));
        property.setProperty("serverHost", serverHost);
        property.setProperty("login", login);
        property.setProperty("userHome", String.valueOf(userHome));
        property.setProperty("savePassword", String.valueOf(savePassword));
        if (savePassword) {
            property.setProperty("password", password);
        }
        Path configPath = clientHome.resolve("config");
        try (OutputStream os = Files.newOutputStream(configPath, StandardOpenOption.CREATE)) {
            property.store(os, "Client settings");
        } catch (IOException e) {
            LOG.error("Write property", e);
            // не смогли записать, будут значения по умолчанию
        }
    }

}

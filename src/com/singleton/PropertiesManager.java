package com.singleton;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertiesManager.java.
 * Class that applies Singleton pattern to instance Properties only once.
 */
public final class PropertiesManager {
    private static PropertiesManager propertiesManager;
    private Properties prop;


    /**
     * Constructor, private to apply singleton pattern.
     */
    private PropertiesManager() {
        init();
    }

    /**
     * Static method to get an class instance.
     *
     * @return instance.
     */
    public static PropertiesManager getInstance() {
        if (propertiesManager == null) {
            propertiesManager = new PropertiesManager();
        }
        return propertiesManager;
    }

    /**
     * Initialize Properties object.
     */
    private void init() {
        prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Getter of username.
     *
     * @return String username.
     */
    public String getUsername() {
        return prop.getProperty("user");
    }

    /**
     * Getter of password.
     *
     * @return String password.
     */
    public String getPassword() {
        return prop.getProperty("password");
    }

    public String getServer() {
        return prop.getProperty("server");
    }

    public String getDB(){
        return prop.getProperty("db");
    }
}

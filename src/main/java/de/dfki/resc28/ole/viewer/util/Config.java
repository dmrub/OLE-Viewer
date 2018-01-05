/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.resc28.ole.viewer.util;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dmitri Rubinstein
 */
public class Config {

    public final static String PROPERTIES_RESOURCE_NAME = "ole-viewer.properties";
    public final static String CONFIG_JS_PATH_SYS_PROPERTY = "ole-viewer.config.js.path";
    public final static String CONFIG_JS_PATH_PROPERTY = "config.js.path";

    public final static String CONFIG_JS_SYS_PROPERTY = "ole-viewer.config.js";
    public final static String CONFIG_JS_PROPERTY = "config.js";
    public final static String CONFIG_JS_ENVVAR = "CONFIG_JS";

    public final static Properties PROPERTIES = new Properties();
    public final static String VERSION;
    public final static String BUILD_DATE;
    public final static String CONFIG_JS_PATH;
    public final static String CONFIG_JS;

    public static String getProperty(java.util.Properties p, String key, String sysKey) {
        return getProperty(p, key, sysKey, null);
    }

    public static String getProperty(java.util.Properties p, String key, String sysKey, String envKey) {
        String value = null;
        if (envKey != null) {
            try {
                value = System.getenv(envKey);
            } catch (SecurityException ex) {

            }
            if (value != null) {
                return value;
            }
        }
        if (sysKey != null) {
            try {
                value = System.getProperty(sysKey);
            } catch (SecurityException ex) {

            }
            if (value != null) {
                return value;
            }
        }
        return p.getProperty(key);
    }

    static {
        java.io.InputStream is = Config.class.getClassLoader().getResourceAsStream(PROPERTIES_RESOURCE_NAME);
        System.out.println("Loading information from internal resource file ...");
        try {
            PROPERTIES.load(is);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
        VERSION = PROPERTIES.getProperty("version", "");
        BUILD_DATE = PROPERTIES.getProperty("build-date", "");
        CONFIG_JS_PATH = getProperty(PROPERTIES, CONFIG_JS_PATH_PROPERTY, CONFIG_JS_PATH_SYS_PROPERTY);
        CONFIG_JS = getProperty(PROPERTIES, CONFIG_JS_PROPERTY, CONFIG_JS_SYS_PROPERTY, CONFIG_JS_ENVVAR);
    }

}

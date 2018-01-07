package com.mak.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by lenovo on 2018/1/6.
 */
public class Constant {

    public static Properties PROPERTIES;

    private static final Logger logger = LoggerFactory.getLogger(Constant.class);

    static {
        try {
            PROPERTIES = PropertiesLoaderUtils.loadAllProperties("application.properties");
        } catch (IOException e) {
            logger.error("load application.properties error!", e);
        }
    }

    public final static String API_SHARES = PROPERTIES.getProperty("api.shares");

    public final static String API_HISTORY = PROPERTIES.getProperty("api.history");

    public final static String API_HISTORY_TEMP_FILE = PROPERTIES.getProperty("api.shares.temp_file");

}

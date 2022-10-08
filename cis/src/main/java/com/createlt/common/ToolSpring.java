package com.createlt.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ToolSpring implements ApplicationContextAware {
    /**
     * <p> Field applicationContext: applicationContext </p>
     */
    @Autowired
    private static ApplicationContext applicationContext = null;
    /**
     * <p>Field pop: properties</p>
     */
    private static Properties pop = null;
    /**
     * <p>Field path: path</p>
     */
    private static String path = null;
    /**
     * <p>Field key: key</p>
     */
    @SuppressWarnings("unused")
    private static String key = "2.sfSFKI20XCV9SFAkJ923";

    /**
     * <p>Description: setApplicationContext</p>
     * @param applicationContext ApplicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (ToolSpring.applicationContext == null) {
            ToolSpring.applicationContext = applicationContext;
        }
        try {
            init();
        } catch (Throwable e) {
        }
    }

    /**
     * <p>Description: getApplicationContext</p>
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    /**
     * <p>Description: get bean</p>
     * @param name name
     * @return value
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * <p>Description: init</p>
     * @throws Throwable Throwable
     */
    private static void init() throws Throwable  {
    }
}

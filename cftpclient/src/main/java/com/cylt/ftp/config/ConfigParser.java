package com.cylt.ftp.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class ConfigParser {

    private static Map<String,Object> config = null;
    public static ArrayList<Map<String,Object>> user = null;

    public ConfigParser() {
        try {
            //IDEA中运行
            File file = getProjectConfigFile();
            //打包到服务器
            //File file = getServerConfigFile();
            InputStream in = new FileInputStream(file);

            Yaml yaml = new Yaml();
            config = (Map<String,Object>) yaml.load(in);

            user = (ArrayList<Map<String, Object>>) config.get(user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开发环境取配置
     * @return
     */
    public File getProjectConfigFile() {
        return new File("src/main/resources/config/ftp.yaml");
    }

    /**
     * 打包后读配置
     */
    public File getServerConfigFile()
    {
        String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        classPath = classPath.substring(5,classPath.indexOf("cftpserver.jar"))+"ftp.yaml";
        return new File(classPath);
    }

    public static Object get(String key){
        if (config==null){
            new ConfigParser();
        }
        return config.get(key);
    }

}

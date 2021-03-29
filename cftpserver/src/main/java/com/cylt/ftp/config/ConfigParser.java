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
            user = (ArrayList<Map<String, Object>>) get("user");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开发环境取配置
     * @return
     */
    public File getProjectConfigFile() {
        File file = new File("src/main/resources/config/ftp.yaml");
        if(file.isFile()) {
            return file;
        } else {
            return getServerConfigFile();
        }
    }

    /**
     * 打包后读配置
     */
    public File getServerConfigFile(){

        String filePath = System.getProperty("java.class.path");
        String pathSplit = System.getProperty("path.separator");//windows下是";",linux下是":"

        if(filePath.contains(pathSplit)){
            filePath = filePath.substring(0,filePath.indexOf(pathSplit));
        }else if (filePath.endsWith(".jar")) {//截取路径中的jar包名,可执行jar包运行的结果里包含".jar"
            filePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
        }
        return new File(filePath + "ftp.yaml");
    }

    public static Object get(String key){
        if (config==null){
            new ConfigParser();
        }
        return config.get(key);
    }

}

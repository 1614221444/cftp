package com.createlt.mapping.utils;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * 动态编译运行Java工具类
 */
public class RunJavaUtils {
    private static JavaCompiler compiler;
    public static final String CLASS_PATH = System.getProperty("user.dir") + File.separator + "cis/target" + File.separator + "script" + File.separator;

    static {
        compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            String error = String.format("Java运行环境缺少文件：请将'系统jdk目录\\lib\\tools.jar'文件复制到'%s\\lib\\目录下'", System.getProperty("java.home"));
            System.out.println("ClassUtil init: " + error);
            throw new RuntimeException(error);
        }
    }

    public boolean javac(String className) {
        //获取java文件管理类
        StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
        //获取java文件对象迭代器
        Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(new File(CLASS_PATH + className + ".java"));
        //设置编译参数
        ArrayList<String> ops = new ArrayList<String>();
        ops.add("-Xlint:unchecked");
        //设置classpath
        //ops.add("-classpath");

        //获取编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, ops, null, it);
        //执行编译任务
        return task.call();
    }

    /**
     *
     * <p>Title: compile</p>
     * <p>Description: compile </p>
     * @param javaName javaName the name of your public class,eg: <code>TestClass.java</code>
     * @param javaSrc  source code string
     * @return return the Map, the KEY means ClassName, the VALUE means bytecode.
     */
    public static String compile(String javaName, String javaSrc) {
        StringBuilder ret = new StringBuilder();
        // 获取jvm的jre
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
        try (StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null)) {
            //获取java文件对象迭代器
            Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(new File(CLASS_PATH + javaName + ".java"));

            // 用来获取编译错误时的错误信息
            DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

            JavaCompiler.CompilationTask task = compiler.getTask(null, manager, collector, null, null, it);
            if (task.call()) {
                //return manager.getClass();
                return ret.toString();
            } else {
                // 当编译存在错误的时候，打印错误日志
                for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                    ret.append(diagnostic.toString());
                }
            }
            // 关闭StandardJavaFileManager
            stdManager.close();

        } catch (IOException e) {
            //log.error("出现异常 : " , e);
        }
        return ret.toString();
    }
    /**
     * 调用类方法
     *
     * @param cls        类
     * @param methodName 方法名
     * @param paramsCls  方法参数类型
     * @param params     方法参数
     * @return
     */
    public static Object invoke(Class<?> cls, String methodName, Class<?>[] paramsCls, Object[] params)
            throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = cls.getDeclaredMethod(methodName, paramsCls);
        Object obj = cls.newInstance();
        return method.invoke(obj, params);
    }

    /**
     * 外部实现类
     * @param name
     * @return
     */
    private static Class<?> load(String name) throws NullPointerException {
        //加载类文件的方法，返回加载后的Class
        Class<?> cls = null;
        //这里使用自定义的ClassLoader
        MyClassLoader classLoader = new MyClassLoader();
        cls = classLoader.findClass(name);
        return cls;
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        new RunJavaUtils().javac("Main");
        Class<?> cls = load("Main");
        Class<?> clss = load("Main");
        System.out.println(clss);
        new RunJavaUtils();
        invoke(cls, "start",new Class[]{String.class},new Object[]{"5412412"});
    }

    /**
     *
     * @param id
     * @param script
     * @return
     * @throws IOException
     */
    public String setScript(String id, String script) throws IOException {
        String className = "C" + id;
        OutputStream out = Files.newOutputStream(new File(CLASS_PATH + className + ".java").toPath());

        script = getScriptClassName(className, script);
        out.write(script.getBytes(StandardCharsets.UTF_8));
        out.close();
        return compile(className, "");
        //return new RunJavaUtils().javac(className);
    }

    private String getScriptClassName(String className, String script) {
        int [] classIndex = new int[]{0,1};
        classIndex[0] = script.indexOf("public class ") + 13;
        classIndex[1] = script.indexOf(" implements BaseModel");
        String substring = script.substring(classIndex[0], classIndex[1]);
        script = script.replace(substring, className);
        return script;
    }

    public Class<?> getScript(String id) {
        String className = "C" + id;
        Class<?> cls = load(className);
        return cls;
    }

}

package com.createlt.cis.mapping.utils;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 动态编译运行Java工具类
 */
public class RunJavaUtils {
    private static JavaCompiler compiler;
    public static final String CLASS_PATH = System.getProperty("user.dir") + File.separator + "target" + File.separator + "script" + File.separator;

    static {
        compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            String error = String.format("Java运行环境缺少文件：请将'系统jdk目录\\lib\\tools.jar'文件复制到'%s\\lib\\目录下'", System.getProperty("java.home"));
            System.out.println("ClassUtil init: " + error);
            throw new RuntimeException(error);
        }
    }

    public void javac() {
        //获取java文件管理类
        StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
        //获取java文件对象迭代器
        Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(new File(CLASS_PATH + "Main.java"));
        //设置编译参数
        ArrayList<String> ops = new ArrayList<String>();
        ops.add("-Xlint:unchecked");
        //设置classpath
        ops.add("-classpath");
        ops.add(CLASS_PATH);
        //获取编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, ops, null, it);
        //执行编译任务
        task.call();
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
    private static Class<?> load(String name) {
        //加载类文件的方法，返回加载后的Class
        Class<?> cls = null;
        try {
            //这里使用自定义的ClassLoader
            MyClassLoader classLoader = new MyClassLoader();
            cls = classLoader.findClass(name);
        } catch (Exception e) {
            System.out.println(e);
        }
        return cls;
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        new RunJavaUtils().javac();
        Class<?> cls = load("Main");
        Class<?> clss = load("Main");
        System.out.println(clss);
        new RunJavaUtils();
        invoke(cls, "start",new Class[]{String.class},new Object[]{"5412412"});
    }



}

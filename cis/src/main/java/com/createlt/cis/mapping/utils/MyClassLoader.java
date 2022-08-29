package com.createlt.cis.mapping.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) {
        String myPath = "file:///" + RunJavaUtils.CLASS_PATH.replaceAll("\\\\", "/") + "/" + name.replace(".", "/") + ".class";
        byte[] cLassBytes = null;
        try {
            Path path = Paths.get(new URI(myPath));
            cLassBytes = Files.readAllBytes(path);
        } catch (IOException | URISyntaxException e) {
            System.out.println(e);
        }
        return defineClass(name, cLassBytes, 0, cLassBytes.length);
    }
}

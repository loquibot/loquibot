package com.alphalaneous.Annotations;

import com.alphalaneous.Main;
import com.alphalaneous.PluginHandler;
import com.alphalaneous.Utilities.Logging;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationHandler {

    public static void loadStartingMethods(){

        Set<Method> methodsAnnotatedWith = new HashSet<>();

        Package[] pkgs = Package.getPackages();
        for(Package pkg : pkgs){
            Set<Method> methods = new Reflections(pkg.getName(), Scanners.MethodsAnnotated).getMethodsAnnotatedWith(OnLoad.class);
            methodsAnnotatedWith.addAll(methods);
        }

        HashMap<Method, Integer> methodsToLoad = new HashMap<>();

        for(Method m : methodsAnnotatedWith){
            int order = m.getAnnotation(OnLoad.class).order();
            methodsToLoad.put(m, order);
        }

        Object[] a = methodsToLoad.entrySet().toArray();
        Arrays.sort(a, Comparator.comparing(o -> ((Map.Entry<Method, Integer>) o).getValue()));
        for (Object e : a) {
            Method m = ((Map.Entry<Method, Integer>) e).getKey();

            try {
                if(m.getDeclaringClass().getName().startsWith(Main.class.getPackageName())) {
                    m.invoke(null);
                }
            } catch (Error | Exception ex) {
                Logging.getLogger().error(ex.getLocalizedMessage(), ex);
                JOptionPane.showMessageDialog(null, "Failed to invoke method " + m.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public static void loadPluginMethods(){

        PluginHandler.classes.forEach((k, v) -> {
            Method[] methods = k.getMethods();
            for(Method method : methods){
                if(method.isAnnotationPresent(OnLoad.class)){
                    try {
                        method.invoke(null);
                    } catch (Error | Exception ex) {
                        Logging.getLogger().error(ex.getLocalizedMessage(), ex);
                        JOptionPane.showMessageDialog(null, "Failed to load Plugin " + v + "\nCouldn't invoke method " + method.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}

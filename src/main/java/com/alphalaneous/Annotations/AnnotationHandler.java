package com.alphalaneous.Annotations;

import com.alphalaneous.Main;
import com.alphalaneous.PluginHandler;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SettingsHandler;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationHandler {


    public static void loadStartingMethods() {
        loadStartingMethods(false);
    }

    public static void loadStartingMethods(boolean tests){

        Set<Method> methodsAnnotatedWith = new HashSet<>();

        Package[] packages = Package.getPackages();
        for(Package pkg : packages){
            Set<Method> methods = new Reflections(pkg.getName(), Scanners.MethodsAnnotated).getMethodsAnnotatedWith(OnLoad.class);
            methodsAnnotatedWith.addAll(methods);
        }

        HashMap<Method, Integer> methodsToLoad = new HashMap<>();

        for(Method method : methodsAnnotatedWith){
            int order = method.getAnnotation(OnLoad.class).order();
            methodsToLoad.put(method, order);
        }

        Object[] array = methodsToLoad.entrySet().toArray();
        Arrays.sort(array, Comparator.comparing(o -> ((Map.Entry<Method, Integer>) o).getValue()));
        for (Object element : array) {
            Method method = ((Map.Entry<Method, Integer>) element).getKey();

            try {
                if(method.getDeclaringClass().getName().startsWith(Main.class.getPackageName())) {
                    boolean debug = method.getAnnotation(OnLoad.class).debug();
                    boolean forTests = method.getAnnotation(OnLoad.class).test();
                    if (tests) {
                        if (forTests) {
                            method.invoke(null);
                        }
                    }
                    else if(!debug || SettingsHandler.getSettings("isDebug").asBoolean()) {
                        method.invoke(null);
                    }
                }
            } catch (Error | Exception ex) {
                Logging.getLogger().error(ex.getMessage(), ex);
                JOptionPane.showMessageDialog(null, "Failed to invoke method " + method.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public static void loadPluginMethods(){

        PluginHandler.classes.forEach((k, v) -> {
            Method[] methods = k.getMethods();
            for(Method method : methods){
                if(method.isAnnotationPresent(OnLoad.class)){
                    try {
                        boolean debug = method.getAnnotation(OnLoad.class).debug();
                        if(!debug || SettingsHandler.getSettings("isDebug").asBoolean()) {
                            method.invoke(null);
                        }
                    } catch (Error | Exception ex) {
                        Logging.getLogger().error(ex.getMessage(), ex);
                        JOptionPane.showMessageDialog(null, "Failed to load Plugin " + v + "\nCouldn't invoke method " + method.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}

package com.jhenly.juifx.util;

public class Utils {
    
    /**
     * To force initialization of a class
     * @param classToInit
     */
    public static void forceInit(final Class<?> classToInit) {
        try {
            Class.forName(classToInit.getName(), true, classToInit.getClassLoader());
        } catch (final ClassNotFoundException e) {
            throw new AssertionError(e);  // Can't happen
        }
    }
    
}

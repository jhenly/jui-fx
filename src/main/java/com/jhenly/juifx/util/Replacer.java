package com.jhenly.juifx.util;

/**
 * Interface representing a replacer.
 * <p>
 * Replacers are objects that are used to replace some aspect(s) of an object
 * by returning the same, or another object with the replaced aspect(s).
 *
 * @param <T> - the type of the return type of the {@link #replace()} method
 * 
 * @author Jonathan Henly
 * @since JavaFX 1.0
 */
@FunctionalInterface
public interface Replacer<T> {
    /**
     * Replaces some pre-determined aspect(s) of a pre-specified object by
     * returning the same object with the replaced aspect(s), or by returning a
     * different object with the replaced aspect(s).
     * @param toReplace - the object to replace some aspect(s) of, or to
     *        replace entirely
     * @return an object with some aspect(s) replaced
     */
    T replace(T toReplace);
}

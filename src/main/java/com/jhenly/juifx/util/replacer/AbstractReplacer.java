package com.jhenly.juifx.util.replacer;

import com.jhenly.juifx.util.Replacer;

/**
 * This class provides a skeletal implementation of the {@link Replacer}
 * interface and incorporates a <i>dispose-after-replace</i> mechanism to help
 * out the garbage collector.
 * 
 * @param <T> - the type of the object to replace
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public abstract class AbstractReplacer<T> implements Replacer<T> {
    protected T toReplace;
    
    /**
     * Constructor used by implementing classes that assigns {@link #toReplace}
     * to the specified object.
     * @param toReplace - the object to replace some aspect(s) of, or to
     *        replace entirely
     */
    protected AbstractReplacer(T toReplace) { this.toReplace = toReplace; }
    
    @Override
    public final T replace() {
        final T ret = replaceImpl();
        dispose();
        return ret;
    }
    
    /**
     * Invoked by {@link #replace()} to get the <i>"replaced"</i> object that
     * will be returned after invoking {@link #dispose}.
     * <p>
     * Implementing classes should put their replacement related logic in this
     * method and, preferably, use the {@code protected} member
     * {@link #toReplace} to do replacements.
     * @return the object with some aspect(s) replaced, or replaced entirely
     */
    protected abstract T replaceImpl();
    
    /**
     * Method to help out the garbage collector, called after
     * {@link #replaceImpl()} has been called.
     * <p>
     * This method simply assigns {@link #toReplace} to {@code null}.
     */
    protected void dispose() { toReplace = null; }
    
}

package com.jhenly.juifx.util.replacer;

import com.jhenly.juifx.util.Replacer;


/**
 * This class provides a skeletal implementation of the {@link Replacer}
 * interface and incorporates a <i>dispose-after-replace</i> mechanism to help
 * out the garbage collector.
 * 
 * @param <T> - the type of the object to replace some aspect(s) of, or to
 *        replace entirely
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public abstract class DisposableReplacer<T> implements Replacer<T> {
    
    /** Empty constructor used by implementing classes. */
    protected DisposableReplacer() {}
    
    /** {@inheritDoc} */
    @Override
    public final T replace(T toReplace) {
        final T ret;
        
        // use try-finally to invoke 'dispose()' regardless of any exceptions
        try {
            ret = replaceImpl(toReplace);
        } finally {
            dispose();
        }
        
        return ret;
    }
    
    /**
     * Invoked by {@link #replace(Object)} to get the <i>"replaced"</i> object
     * that will be returned after invoking {@link #dispose()}.
     * <p>
     * Implementing classes should put their replacement related logic in this
     * method.
     * <p>
     * <b>Note:</b> {@link #dispose()} will be invoked regardless of any
     * propagated exceptions thrown by implementations of this method.
     * 
     * @param toReplace - the object to replace some aspect(s) of, or to
     *        replace entirely
     * @return the object with some aspect(s) replaced, or replaced entirely
     */
    protected abstract T replaceImpl(T toReplace);
    
    /**
     * Method used to clean up any resources and help out the garbage
     * collector.
     * <p>
     * This method is invoked by {@link #replace(Object)}, after
     * {@link #replaceImpl(Object)} has been invoked, regardless of any
     * propagated exceptions thrown by implementations of
     * {@code replaceImpl(Object)}. Any exceptions thrown by implementations of
     * this method will be propagated to the caller.
     * <p>
     * Implementing classes should override this
     * method to clean up any resources and/or set any members to {@code null}.
     * The default implementation of this method does nothing, since this class
     * uses no resources and contains no members.
     */
    protected void dispose() {}
    
}

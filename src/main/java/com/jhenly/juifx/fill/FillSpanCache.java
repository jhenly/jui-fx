package com.jhenly.juifx.fill;

import java.util.HashMap;
import java.util.Map;


/**
 * Class for caching {@code FillSpan} instances.
 * <p>
 * This class keeps a static cache of {@link FillSpan} instances to
 * improve performance. The cache is enabled by default . The size of the cache
 * can be queried via {@link #getCacheSize()}. If for some reason the cache is
 * found to be hurting performance, then the cache can be disabled via
 * {@link #disableCache()} and/or cleared via {@link #clearCache()}. The
 * enabled state of the cache can be queried via {@link #isCacheEnabled()}. If
 * the cache is disabled it can be re-enabled via {@link #enableCache()}.
 * <p>
 * If it is known that no instances of {@code FillSpan} are in use and no more
 * will be needed, then clearing the cache would be beneficial.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public final class FillSpanCache {
    
    /**************************************************************************
     *                                                                        *
     * Static Members                                                         *
     *                                                                        *
     *************************************************************************/
    
    // used to indicate if the cache is enabled or not
    private static boolean isDisabled = false;
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets whether or not the cache of {@link FillSpan} instances is enabled.
     * 
     * @return {@code true} if the cache of {@code FillSpan} instances is
     *         enabled, otherwise {@code false}
     */
    public static final boolean isCacheEnabled() { return !isDisabled; }
    
    /**
     * Enables the caching of {@link FillSpan} instances.
     */
    public static final void enableCache() { isDisabled = false; }
    
    /** 
     * Disables the caching of {@link FillSpan} instances.
     */
    public static final void disableCache() { isDisabled = true; }
    
    /**
     * Gets the size of the cache of {@link FillSpan} instances.
     * @return the size of the cache of {@code FillSpan} instances
     */
    public static int getCacheSize() { return getCache().cache.size(); }
    
    /**
     * Clears the cache of {@link FillSpan} instances.
     */
    public static final void clearCache() { getCache().clear(); }
    
    
    /**************************************************************************
     *                                                                        *
     * Package Private API                                                    *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a {@code FillSpan} from this cache if the cache contains it,
     * otherwise adds the newly created {@code FillSpan} to the cache and
     * returns it.
     * <p>
     * <b>Note:</b> if the cache was disabled via {@link #disableCache()} then
     * this method simply returns the passed in {@code FillSpan} instance.
     * 
     * @param span - the {@code FillSpan} to get from the cache
     * @return a new {@code FillSpan}, or a previously cached 
     *         {@code FillSpan}
     */
    static final FillSpan get(FillSpan span) {
        if (isDisabled) { return span; }
        
        return getCache().getOrPut(span);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Cache API                                                              *
     *                                                                        *
     *************************************************************************/
    
    // lazy, thread safe instantiation
    private static final class Holder {
        static final FillSpanCache INSTANCE = new FillSpanCache();
    }
    
    /** 
     * Gets the cache of {@link FillSpan} instances.
     * @return the cache of {@code FillSpan} instances
     */
    private static final FillSpanCache getCache() { return Holder.INSTANCE; }
    
    /** The cache of {@link FillSpan} instances. */
    private Map<FillSpan, FillSpan> cache;
    
    /** Creates cache, only one cache is created via Holder.INSTANCE */
    private FillSpanCache() {
        cache = new HashMap<>();
    }
    
    /** All getOrPut* methods calls this method. */
    private final FillSpan getOrPut(FillSpan span) {
        FillSpan ret = cache.get(span);
        if (ret == null) {
            ret = span;
            cache.put(ret, ret);
        }
        
        return ret;
    }
    
    /** Clears the {@code FillSpanCache}. */
    final void clear() {
        if (cache != null) { cache.clear(); }
    }
    
} // class FillSpanCache

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.jhenly.juifx.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.Interpolatable;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * {@code FillSpan} represents one of the, possibly many, fills in a
 * {@link Fill} instance.
 * <p>
 * A {@code FillSpan} is made up of a <i>fill-from</i> and a <i>fill-to</i>,
 * which can be obtained via {@link #from()} and {@link #to()}, respectively.
 * <p>
 * {@code FillSpan} instances are immutable and cached
 * by this class. An instance of {@code FillSpan} can be obtained from
 * the static {@link #of(Color, Color)} method, or multiple instances of
 * {@code FillSpan} can be obtained via {@link #of(Color[], Color[]) of(Color[], Color[])} or
 * {@link #of(List, List) of(List&lt;Color&gt;, List&lt;Color&gt;)}.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see Fill
 * @see Color
 */
public final class FillSpan implements Interpolatable<Color> {
    
    /**************************************************************************
     *                                                                        *
     * Null Argument Instances                                                *
     *                                                                        *
     *************************************************************************/
    
    /** Lazy, thread safe instantiation. */
    private static final class Holder {
        static final FillSpan NULL_ARGS_INSTANCE = new FillSpan(Color.TRANSPARENT);
        static final List<FillSpan> NULL_LIST_ARGS_INSTANCE = List.of(NULL_ARGS_INSTANCE);
    }
    /**
     * Gets the {@code FillSpan} associated with {@code null} fill-from and
     * fill-to arguments.
     * @return the {@code FillSpan} associated with {@code null} fill-from
     *         and fill-to arguments
     */
    static FillSpan getNullArgsInstance() { return Holder.NULL_ARGS_INSTANCE; }
    /**
     * Gets the {@code FillSpan[]} associated with {@code null}
     * {@link #of(Color[], Color[]) FillSpan.of(from[], to[])} arguments.
     * @return the {@code FillSpan[]} associated with {@code null}
     *         {@code FillSpan.of(from[], to[])} arguments
     */
    static final List<FillSpan> getNullListArgsInstance() { return Holder.NULL_LIST_ARGS_INSTANCE; }
    
    
    /**************************************************************************
     *                                                                        *
     * Special Specifiers                                                     *
     *                                                                        *
     *************************************************************************/
    
    static final Color INHERIT = new Color(0.0, 0.0, 0.0, 0.0);
    static final Color USE_BG = new Color(0.1, 0.0, 0.0, 0.0);
    static final Color USE_TEXT = new Color(0.1, 0.1, 0.0, 0.0);
    static final Color USE_SHAPE = new Color(0.1, 0.1, 0.1, 0.0);
    
    
    /**************************************************************************
     *                                                                        *
     * Private Members                                                        *
     *                                                                        *
     *************************************************************************/
    
    // fill-from and fill-to colors
    private Color from, to;
    private boolean fromEqualsTo;
    // pre-compute hash
    private int hash;
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Creates a {@code FillSpan} with the specified fill-from and fill-to
     * colors.
     * <p>
     * This constructor's parameters can never be null.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     */
    private FillSpan(Color from, Color to) {
        this.fromEqualsTo = from.equals(to);
        if (this.fromEqualsTo) {
            this.from = this.to = from;
        } else {
            this.from = from;
            this.to = to;
        }
        
        // hash calculation is:
        // hash = 7;
        // hash = 31 * hash + from.hashCode();
        // hash = 31 * hash + to.hashCode();
        this.hash = 31 * (31 * 7 + this.from.hashCode()) + this.to.hashCode();
    }
    
    /**
     * Creates a {@code FillSpan} with fill-from and fill-to set to the same
     * specified color.
     * <p>
     * This constructor is used by {@link #of(Color, Color)} to create fill
     * spans when 'from' == 'to' is known. This constructor's parameter can
     * never be null.
     * 
     * @param same - the color to set both fill-from and fill-to to
     */
    private FillSpan(Color same) {
        this.fromEqualsTo = true;
        this.from = this.to = same;
        
        // hash calculation is:
        // hash = 7;
        // hash = 31 * hash + from.hashCode();
        // hash = 31 * hash + from.hashCode();
        final int fromHash = this.from.hashCode();
        this.hash = 31 * (31 * 7 + fromHash) + fromHash;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets the fill-from {@link Color}.
     * @return the fill-from color
     */
    public final Color from() { return from; }
    
    /**
     * Gets the fill-to {@link Color}.
     * @return the fill-to color
     */
    public final Color to() { return to; }
    
    /**
     * Gets whether this fill span's fill-from color is equal to its fill-to
     * color.
     * @return {@code true} if this fill span's fill-from color is equal to its
     *         fill-to color
     */
    public final boolean fromEqualsTo() { return fromEqualsTo; }
    
    /**
     * This method was overridden to comply with the {@link Interpolatable}
     * interface, {@link #interpolate(double)} should be preferred over this
     * method.
     * <p>
     * The {@code Color} parameter {@code fodder} is not used. Using
     * {@code null} when calling this method is advisable, i.e.
     * {@code interpolate(null, frac)}.
     * <p>
     * Returns an interpolated {@linkplain Color} along the fraction {@code frac}
     * between {@code 0.0} and {@code 1.0}. The table in the
     * {@link #interpolate(double)} method's documentation shows the expected
     * values returned by this method.
     * 
     * @param fodder - not used
     * @param frac - fraction between {@code 0.0} and {@code 1.0}
     */
    @Override
    public final Color interpolate(Color fodder, double frac) {
        return interpolate(frac);
    }
    
    /**
     * Returns an interpolated {@link Color} along the fraction {@code frac}
     * between {@code 0.0} and {@code 1.0}.
     * <p>
     * The following table shows the expected values returned by this method.
     * <p>
     * <table>
     * <style>table td { padding: 0 10 0 10; text-align: center; }</style>
     * <th>input</th><th>result</th>
     * <tr><td>{@code fromEqualsTo() == true}</td><td>{@code from()}</td></tr>
     * <tr><td>{@code frac <= 0.0}</td><td>{@code from()}</td></tr>
     * <tr><td>{@code 0.0 < frac < 1.0}</td><td>{@code from().interpolate(to(), frac)}</td></tr>
     * <tr><td>{@code frac >= 1.0}</td><td>{@code to()}</td></tr>
     * </table>
     * 
     * @param frac - fraction between {@code 0.0} and {@code 1.0}
     * @return the interpolated color
     * 
     * @see #from()
     * @see #to()
     * @see #fromEqualsTo()
     * @see Color#interpolate(Color, double)
     */
    public final Color interpolate(double frac) {
        return fromEqualsTo() ? from : from.interpolate(to, frac);
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() { return hash; }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj == null || !(obj instanceof FillSpan)) { return false; }
        
        return equals((FillSpan) obj);
    }
    
    // implementation specific equals method
    private boolean equals(FillSpan that) {
        // 'that' cannot be null!
        
        if (this.hash != that.hash) { return false; }
        if (this.fromEqualsTo != that.fromEqualsTo) { return false; }
        
        return from.equals(that.from) && to.equals(that.to);
    }
    
    /**
     * Gets the string representation of this {@code FillSpan} instance.
     * <p>
     * The string representation of a {@code FillSpan} instance follows:
     * <pre>"FillSpan [ from: &lt;Color.toString()&gt;  to: &lt;Color.toString()&gt; ]"</pre>
     * <p>
     * 
     * @return the string representation of this {@code FillSpan} instance
     */
    @Override
    public String toString() {
        return String.format("FillSpan [ from: %s  to: %s ]", from.toString(), to.toString());
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Public Static API                                                      *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    public static final FillSpan of(final Color from, final Color to) {
        final boolean fromIsNull = (from == null);
        final boolean toIsNull = (to == null);
        
        if (fromIsNull && toIsNull) {
            // if both are null then use NULL_ARGS colors
            return getNullArgsInstance();
        } else if (!fromIsNull && toIsNull) {
            // if 'to' is null then use a fill span of fill-from to fill-from
            return FillSpanCache.get(from);
        } else if (fromIsNull && !toIsNull) {
            // if 'from' is null then use a fill span of fill-to to fill-to
            return FillSpanCache.get(to);
        }
        
        if (from.equals(to)) {
            // if they equal then use a fill span of fill-from to fill-from
            return FillSpanCache.get(from);
        }
        
        return FillSpanCache.get(from, to);
    }
    
    /**
     * Gets a list of {@link FillSpan} instances containing the specified
     * fill-from and fill-to colors.
     * <p>
     * This method is a convenience method that simply wraps the specified
     * arrays in lists and calls {@link #of(List, List)}.
     * 
     * @param from - the fill-from colors
     * @param to - the fill-to colors
     * @return a list of {@code FillSpan} instances containing the specified
     *         fill-from and fill-to colors
     */
    public static final List<FillSpan> of(Color[] from, Color[] to) {
        final List<Color> fromList = (from == null) ? (List<Color>) null : Arrays.asList(from);
        final List<Color> toList = (to == null) ? (List<Color>) null : Arrays.asList(to);
        
        return of(fromList, toList);
    }
    
    /**
     * Gets a list of {@link FillSpan} instances containing the specified
     * fill-from and fill-to colors.
     * 
     * @param from - the fill-from colors
     * @param to - the fill-to colors
     * @return a list of {@code FillSpan} instances containing the specified
     *         fill-from and fill-to colors
     */
    public static final List<FillSpan> of(List<Color> from, List<Color> to) {
        final boolean fromNullEmpty = (from == null || from.isEmpty());
        final boolean toNullEmpty = (to == null || to.isEmpty());
        
        if (fromNullEmpty && toNullEmpty) {
            // if both are null or empty then return NULL_LIST_ARGS
            return getNullListArgsInstance();
        } else if (!fromNullEmpty && toNullEmpty) {
            // if 'to' is null or empty then just use fill-from list
            return createList(from, from);
        } else if (fromNullEmpty && !toNullEmpty) {
            // if 'from' is null or empty then just use fill-to list
            return createList(to, to);
        }
        
        return createList(from, to);
    }
    
    /**
     * Clears the cache of {@link FillSpan} instances.
     * <p>
     * <b>Note:</b> this method is a no-op if called after
     * {@link #disposeOfCache()}.
     */
    public static final void clearCache() { FillSpanCache.getCache().clear(); }
    
    /**
     * Disposes of the cache of {@link FillSpan} instances.
     * <p>
     * This method should only be used when no more {@code FillSpan} instances
     * are needed (for instance, when shutting down or exiting an application).
     * <p>
     * <b>Note:</b> subsequent calls to this method will have no effect,
     * however other methods in this class will exhibit undefined behavior if
     * called after this method.
     */
    public static final void disposeOfCache() { FillSpanCache.getCache().dispose(); }
    
    
    /**************************************************************************
     *                                                                        *
     * Package Static API                                                     *
     *                                                                        *
     *************************************************************************/
    
    static final List<FillSpan> replaceUseBgWithBgFills(List<FillSpan> spans, Background bg) {
        
        if (spans == null || spans.isEmpty()) { return Holder.NULL_LIST_ARGS_INSTANCE; }
        if (bg == null || bg.getFills() == null) { return spans; }
        
        return replaceWithBgFillsHelper(spans, bg.getFills());
    }
    
    private static final List<FillSpan> replaceWithBgFillsHelper(List<FillSpan> spans, List<BackgroundFill> bgFills) {
        final int bgFillsSize = bgFills.size();
        final int spansSize = spans.size();
        
        if (bgFillsSize > spansSize) {
            return replaceHelperBgLarger(spans, bgFills);
        } else if (bgFillsSize < spansSize) {
            //
            return replaceHelperBgSmaller(spans, bgFills);
        } else {
            return replaceHelperBgSameSize(spans, bgFills);
        }
        
    }
    
    private static final Color getBgFillColor(Color color, List<BackgroundFill> fills, int index) {
        if (color != USE_BG) { return color; }
        
        try {
            return (Color) fills.get(index).getFill();
        } catch (Exception e) {
            return USE_BG;
        }
    }
    
    private static final List<FillSpan> replaceHelperBgSameSize(List<FillSpan> spans, List<BackgroundFill> bgFills) {
        final int size = spans.size();
        final Color[] fromColors = new Color[size];
        final Color[] toColors = new Color[size];
        
        for (int i = 0; i < size; i++) {
            final FillSpan span = spans.get(size - i);
            
            Color newFrom = getBgFillColor(span.from, bgFills, size - i);
            Color newTo = getBgFillColor(span.to, bgFills, size - i);
            
            final boolean fromFail = newFrom == USE_BG;
            final boolean toFail = newTo == USE_BG;
            
            if (fromFail && toFail) {
                newFrom = newTo = Color.TRANSPARENT;
            } else if (!fromFail && toFail) {
                newTo = newFrom;
            } else if (fromFail && !toFail) {
                newFrom = newTo;
            }
            
            fromColors[(size - 1) - i] = newFrom;
            toColors[(size - 1) - i] = newTo;
        }
        
        return of(fromColors, toColors);
    }
    
    private static final List<FillSpan> replaceHelperBgLarger(List<FillSpan> spans, List<BackgroundFill> bgFills) {
        final int ssize = spans.size();
        final int bgSize = bgFills.size();
        final Color[] fromColors = new Color[ssize];
        final Color[] toColors = new Color[ssize];
        
        for (int i = 0; i < ssize; i++) {
            final FillSpan span = spans.get(ssize - i);
            
            Color newFrom = getBgFillColor(span.from, bgFills, bgSize - i);
            Color newTo = getBgFillColor(span.to, bgFills, bgSize - i);
            
            final boolean fromFail = newFrom == USE_BG;
            final boolean toFail = newTo == USE_BG;
            
            if (fromFail && toFail) {
                newFrom = newTo = Color.TRANSPARENT;
            } else if (!fromFail && toFail) {
                newTo = newFrom;
            } else if (fromFail && !toFail) {
                newFrom = newTo;
            }
            
            fromColors[(ssize - 1) - i] = newFrom;
            toColors[(ssize - 1) - i] = newTo;
        }
        
        return of(fromColors, toColors);
    }
    
    private static final List<FillSpan> replaceHelperBgSmaller(List<FillSpan> spans, List<BackgroundFill> bgFills) {
        final int ssize = spans.size();
        final int bgSize = bgFills.size();
        final Color[] fromColors = new Color[ssize];
        final Color[] toColors = new Color[ssize];
        
        for (int i = 0; i < bgSize; i++) {
            final FillSpan span = spans.get(ssize - i);
            
            Color newFrom = getBgFillColor(span.from, bgFills, bgSize - i);
            Color newTo = getBgFillColor(span.to, bgFills, bgSize - i);
            
            final boolean fromFail = newFrom == USE_BG;
            final boolean toFail = newTo == USE_BG;
            
            if (fromFail && toFail) {
                newFrom = newTo = Color.TRANSPARENT;
            } else if (!fromFail && toFail) {
                newTo = newFrom;
            } else if (fromFail && !toFail) {
                newFrom = newTo;
            }
            
            fromColors[(ssize - 1) - i] = newFrom;
            toColors[(ssize - 1) - i] = newTo;
        }
        
        // copy remaining span colors, replace USE_BG with TRANSPARENT
        for (int i = 0, n = ssize - bgSize; i < n; i++) {
            final FillSpan span = spans.get(i);
            
            Color from = (span.from == USE_BG) ? Color.TRANSPARENT : span.from;
            Color to = (span.to == USE_BG) ? Color.TRANSPARENT : span.to;
            
            fromColors[i] = from;
            toColors[i] = to;
        }
        
        return of(fromColors, toColors);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Private Static API                                                     *
     *                                                                        *
     *************************************************************************/
    
    /** Helper that creates a List<FillSpan> from two Color lists. */
    private static final List<FillSpan> createList(List<Color> from, List<Color> to) {
        int diff = from.size() - to.size();
        
        if (diff != 0) {
            // return array created from lists with different lengths
            return createListFromStaggered(from, to);
        }
        
        // both color arrays are same size, so just create FillSpans from both
        List<FillSpan> ret = new ArrayList<>(from.size());
        for (int i = 0, n = from.size(); i < n; i++) {
            ret.add(of(from.get(i), to.get(i)));
        }
        
        return ret;
    }
    
    /** Helper that creates a List<FillSpan> from different sized Color lists. */
    private static final List<FillSpan> createListFromStaggered(List<Color> from, List<Color> to) {
        List<Color> larger = (from.size() > to.size()) ? from : to;
        List<Color> smaller = (from.size() < to.size()) ? from : to;
        
        FillSpan[] ret = new FillSpan[larger.size()];
        // create spans using from and to until smaller array length is reached
        for (int i = 0, n = smaller.size(); i < n; i++) {
            ret[i] = of(from.get(i), to.get(i));
        }
        
        boolean fromIsLarger = (larger == from);
        // create spans using the larger array's colors for 'from' and 'to'
        for (int i = smaller.size(), n = larger.size(); i < n; i++) {
            Color which = (fromIsLarger) ? from.get(i) : to.get(i);
            
            ret[i] = of(which, which);
        }
        
        return new ArrayList<FillSpan>(Arrays.asList(ret));
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Fill Span Cache                                                        *
     *                                                                        *
     *************************************************************************/
    
    /** Class that caches {@code FillSpan} instances. */
    private static final class FillSpanCache {
        
        // lazy, thread safe instantiation
        private static final class Holder {
            static final FillSpanCache INSTANCE = new FillSpanCache();
        }
        
        /** 
         * Gets the cache of {@link FillSpan} instances.
         * @return the cache of {@code FillSpan} instances
         */
        static final FillSpanCache getCache() { return Holder.INSTANCE; }
        
        /**
         * Gets a {@code FillSpan} with the specified colors from this cache if
         * the cache contains it, otherwise a new {@code FillSpan} is created
         * with the specified colors, then added to the cache and returned.
         * 
         * @param from - the fill-from color
         * @param to - the fill-to color
         * @return a new {@code FillSpan} created from the specified colors, or
         *         a previously cached {@code FillSpan} with the specified
         *         colors
         */
        static final FillSpan get(Color from, Color to) { return Holder.INSTANCE.getOrPut(from, to); }
        
        /**
         * Gets a {@code FillSpan} that has the same fill-from and fill-to
         * color from this cache if the cache contains it, otherwise a new
         * {@code FillSpan} is created with the specified color, added to the
         * cache and returned.
         * 
         * @param same - the fill-from and fill-to color
         * @return a new {@code FillSpan} created from the specified color, or
         *         a previously cached {@code FillSpan} with the specified
         *         color
         */
        static final FillSpan get(Color same) { return Holder.INSTANCE.getOrPut(same); }
        
        
        // the FillSpan cache
        private Map<FillSpan, FillSpan> cache;
        
        // creates cache, only one cache is created via Holder.INSTANCE
        private FillSpanCache() { cache = new HashMap<>(); }
        
        /**
         * Gets a {@code FillSpan} with the specified colors from this cache if
         * the cache contains it, otherwise a new {@code FillSpan} is created
         * with the specified colors, added to the cache and returned.
         * 
         * @param from - the fill-from color
         * @param to - the fill-to color
         * @return a new {@code FillSpan} created from the specified colors, or
         *         a previously cached {@code FillSpan} with the specified
         *         colors
         */
        final FillSpan getOrPut(Color from, Color to) {
            if (from == USE_BG || to == USE_BG) {
                // don't cache fill spans with USE_BACKGROUND
                return new FillSpan(from, to);
            }
            
            return getOrPut(new FillSpan(from, to));
        }
        
        /**
         * Gets a {@code FillSpan} that has the same fill-from and fill-to
         * color from this cache if the cache contains it, otherwise a new
         * {@code FillSpan} is created with the specified color, added to the
         * cache and returned.
         * 
         * @param same - the fill-from and fill-to color
         * @return a new {@code FillSpan} created from the specified color, or
         *         a previously cached {@code FillSpan} with the specified
         *         color
         */
        final FillSpan getOrPut(Color same) {
            // don't cache fill spans with USE_BACKGROUND
            if (same == USE_BG) { return new FillSpan(same); }
            
            return getOrPut(new FillSpan(same));
            
        }
        
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
        
        /** CLears and sets {@code FillSpanCache#cache} to {@code null}. */
        final void dispose() {
            clearCache();
            cache = null;
        }
        
    } // class FillSpanCache
    
    
}

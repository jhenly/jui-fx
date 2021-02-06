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
package com.jhenly.juifx.fill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jhenly.juifx.control.Fillable;

import javafx.animation.Interpolatable;
import javafx.scene.paint.Color;

/**
 * A {@code FillSpan} represents one of the, possibly many, fills in a
 * {@link Fill} instance.
 * <p>
 * The {@code FillSpan} object is made up of a <i>fill-from</i> and a
 * <i>fill-to</i>, which can be obtained via {@link #from()} and {@link #to()},
 * respectively. A {@code FillSpan} instance can also be interpolated via
 * {@link #interpolate(Color, double)} or {@link #interpolate(double)}.
 * <p>
 * {@code FillSpan} instances are immutable and cached by the
 * {@link FillSpanCache} class. An instance of {@code FillSpan} can be obtained
 * from the static {@link #of(Color, Color)} method, or multiple instances of
 * {@code FillSpan} can be obtained via
 * {@link #of(Color[], Color[]) of(Color[], Color[])} or
 * {@link #of(List, List) of(List&lt;Color&gt;, List&lt;Color&gt;)}.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see Fill
 * @see Color
 */
public class FillSpan implements Interpolatable<Color> {
    
    /**************************************************************************
     *                                                                        *
     * Null Argument Instances                                                *
     *                                                                        *
     *************************************************************************/
    
    /** Lazy, thread safe instantiation. */
    static final class Holder {
        static final FillSpan NULL_ARGS_INSTANCE = new FillSpan();
        static final List<FillSpan> NULL_LIST_ARGS_INSTANCE = List.of();
        
        private Holder() { throw new IllegalAccessError("a Holder class should not be instantiated"); }
    }
    /**
     * Gets the {@code FillSpan} associated with {@code null} fill-from and
     * fill-to arguments.
     * @return the {@code FillSpan} associated with {@code null} fill-from
     *         and fill-to arguments
     */
    static final FillSpan getNullArgsInstance() { return Holder.NULL_ARGS_INSTANCE; }
    /**
     * Gets the {@code FillSpan[]} associated with {@code null}
     * {@link #of(Color[], Color[]) FillSpan.of(from[], to[])} arguments.
     * @return the {@code FillSpan[]} associated with {@code null}
     *         {@code FillSpan.of(from[], to[])} arguments
     */
    static final List<FillSpan> getNullListArgsInstance() { return Holder.NULL_LIST_ARGS_INSTANCE; }
    
    
    /**************************************************************************
     *                                                                        *
     * Special Identifiers                                                    *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Special indicator that indicates to use the {@link Fillable} instance's
     * text fill color for either fill-from or fill-to.
     */
    public static final Color USE_TEXT = new Color(0.02, 0.0, 0.0, 0.0);
    /**
     * Special indicator that indicates to use the {@link Fillable} instance's
     * shape fill color for either fill-from or fill-to.
     */
    public static final Color USE_SHAPE = new Color(0.03, 0.0, 0.0, 0.0);
    /**
     * Special indicator that indicates to use the {@link Fillable} instance's
     * shape's stroke fill color for either fill-from or fill-to.
     */
    public static final Color USE_STROKE = new Color(0.04, 0.0, 0.0, 0.0);
    /**
     * Special indicator that indicates to use one of a {@link Fillable}
     * instance's background fill colors for either fill-from or fill-to.
     */
    public static final Color USE_BG = new Color(0.05, 0.0, 0.0, 0.0);
    /**
     * Special indicator that indicates to use one of a {@link Fillable}
     * instance's border stroke colors for either fill-from or fill-to.
     */
    public static final Color USE_BORDER = new Color(0.06, 0.0, 0.0, 0.0);
    
    /**
     * Checks whether a specified {@code Color} instance is a special
     * identifier.
     * @param c - the color to check
     * @return {@code true} if the specified color is a special identifier,
     *         otherwise {@code false}
     */
    static boolean colorIsSpecialIdentifier(final Color c) {
        if (c == null) { return false; }
        return c == USE_BG || c == USE_TEXT || c == USE_BORDER || c == USE_SHAPE || c == USE_STROKE;
    }
    
    /**
     * Used to indicate which border stroke position either fill-from or
     * fill-to should be replaced with, when fill-from or fill-to is set to
     * {@link FillSpan#USE_BORDER}.
     * <p>
     * The border stroke position can also be indicated through CSS:<pre>
     * .some-class {
     *     -jui-bg-from: "border[t:3], afafaf";
     *     -jui-bg-to: "border[r], fafafa";
     * }</pre>
     * Where {@code "border[t:3]"} is specifying to use the {@link #TOP} border
     * stroke, of the third to last border stroke, in the {@link Fillable}
     * instance's list of border strokes. Similarly, {@code "border[r]"} is
     * specifying to use the {@link #RIGHT} border stroke, of the second to last
     * border stroke, in said list of border strokes.
     * 
     * @see FillSpan#of(Color, Color, int, int, BorderStrokePosition, BorderStrokePosition)
     * @see FillSpan#of(Color, Color, BorderStrokePosition, BorderStrokePosition)
     */
    public static enum BorderStrokePosition {
        /** Indicates the top border stroke should be used. */
        TOP,
        /** Indicates the right border stroke should be used. */
        RIGHT,
        /** Indicates the bottom border stroke should be used. */
        BOTTOM,
        /** Indicates the left border stroke should be used. */
        LEFT;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Private Members                                                        *
     *                                                                        *
     *************************************************************************/
    
    // fill-from and fill-to colors
    private final Color from, to;
    private final boolean fromEqualsTo;
    // pre-compute hash
    private final int hash;
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Do not use this constructor, this constructor is only invoked to create
     * the {@code FillSpan} used by {@link Holder#NULL_ARGS_INSTANCE}.
     */
    private FillSpan() {
        this.from = this.to = Color.TRANSPARENT;
        this.fromEqualsTo = true;
        this.hash = 1;
    }
    
    /**
     * Creates a {@code FillSpan} with the specified fill-from and fill-to
     * colors.
     * <p>
     * This constructor's parameters can never be null.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     */
    FillSpan(Color from, Color to) {
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
    FillSpan(Color same) {
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
     * Gets an interpolated {@linkplain Color} between {@link #from()} and
     * {@link #to()} along the fraction {@code frac} between {@code 0.0} and
     * {@code 1.0}.
     * <p>
     * This method was overridden to comply with the {@link Interpolatable}
     * interface, {@link #interpolate(double)} should be preferred over this
     * method. The {@code Color} parameter {@code fodder} is not used in any
     * way by this method. Using {@code null} when calling this method is
     * advisable, i.e. {@code interpolate(null, frac)}.
     * <p>
     * The table in the {@link #interpolate(double)} method's documentation
     * shows the expected values returned by this method.
     * 
     * @param fodder - not used
     * @param frac - fraction between {@code 0.0} and {@code 1.0}
     * @return the interpolated {@code Color} between {@code this.from()} and
     *         {@code this.to()}
     * @see Color#interpolate(Color, double)
     */
    @Override
    public final Color interpolate(Color fodder, double frac) { return interpolate(frac); }
    
    /**
     * Gets an interpolated {@linkplain Color} between {@link #from()} and
     * {@link #to()} along the fraction {@code frac} between {@code 0.0} and
     * {@code 1.0}.
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
     * @return the interpolated {@code Color} between {@code this.from()} and
     *         {@code this.to()}
     * 
     * @see #from()
     * @see #to()
     * @see #fromEqualsTo()
     * @see Color#interpolate(Color, double)
     */
    public final Color interpolate(double frac) { return fromEqualsTo() ? from : from.interpolate(to, frac); }
    
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
    
    /**
     * Indicates whether a specified {@code FillSpan} instance is "equal to"
     * this one.
     * @param that - the non-{@code null} fill span to check for equality
     * @return {@code true} if the specified fill span is equal to this fill
     *         span
     * @throws NullPointerException if the specified {@code FillSpan} instance
     *         is {@code null}
     */
    boolean equals(FillSpan that) {
        // we precompute hash, so this check can be fast
        if (this.hash != that.hash) { return false; }
        if (this.fromEqualsTo != that.fromEqualsTo) { return false; }
        
        return from.equals(that.from) && to.equals(that.to);
    }
    
    /**
     * Gets the string representation of this {@code FillSpan} instance.
     * <p>
     * The string representation of a {@code FillSpan} instance follows:
     * <pre>"FillSpan [ from: &lt;Color.toString()&gt;, to: &lt;Color.toString()&gt; ]"</pre>
     * <p>
     * 
     * @return the string representation of this {@code FillSpan} instance
     */
    @Override
    public String toString() { return String.format("FillSpan [ from: %s, to: %s ]", from.toString(), to.toString()); }
    
    
    /**************************************************************************
     *                                                                        *
     * Public Static API                                                      *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * <p>
     * If {@code from} and {@code to} are {@code null} then a fill span with a
     * fill-from and fill-to of {@link Color#TRANSPARENT} will be returned. If
     * only {@code from} is {@code null} then {@code from} will be set to
     * {@code to}. If only {@code to} is {@code null} then {@code to} will be
     * set to {@code from}.
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
            // if both are null then use NULL_ARGS color
            return getNullArgsInstance();
        } else if (!fromIsNull && toIsNull) {
            // if 'to' is null then use a fill span of fill-from to fill-from
            return of(from);
        } else if (fromIsNull && !toIsNull) {
            // if 'from' is null then use a fill span of fill-to to fill-to
            return of(to);
        }
        
        if (from.equals(to)) {
            // if they equal then use a fill span of fill-from to fill-from
            return of(from);
        }
        
        final boolean fromIsSpec = colorIsSpecialIdentifier(from);
        final boolean toIsSpec = colorIsSpecialIdentifier(to);
        
        // get a special fill span from the cache if from or to are special
        return (fromIsSpec || toIsSpec) ? SpecialFillSpan.of(from, to, fromIsSpec, toIsSpec) : getFromCache(from, to);
    }
    
    /**
     * This method does not check for null, helper that gets a fill span with
     * fill-from equal to fill-to.
     * 
     * @param same - the color to set 'from' and 'to' to
     * @return a fill span
     */
    private static final FillSpan of(final Color same) {
        return colorIsSpecialIdentifier(same) ? SpecialFillSpan.of(same) : getFromCache(same);
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors,
     * and the specified from and to indexes.
     * <p>
     * This method is specifically meant to be used when either {@code from} or
     * {@code to} is a list based special identifier, such as {@link #USE_BG},
     * {@link #USE_BORDER}. If neither {@code from} nor {@code to} is a list
     * based special identifier then this method just returns
     * {@link #of(Color, Color) of(from, to)}. If {@code from} is {@code null}
     * then {@code from} will be set to {@code to}. If {@code to} is
     * {@code null} then {@code to} will be set to {@code from}.
     * <p>
     * The specified indexes must be in the range {@code -1} to {@code 254},
     * otherwise the indexes will be set to {@code -1}. An index of {@code -1}
     * signals to use the innermost background fill or border stroke, for a
     * single {@code FillSpan}, or to use the index of the {@code FillSpan} in
     * a list as the index of the background fill or border stroke to replace
     * it with. Which is the default behavior.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @param fIndex - the index of the background fill or border stroke to
     *        replace fill-from with, must be less than or equal to {@code 255}
     *        and greater than or equal to {@code -1}
     * @param tIndex - the index of the background fill or border stroke to
     *        replace fill-to with, must be less than or equal to {@code 255}
     *        and greater than or equal to {@code -1}
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    public static final FillSpan of(Color from, Color to, int fIndex, int tIndex) {
        // return of(...) if neither from nor to is list based spec. id.
        if ((from != USE_BG || from != USE_BORDER) && (to != USE_BG || to != USE_BORDER)) { return of(from, to); }
        
        // index bounds checking
        fIndex = (fIndex < -1 || fIndex > 254) ? -1 : fIndex;
        tIndex = (tIndex < -1 || tIndex > 254) ? -1 : tIndex;
        
        final boolean fromIsNull = (from == null);
        final boolean toIsNull = (to == null);
        
        if (!fromIsNull && toIsNull) {
            // if 'to' is null then use a fill span of fill-from to fill-from
            return of(from, fIndex);
        } else if (fromIsNull && !toIsNull) {
            // if 'from' is null then use a fill span of fill-to to fill-to
            return of(to, tIndex);
        }
        
        if (from.equals(to) && fIndex == tIndex) {
            // if they equal then use a fill span of fill-from to fill-from
            return of(from, fIndex);
        }
        
        final boolean fromIsSpec = colorIsSpecialIdentifier(from);
        final boolean toIsSpec = colorIsSpecialIdentifier(to);
        
        // get a special list based fill span from the cache
        return SpecialFillSpan.of(from, to, fromIsSpec, toIsSpec, fIndex, tIndex);
    }
    
    /** Helper for special list based 'of' for 'same' fill-from and fill-to. */
    private static final FillSpan of(Color same, int index) {
        return SpecialFillSpan.of(same, index);
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors,
     * and the specified from and to border stroke positions.
     * <p>
     * This method is specifically meant to be used when either {@code from} or
     * {@code to} is {@link #USE_BORDER}. If neither {@code from} nor
     * {@code to} is {@link #USE_BORDER} then this method just returns
     * {@link #of(Color, Color, int, int) of(from, to, fIndex, tIndex)}. If
     * {@code from} is {@code null} then {@code from} will be set to
     * {@code to}. If {@code to} is {@code null} then {@code to} will be set to
     * {@code from}.
     * <p>
     * This method is similar to
     * {@link #of(Color, Color, int, int, BorderStrokePosition, BorderStrokePosition)}
     * , but the innermost border stroke will be used rather than any specified
     * border stroke indexes.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @param fBsPos - the border stroke position to replace the color of
     *        fill-from with
     * @param tBsPos - the border stroke position to replace the color of
     *        fill-to with
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    public static final FillSpan of(Color from, Color to, BorderStrokePosition fBsPos, BorderStrokePosition tBsPos) {
        return of(from, to, -1, -1, fBsPos, tBsPos);
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors,
     * the specified from and to indexes, and the specified from and to border
     * stroke positions.
     * <p>
     * This method is specifically meant to be used when either {@code from} or
     * {@code to} is {@link #USE_BORDER}. If neither {@code from} nor
     * {@code to} is {@link #USE_BORDER} then this method just returns
     * {@link #of(Color, Color, int, int) of(from, to, fIndex, tIndex)}. If
     * {@code from} is {@code null} then {@code from} will be set to
     * {@code to}. If {@code to} is {@code null} then {@code to} will be set to
     * {@code from}.
     * <p>
     * The specified indexes must be in the range {@code -1} to {@code 254},
     * otherwise the indexes will be set to {@code -1}. An index of {@code -1}
     * signals to use the innermost background fill or border stroke, for a
     * single {@code FillSpan}, or to use the index of the {@code FillSpan} in
     * a list as the index of the background fill or border stroke to replace
     * it with. Which is the default behavior.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @param fIndex - the index of the background fill or border stroke to
     *        replace fill-from with, must be less than or equal to {@code 255}
     *        and greater than or equal to {@code -1}
     * @param tIndex - the index of the background fill or border stroke to
     *        replace fill-to with, must be less than or equal to {@code 255}
     *        and greater than or equal to {@code -1}
     * @param fBsPos - the border stroke position to replace the color of
     *        fill-from with
     * @param tBsPos - the border stroke position to replace the color of
     *        fill-to with
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    public static final FillSpan of(Color from, Color to, int fIndex, int tIndex, BorderStrokePosition fBsPos,
        BorderStrokePosition tBsPos) {
        // return of(...) if neither from nor to is border stroke special id.
        if (from != USE_BORDER && to != USE_BORDER) { return of(from, to, fIndex, tIndex); }
        
        // index bounds checking
        fIndex = (fIndex < -1 || fIndex > 254) ? -1 : fIndex;
        tIndex = (tIndex < -1 || tIndex > 254) ? -1 : tIndex;
        
        final boolean fromIsNull = (from == null);
        final boolean toIsNull = (to == null);
        
        if (!fromIsNull && toIsNull) {
            // if 'to' is null then use a fill span of fill-from to fill-from
            return of(from, fIndex, fBsPos);
        } else if (fromIsNull && !toIsNull) {
            // if 'from' is null then use a fill span of fill-to to fill-to
            return of(to, tIndex, tBsPos);
        }
        
        if (from.equals(to) && fIndex == tIndex && fBsPos == tBsPos) {
            // if they equal then use a fill span of fill-from to fill-from
            return of(from, fIndex, fBsPos);
        }
        
        final boolean fromIsSpec = colorIsSpecialIdentifier(from);
        final boolean toIsSpec = colorIsSpecialIdentifier(to);
        
        int fPos = (fBsPos == null) ? 0 : fBsPos.ordinal();
        int tPos = (tBsPos == null) ? 0 : tBsPos.ordinal();
        
        // get a special border stroke fill span from the cache
        return SpecialFillSpan.of(from, to, fromIsSpec, toIsSpec, fIndex, tIndex, fPos, tPos);
    }
    
    /** Helper for special border stroke 'of' for 'same' fill-from and fill-to. */
    private static final FillSpan of(Color same, int index, BorderStrokePosition bsPos) {
        return SpecialFillSpan.of(same, index, bsPos == null ? 0 : bsPos.ordinal());
    }
    
    /**
     * Gets a list of {@link FillSpan} instances containing the specified
     * fill-from and fill-to colors.
     * <p>
     * This method is a convenience method that simply converts the specified
     * arrays to lists and calls {@link #of(List, List)}.
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
            
            // we didn't check for null so use of(from, to) instead of of(same)
            ret[i] = of(which, which);
        }
        
        return new ArrayList<FillSpan>(Arrays.asList(ret));
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Fill Span Cache                                                        *
     *                                                                        *
     *************************************************************************/
    
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
    static final FillSpan getFromCache(Color from, Color to) { return FillSpanCache.get(new FillSpan(from, to)); }
    
    /**
     * Gets a {@code FillSpan} that has the same fill-from and fill-to color
     * from this cache if the cache contains it, otherwise a new
     * {@code FillSpan} is created with the specified color, added to the
     * cache and returned.
     * 
     * @param same - the fill-from and fill-to color
     * @return a new {@code FillSpan} created from the specified color, or
     *         a previously cached {@code FillSpan} with the specified
     *         color
     */
    static final FillSpan getFromCache(Color same) { return FillSpanCache.get(new FillSpan(same)); }
    
    
    /**
     * Used by {@link SpecialFillSpan#of(Color, Color)} and
     * {@link SpecialFillSpan#of(Color)} to get a {@link SpecialFillSpan} from the cache.
     * 
     * @param spec - the special fill span to get
     * @return a special fill span from the cache
     */
    static final FillSpan getFromCache(SpecialFillSpan spec) { return FillSpanCache.get(spec); }
    
}

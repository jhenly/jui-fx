/** Licensed to the Apache Software Foundation (ASF) under one or more
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
 * the License. */
package impl.com.jhenly.juifx.fill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jhenly.juifx.control.Fillable;

import javafx.animation.Interpolatable;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;


/**
 * A {@code FillSpan} represents one of the, possibly many, fills in a
 * {@link Fill} instance.
 * <p>
 * The {@code FillSpan} object is made up of a <i>fill-from</i> and a
 * <i>fill-to</i>, which can be obtained via {@link #from()} and {@link #to()},
 * respectively. A {@code FillSpan} instance can also be interpolated via
 * {@link #interpolate(Paint, double)} or {@link #interpolate(double)}.
 * <p>
 * {@code FillSpan} instances are immutable and cached by the
 * {@link FillSpanCache} class. An instance of {@code FillSpan} can be obtained
 * from any of the static {@code FillSpan.of(...)} methods, or multiple
 * instances of {@code FillSpan} can be obtained via
 * {@link #of(Paint[], Paint[]) of(Paint[], Paint[])} or
 * {@link #of(List, List) of(List&lt;Paint&gt;, List&lt;Paint&gt;)}.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see Fill
 * @see Paint
 */
public class FillSpan implements Interpolatable<Paint> {
    
    /**************************************************************************
     *                                                                        *
     * Null Argument Instances                                                *
     *                                                                        *
     *************************************************************************/
    
    /** Lazy, thread safe instantiation. */
    static final class Holder {
        static final FillSpan NULL_ARGS_INSTANCE = new FillSpan();
        static final List<FillSpan> NULL_LIST_ARGS_INSTANCE = List.of();
        
        private Holder() { throw new IllegalAccessError("the FillSpan.Holder class should not be instantiated"); }
    }
    /**
     * Gets the {@code FillSpan} associated with {@code null} fill-from and
     * fill-to arguments.
     * <p>
     * The returned {@code FillSpan} has a fill-from and fill-to of
     * {@link Color#TRANSPARENT}.
     * 
     * @return the {@code FillSpan} associated with {@code null} fill-from
     *         and fill-to arguments
     */
    static final FillSpan getNullArgsInstance() { return Holder.NULL_ARGS_INSTANCE; }
    /**
     * Gets the {@code FillSpan[]} associated with {@code null}
     * {@link #of(Paint[], Paint[]) FillSpan.of(from[], to[])} arguments.
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
     * Special indicator that signals to use a {@link Fillable} instance's
     * text fill paint for either fill-from or fill-to.
     */
    public static final Paint USE_TEXT = new Color(0.02, 0.0, 0.0, 0.0);
    /**
     * Special indicator that signals to use a {@link Fillable} instance's
     * shape fill paint for either fill-from or fill-to.
     */
    public static final Paint USE_SHAPE = new Color(0.03, 0.0, 0.0, 0.0);
    /**
     * Special indicator that signals to use a {@link Fillable} instance's
     * shape's stroke fill paints for either fill-from or fill-to.
     */
    public static final Paint USE_STROKE = new Color(0.04, 0.0, 0.0, 0.0);
    /**
     * Special indicator that signals to use one of a {@link Fillable}
     * instance's background fill paints for either fill-from or fill-to.
     */
    public static final Paint USE_BG = new Color(0.05, 0.0, 0.0, 0.0);
    /**
     * Special indicator that signals to use one of a {@link Fillable}
     * instance's border stroke paints for either fill-from or fill-to.
     */
    public static final Paint USE_BORDER = new Color(0.06, 0.0, 0.0, 0.0);
    /**
     * Special indicator that signals to use a {@link Fillable} instance's
     * property's existing {@link Paint}.
     * <p>
     * This is useful in situations where a {@link Fillable} instance's
     * list of {@link BackgroundFill} instances may contain a {@code Paint}
     * that cannot be cast to a {@link Color}, such as a {@link LinearGradient}
     * or {@link RadialGradient}. Rather than replacing the {@code Paint} with
     * an interpolated {@code Color}, it is just skipped.
     * <p>
     * <b>Note:</b> specifying {@code SKIP} as a fill-from or fill-to
     * {@code Color} will assign both fill-from and fill-to to {@code SKIP}.
     */
    public static final Paint SKIP = new Color(0.07, 0.0, 0.0, 0.0);
    
    /**
     * Checks whether a specified {@code Paint} instance is a special
     * identifier.
     * @param p - the {@code Paint} to check
     * @return {@code true} if the specified {@code Paint} is a special
     *         identifier, otherwise {@code false}
     */
    static boolean paintIsSpecialIdentifier(final Paint p) {
        if (p == null) { return false; }
        return p == SKIP || p == USE_BG || p == USE_TEXT || p == USE_BORDER || p == USE_SHAPE || p == USE_STROKE;
    }
    
    /**
     * Used to indicate which border stroke position either fill-from or
     * fill-to should be replaced with, when fill-from or fill-to is set to
     * {@link FillSpan#USE_BORDER}.
     * <p>
     * The border stroke position can also be indicated through CSS:<pre>
     * .some-class {
     *     -fill-bg-from: "border[t:3], afafaf";
     *     -fill-bg-to: "fafafa, border[r]";
     * }</pre>
     * Where {@code "border[t:3]"} is specifying to use the {@link #TOP} border
     * stroke, of the third to last border stroke, in the {@link Fillable}
     * instance's list of border strokes. Similarly, {@code "border[r]"} is
     * specifying to use the {@link #RIGHT} border stroke, of the second to last
     * border stroke, in said list of border strokes.
     * 
     * @see FillSpan#of(Paint, Paint, int, int, BorderStrokePosition, BorderStrokePosition)
     * @see FillSpan#of(Paint, Paint, BorderStrokePosition, BorderStrokePosition)
     */
    public enum BorderStrokePosition {
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
    protected final Paint from, to;
    private final boolean fromEqualsTo;
    // pre-compute hash
    protected int hash;
    
    
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
        from = to = Color.TRANSPARENT;
        fromEqualsTo = true;
        hash = 1;
    }
    
    /**
     * Creates a {@code FillSpan} with the specified fill-from and fill-to
     * {@code Paint} instances.
     * <p>
     * This constructor's parameters can never be null.
     * 
     * @param from - the fill-from paint
     * @param to - the fill-to paint
     */
    protected FillSpan(Paint from, Paint to) {
        fromEqualsTo = from.equals(to);
        if (fromEqualsTo) {
            this.from = this.to = from;
        } else {
            this.from = from;
            this.to = to;
        }
        
        // hash calculation is:
        // hash = 7;
        // hash = 31 * hash + from.hashCode();
        // hash = 31 * hash + to.hashCode();
        hash = 31 * (31 * 7 + this.from.hashCode()) + this.to.hashCode();
    }
    
    /**
     * Creates a {@code FillSpan} with fill-from and fill-to set to the same
     * specified {@code Paint} instance.
     * <p>
     * This constructor is used by {@link #of(Paint, Paint)} to create fill
     * spans when {@code from.equals(to)} is {@code true}. This constructor's
     * parameter can never be null.
     * 
     * @param same - the {@code Paint} to set both fill-from and fill-to to
     */
    protected FillSpan(Paint same) {
        fromEqualsTo = true;
        from = to = same;
        
        // hash calculation is:
        // hash = 7;
        // hash = 31 * hash + from.hashCode();
        // hash = 31 * hash + from.hashCode();
        final int fromHash = from.hashCode();
        hash = 31 * (31 * 7 + fromHash) + fromHash;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
//
    /**
     * Gets the fill-from {@link Paint}.
     * @return the fill-from paint
     */
    public Paint from() { return from; }
    
    /**
     * Gets the fill-to {@link Paint}.
     * @return the fill-to paint
     */
    public Paint to() { return to; }
    
    /**
     * Gets whether this fill span's fill-from is equal to its fill-to.
     * @return {@code true} if this fill span's fill-from is equal to its
     *         fill-to.
     */
    public final boolean fromEqualsTo() { return fromEqualsTo; }
    
    /**
     * Gets an interpolated {@linkplain Paint} between {@link #from()} and
     * {@link #to()} along the fraction {@code frac} between {@code 0.0} and
     * {@code 1.0}.
     * <p>
     * This method was overridden to comply with the {@link Interpolatable}
     * interface, {@link #interpolate(double)} should be preferred over this
     * method. The {@code Paint} parameter {@code fodder} is not used in any
     * way by this method. Using {@code null} when calling this method is
     * advisable, i.e. {@code interpolate(null, frac)}.
     * <p>
     * The table in the {@link #interpolate(double)} method's documentation
     * shows the expected values returned by this method.
     * 
     * @param fodder - not used
     * @param frac - fraction between {@code 0.0} and {@code 1.0}
     * @return the interpolated {@code Paint} between {@code this.from()} and
     *         {@code this.to()}
     */
    @Override
    public final Paint interpolate(Paint fodder, double frac) { return interpolate(frac); }
    
    /**
     * Gets an interpolated {@linkplain Paint} between {@link #from()} and
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
     * @return the interpolated {@code Paint} between {@code this.from()} and
     *         {@code this.to()}
     * 
     * @see #from()
     * @see #to()
     * @see #fromEqualsTo()
     */
    public final Paint interpolate(double frac) {
        if (fromEqualsTo || frac <= 0.0) { return from; }
        if (frac >= 1.0) { return to; }
        
        return interpolateImpl(frac);
    }
    
    /**
     * Invoked by {@link #interpolate(double)} after checking
     * {@code fromEqualsTo} and {@code frac}, subclasses should override this
     * method with implementation specific interpolate logic.
     * 
     * @param frac - fraction between {@code 0.0} and {@code 1.0}
     * @return the interpolated {@code Paint} between {@code this.from()} and
     *         {@code this.to()}
     */
    protected Paint interpolateImpl(double frac) {
        return ((Color) from).interpolate((Color) to, frac);
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() { return hash; }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        // null check not needed, instanceof returns false when obj is null
        if (!(obj instanceof FillSpan)) { return false; }
        
        FillSpan that = (FillSpan) obj;
        // we precompute hash, so this check can be fast
        if (hash != that.hash) { return false; }
        if (fromEqualsTo != that.fromEqualsTo || isSpecial() != that.isSpecial()) { return false; }
        
        return equals(that);
    }
    
    /**
     * Indicates whether a specified {@code FillSpan} instance is "equal to"
     * this one.
     * @param that - the non-{@code null} fill span to check for equality
     * @return {@code true} if the specified fill span is equal to this fill
     *         span, otherwise {@code false}
     * @throws NullPointerException if the specified {@code FillSpan} instance
     *         is {@code null}
     */
    boolean equals(FillSpan that) {
        return from.equals(that.from) && to.equals(that.to);
    }
    
    /**
     * Gets the string representation of this {@code FillSpan} instance.
     * <p>
     * The string representation of a {@code FillSpan} instance follows:
     * <pre>"FillSpan [ from: &lt;Paint.toString()&gt;, to: &lt;Paint.toString()&gt; ]"</pre>
     * <p>
     * 
     * @return the string representation of this {@code FillSpan} instance
     */
    @Override
    public String toString() { return String.format("FillSpan [ from: %s, to: %s ]", from, to); }
    
    /**
     * Gets whether or not this {@code FillSpan} instance is a
     * {@link SpecialFillSpan}.
     * 
     * @return {@code true} if this fill span is special, otherwise
     *         {@code false}
     */
    boolean isSpecial() { return false; }
    
    /**************************************************************************
     *                                                                        *
     * Public Static API                                                      *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to
     * {@link Paint} instances.
     * <p>
     * If {@code from} and {@code to} are {@code null} then a fill span with a
     * fill-from and fill-to of {@link Color#TRANSPARENT} will be returned. If
     * only {@code from} is {@code null} then {@code from} will be set to
     * {@code to}. If only {@code to} is {@code null} then {@code to} will be
     * set to {@code from}.
     * 
     * @param from - the fill-from paint
     * @param to - the fill-to paint
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     */
    public static final FillSpan of(final Paint from, final Paint to) {
        // have to check for SKIP before anything else
        if (from == SKIP || to == SKIP) { return of(SKIP); }
        
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
        
        final boolean fromIsSpec = paintIsSpecialIdentifier(from);
        final boolean toIsSpec = paintIsSpecialIdentifier(to);
        
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
    private static final FillSpan of(final Paint same) {
        return paintIsSpecialIdentifier(same) ? SpecialFillSpan.of(same) : getFromCache(same);
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors,
     * and the specified from and to indexes.
     * <p>
     * This method is specifically meant to be used when either {@code from} or
     * {@code to} is a list based special identifier, such as {@link #USE_BG}
     * or {@link #USE_BORDER}. If neither {@code from} nor {@code to} is a list
     * based special identifier then this method just returns
     * {@link #of(Paint, Paint) of(from, to)}. If {@code from} is {@code null}
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
     * @param from - the fill-from paint
     * @param to - the fill-to paint
     * @param fIndex - the index of the background fill or border stroke to
     *        replace fill-from with, must be less than or equal to {@code 254}
     *        and greater than or equal to {@code -1}
     * @param tIndex - the index of the background fill or border stroke to
     *        replace fill-to with, must be less than or equal to {@code 254}
     *        and greater than or equal to {@code -1}
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         paints
     */
    public static final FillSpan of(Paint from, Paint to, int fIndex, int tIndex) {
        // have to check for SKIP before anything else
        if (from == SKIP || to == SKIP) { return of(SKIP); }
        
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
        
        final boolean fromIsSpec = paintIsSpecialIdentifier(from);
        final boolean toIsSpec = paintIsSpecialIdentifier(to);
        
        // get a special list based fill span from the cache
        return SpecialFillSpan.of(from, to, fromIsSpec, toIsSpec, fIndex, tIndex);
    }
    
    /** Helper for special list based 'of' for 'same' fill-from and fill-to. */
    private static final FillSpan of(Paint same, int index) {
        return SpecialFillSpan.of(same, index);
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors,
     * and the specified from and to border stroke positions.
     * <p>
     * This method is specifically meant to be used when either {@code from} or
     * {@code to} is {@link #USE_BORDER}. If neither {@code from} nor
     * {@code to} is {@link #USE_BORDER} then this method just returns
     * {@link #of(Paint, Paint, int, int) of(from, to, fIndex, tIndex)}. If
     * {@code from} is {@code null} then {@code from} will be set to
     * {@code to}. If {@code to} is {@code null} then {@code to} will be set to
     * {@code from}.
     * <p>
     * This method is similar to
     * {@link #of(Paint, Paint, int, int, BorderStrokePosition, BorderStrokePosition)}
     * , but the innermost border stroke will be used rather than any specified
     * border stroke indexes.
     * 
     * @param from - the fill-from paint
     * @param to - the fill-to paint
     * @param fBsPos - the border stroke position to replace the paint of
     *        fill-from with
     * @param tBsPos - the border stroke position to replace the paint of
     *        fill-to with
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         paints
     */
    public static final FillSpan of(Paint from, Paint to, BorderStrokePosition fBsPos, BorderStrokePosition tBsPos) {
        return of(from, to, -1, -1, fBsPos, tBsPos);
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to paints,
     * the specified from and to indexes, and the specified from and to border
     * stroke positions.
     * <p>
     * This method is specifically meant to be used when either {@code from} or
     * {@code to} is {@link #USE_BORDER}. If neither {@code from} nor
     * {@code to} is {@link #USE_BORDER} then this method just returns
     * {@link #of(Paint, Paint, int, int) of(from, to, fIndex, tIndex)}. If
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
     * @param from - the fill-from paint
     * @param to - the fill-to paint
     * @param fIndex - the index of the background fill or border stroke to
     *        replace fill-from with, must be less than or equal to {@code 254}
     *        and greater than or equal to {@code -1}
     * @param tIndex - the index of the background fill or border stroke to
     *        replace fill-to with, must be less than or equal to {@code 254}
     *        and greater than or equal to {@code -1}
     * @param fBsPos - the border stroke position to replace the paint of
     *        fill-from with
     * @param tBsPos - the border stroke position to replace the paint of
     *        fill-to with
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         paints
     */
    public static final FillSpan
    of(Paint from, Paint to, int fIndex, int tIndex, BorderStrokePosition fBsPos, BorderStrokePosition tBsPos)
    {
        // have to check for SKIP before anything else
        if (from == SKIP || to == SKIP) { return of(SKIP); }
        
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
        
        final boolean fromIsSpec = paintIsSpecialIdentifier(from);
        final boolean toIsSpec = paintIsSpecialIdentifier(to);
        
        // get a special border stroke fill span from the cache
        return SpecialFillSpan.of(from, to, fromIsSpec, toIsSpec, fIndex, tIndex, fBsPos, tBsPos);
    }
    
    /** Helper for special border stroke 'of' for 'same' fill-from and fill-to. */
    private static final FillSpan of(Paint same, int index, BorderStrokePosition bsPos) {
        return SpecialFillSpan.of(same, index, bsPos);
    }
    
    /**
     * Gets a list of {@link FillSpan} instances containing the specified
     * fill-from and fill-to paints.
     * <p>
     * This method is a convenience method that simply converts the specified
     * arrays to lists and calls {@link #of(List, List)}.
     * 
     * @param from - the fill-from paints
     * @param to - the fill-to paints
     * @return a list of {@code FillSpan} instances containing the specified
     *         fill-from and fill-to paints
     */
    public static final List<FillSpan> of(Paint[] from, Paint[] to) {
        final List<Paint> fromList = (from == null) ? (List<Paint>) null : Arrays.asList(from);
        final List<Paint> toList = (to == null) ? (List<Paint>) null : Arrays.asList(to);
        
        return of(fromList, toList);
    }
    
    /**
     * Gets a list of {@link FillSpan} instances containing the specified
     * fill-from and fill-to paints.
     * 
     * @param from - the fill-from paints
     * @param to - the fill-to paints
     * @return a list of {@code FillSpan} instances containing the specified
     *         fill-from and fill-to paints
     */
    public static final List<FillSpan> of(List<Paint> from, List<Paint> to) {
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
    
    /** Helper that creates a List<FillSpan> from two Paint lists. */
    private static final List<FillSpan> createList(List<Paint> from, List<Paint> to) {
        int diff = from.size() - to.size();
        
        if (diff != 0) {
            // return array created from lists with different lengths
            return createListFromStaggered(from, to);
        }
        
        // both paint arrays are same size, so just create FillSpans from both
        List<FillSpan> ret = new ArrayList<>(from.size());
        for (int i = 0, n = from.size(); i < n; i++) {
            ret.add(of(from.get(i), to.get(i)));
        }
        
        return ret;
    }
    
    /** Helper that creates a List<FillSpan> from different sized Paint lists. */
    private static final List<FillSpan> createListFromStaggered(List<Paint> from, List<Paint> to) {
        List<Paint> larger = (from.size() > to.size()) ? from : to;
        List<Paint> smaller = (from.size() < to.size()) ? from : to;
        
        FillSpan[] ret = new FillSpan[larger.size()];
        // create spans using from and to until smaller array length is reached
        for (int i = 0, n = smaller.size(); i < n; i++) {
            ret[i] = of(from.get(i), to.get(i));
        }
        
        boolean fromIsLarger = (larger == from);
        // create spans using the larger array's paints for 'from' and 'to'
        for (int i = smaller.size(), n = larger.size(); i < n; i++) {
            Paint which = (fromIsLarger) ? from.get(i) : to.get(i);
            
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
     * Gets a {@code FillSpan} with the specified paints from the cache if
     * the cache contains it, otherwise a new {@code FillSpan} is created
     * with the specified paints, then added to the cache and returned.
     * 
     * @param from - the fill-from paint
     * @param to - the fill-to paint
     * @return a new {@code FillSpan} created from the specified paints, or
     *         a previously cached {@code FillSpan} with the specified
     *         paints
     */
    static final FillSpan getFromCache(Paint from, Paint to) {
        return FillSpanCache.get(new FillSpan(from, to));
    }
    
    /**
     * Gets a {@code FillSpan} that has the same fill-from and fill-to paint
     * from the cache if the cache contains it, otherwise a new
     * {@code FillSpan} is created with the specified paint, added to the
     * cache and returned.
     * 
     * @param same - the fill-from and fill-to paints
     * @return a new {@code FillSpan} created from the specified paint, or
     *         a previously cached {@code FillSpan} with the specified
     *         paint
     */
    static final FillSpan getFromCache(Paint same) { return FillSpanCache.get(new FillSpan(same)); }
    
    /**
     * Used by implementations of {@code FillSpan} to get a cached
     * {@code FillSpan}.
     * 
     * @param span - the fill span to get
     * @return a cached {@code FillSpan} instance
     */
    @SuppressWarnings("unchecked")
    static final <T extends FillSpan> T getFromCache(T span) { return (T) FillSpanCache.get(span); }
    
}

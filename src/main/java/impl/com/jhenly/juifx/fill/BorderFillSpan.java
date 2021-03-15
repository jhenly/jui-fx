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

import static impl.com.jhenly.juifx.fill.FillSpanHelper.fillSpansAreEqual;

import javafx.animation.Interpolatable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * A {@code BorderFillSpan} represents one of the, possibly many, border fills
 * in a {@link Fill} instance.
 * <p>
 * The {@code BorderFillSpan} object is made up of top, right, bottom and
 * left {@link FillSpan} instances, which can be obtained via {@link #getTop()},
 * {@link #getRight()}, {@link #getBottom()} and {@link #getLeft()},
 * respectively.
 * <p>
 * {@code BorderFillSpan} instances are immutable. An instance of
 * {@code BorderFillSpan} can be obtained from any of the static
 * {@code BorderFillSpan.of(...)} methods. A {@code BorderFillSpan} instance
 * can also be interpolated via {@link #interpolate(Color, double)} or
 * {@link #interpolate(double)}.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see Fill
 * @see FillSpan
 */
public class BorderFillSpan implements Interpolatable<Paint[]> {
    
    /** Lazy, thread safe instantiation. */
    private static final class Holder {
        static final FillSpan DEFAULT_TOP = FillSpan.of(Color.BLACK, Color.BLACK);
        
        private Holder() { throw new IllegalAccessError("the BorderFillSpan.Holder class should not be instantiated"); }
    }
    static final FillSpan getDefaultTop() { return Holder.DEFAULT_TOP; }
    
    
    /**************************************************************************
     *                                                                        *
     * Member(s)                                                              *
     *                                                                        *
     *************************************************************************/
    
    private final FillSpan top;
    protected boolean special;
    private final int hash;
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
//    
    /**
     * Constructs a uniform {@code BorderFillSpan} with top, right, bottom
     * and left set to the specified {@code FillSpan} instance.
     * 
     * @param t - the {@code FillSpan} to use on the top
     */
    private BorderFillSpan(FillSpan t) {
        top = t;
        
        special = t.isSpecial();
        
        hash = preComputeHash();
    }
    
    /** 
     * Overridden by implementing classes to pre-compute the hash code.
     * <p>
     * Implementing classes should use the value returned by
     * {@code super.preComputeHash()} when pre-computing the hash code.
     * 
     * @return the pre-computed hash code
     */
    protected int preComputeHash() {
//        System.out.println("BorderFillSpan # preComputeHash()");
        return 31 * top.hashCode();
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
//    
    /**
     * Gets whether or not the top, right, bottom and left {@link FillSpan}
     * instances are equal.
     * 
     * @return {@code true} if top, right, bottom and left are equal, otherwise
     *         {@code false}
     */
    public boolean isUniform() { return true; }
    
    /**
     * Gets the {@link FillSpan} instance associated with the top border
     * stroke.
     * <p>
     * <b>Note:</b> this method will never return {@code null}.
     * 
     * @return the {@code FillSpan} instance associated with the top border
     *         stroke
     */
    public FillSpan getTop() { return top; }
    
    /**
     * Gets the {@link FillSpan} instance associated with the right border
     * stroke.
     * <p>
     * <b>Note:</b> this method will never return {@code null}.
     * 
     * @return the {@code FillSpan} instance associated with the right border
     *         stroke
     */
    public FillSpan getRight() { return top; }
    
    /**
     * Gets the {@link FillSpan} instance associated with the bottom border
     * stroke.
     * <p>
     * <b>Note:</b> this method will never return {@code null}.
     * 
     * @return the {@code FillSpan} instance associated with the bottom border
     *         stroke
     */
    public FillSpan getBottom() { return top; }
    
    /**
     * Gets the {@link FillSpan} instance associated with the left border
     * stroke.
     * <p>
     * <b>Note:</b> this method will never return {@code null}.
     * 
     * @return the {@code FillSpan} instance associated with the left border
     *         stroke
     */
    public FillSpan getLeft() { return top; }
    
    /**
     * Gets an array of {@code Color} instances interpolated from the
     * {@code FillSpan} instances that make up this {@code BorderFillSpan}
     * instance.
     * <p>
     * This method was overridden to comply with the {@link Interpolatable}
     * interface, {@link #interpolate(double)} should be preferred over this
     * method. The {@code Color[]} parameter {@code fodder} is not used in any
     * way by this method. Using {@code null} when calling this method is
     * advisable, i.e. {@code interpolate(null, frac)}.
     * 
     * @param fodder - not used
     * @param frac - fraction between {@code 0.0} and {@code 1.0}
     * @return an array of interpolated {@code Color} instances
     * @see #interpolate(double)
     * @see FillSpan#interpolate(double)
     */
    @Override
    public Paint[] interpolate(Paint[] fodder, double frac) { return interpolate(frac); }
    
    /**
     * Gets an array of {@code Paint} instances interpolated from the
     * {@code FillSpan} instances that make up this {@code BorderFillSpan}
     * instance.
     * <p>
     * The returned array will always have the following layout:<pre>
     * { getTop().interpolate(frac),
     *   getRight().interpolate(frac),
     *   getBottom().interpolate(frac),
     *   getLeft().interpolate(frac) }</pre>
     * meaning the returned array will always be of length 4 and in the order
     * {@code [top, right, bottom, left]}.
     * </p>
     * 
     * @param frac - fraction between {@code 0.0} and {@code 1.0}
     * @return an array of interpolated {@code Paint} instances
     * @see FillSpan#interpolate(double)
     */
    public Paint[] interpolate(double frac) {
        return new Paint[] { getTop().interpolate(frac), //
                             getRight().interpolate(frac), //
                             getBottom().interpolate(frac), //
                             getLeft().interpolate(frac) };
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        // null check not needed, instanceof returns false when obj is null
        if (!(obj instanceof BorderFillSpan)) { return false; }
        
        BorderFillSpan that = (BorderFillSpan) obj;
        if (hash != that.hash || special != that.special || isUniform() != that.isUniform()) { return false; }
        
        return equals(that);
    }
    
    /**
     * Called by {@link #equals(Object)}, implementing classes should use this
     * method to check for equality.
     */
    protected boolean equals(BorderFillSpan that) {
        return FillSpanHelper.fillSpansAreEqual(top, that.top);
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() { return hash; }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("BorderFillSpan [ top: %s, right: %s, bottom: %s, left: %s ]", getTop(), getRight(),
            getBottom(), getLeft());
    }
    
    /**
     * Gets whether or not this {@code BorderFillSpan} has a
     * {@code SpecialFillSpan} instance.
     * 
     * @return {@code true} if this {@code BorderFillSpan} has a
     *         {@code SpecialFillSpan} instance, otherwise {@code false}
     */
    boolean isSpecial() { return special; }
    
    
    /**************************************************************************
     *                                                                        *
     * Public Static API                                                      *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a uniform {@code BorderFillSpan} with top, right, bottom and left
     * associated with a specified {@code FillSpan} instance.
     * 
     * @param t - the {@code FillSpan} to use on the top, bottom, right and
     *        left, if {@code null}, defaults to a {@code FillSpan} of
     *        {@code Color.BLACK}
     * @return a {@code BorderFillSpan} with top, right, bottom and left
     *         associated with the specified fill span
     */
    public static final BorderFillSpan of(FillSpan t) {
        t = (t == null) ? getDefaultTop() : t;
        
        return new BorderFillSpan(t);
    }
    
    /**
     * Gets a {@code BorderFillSpan} containing the specified top and bottom,
     * and right and left {@code FillSpan} instances.
     * 
     * @param tb - the {@code FillSpan} to use on the top and bottom, if
     *        {@code null}, defaults to a {@code FillSpan} of
     *        {@code Color.BLACK}
     * @param rl - the {@code FillSpan} to use on the right and left, if
     *        {@code null}, defaults to the same value as {@code top}
     * @return a {@code BorderFillSpan} with the specified top and bottom, and
     *         right and left {@code FillSpan} instances
     */
    public static final BorderFillSpan of(FillSpan tb, FillSpan rl) {
        tb = (tb == null) ? getDefaultTop() : tb;
        rl = (rl == null) ? tb : rl;
        
        return biAreUni(tb, rl) ? of(tb) : new BiBorderFillSpan(tb, rl);
    }
    
    /** 'of(tb, rl)' equality helper method. */
    private static boolean biAreUni(FillSpan tb, FillSpan rl) {
        return fillSpansAreEqual(tb, rl);
    }
    
    /**
     * Gets a {@code BorderFillSpan} containing the specified top, right,
     * bottom and left {@code FillSpan} instances.
     * 
     * @param t - the {@code FillSpan} to use on the top, if {@code null},
     *        defaults to a {@code FillSpan} of {@code Color.BLACK}
     * @param r - the {@code FillSpan} to use on the right, if {@code null},
     *        defaults to the same value as {@code top}
     * @param b - the {@code FillSpan} to use on the bottom, if {@code null},
     *        defaults to the same value as {@code top}
     * @param l - the {@code FillSpan} to use on the left, if {@code null},
     *        defaults to the same value as {@code top}
     * @return a {@code BorderFillSpan} with the specified top, right, bottom
     *         and left {@code FillSpan} instances
     */
    public static final BorderFillSpan of(FillSpan t, FillSpan r, FillSpan b, FillSpan l) {
        t = (t == null) ? BorderFillSpan.getDefaultTop() : t;
        r = (r == null) ? t : r;
        b = (b == null) ? t : b;
        l = (l == null) ? t : l;
        
        if (quadAreUni(t, r, b, l)) { return of(t); }
        
        if (quadAreBi(t, r, b, l)) { return of(t, r); }
        
        return new QuadBorderFillSpan(t, r, b, l);
    }
    
    /** 'of(t, r, b, l)' equality helper method. */
    private static boolean quadAreBi(FillSpan t, FillSpan r, FillSpan b, FillSpan l) {
        return fillSpansAreEqual(t, b) && fillSpansAreEqual(r, l);
    }
    
    /** 'of(t, r, b, l)' equality helper method. */
    private static boolean quadAreUni(FillSpan t, FillSpan r, FillSpan b, FillSpan l) {
        return fillSpansAreEqual(t, r) && fillSpansAreEqual(t, b) && fillSpansAreEqual(t, l);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Bi Border Fill Span                                                    *
     *                                                                        *
     *************************************************************************/
    
    static class BiBorderFillSpan extends BorderFillSpan {
        private final FillSpan right;
        
        /**
         * Constructs a {@code BorderFillSpan} with the specified top and
         * bottom, and right and left {@code FillSpan} instances.
         * @param tb - the top and bottom {@code FillSpan} instances
         * @param rl - the right and left {@code FillSpan} instances
         */
        private BiBorderFillSpan(FillSpan tb, FillSpan rl) {
            super(tb);
            right = rl;
            
            special |= rl.isSpecial();
        }
        
        @Override
        protected int preComputeHash() {
//            System.out.println("BiBorderFillSpan # preComputeHash()");
            return 31 * super.preComputeHash() + right.hashCode();
        }
        
        @Override
        public boolean isUniform() { return false; }
        
        @Override
        public FillSpan getRight() { return right; }
        
        @Override
        public FillSpan getLeft() { return right; }
        
        @Override
        boolean isSpecial() { return special; }
        
        @Override
        protected boolean equals(BorderFillSpan that) {
            if (!(that instanceof BiBorderFillSpan)) { return false; }
            
            return FillSpanHelper.fillSpansAreEqual(right, ((BiBorderFillSpan) that).right) && super.equals(that);
        }
        
    } // class BiBorderFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Quad Border Fill Span                                                  *
     *                                                                        *
     *************************************************************************/
    
    static class QuadBorderFillSpan extends BiBorderFillSpan {
        private final FillSpan bottom, left;
        
        /**
         * Constructs a {@code BorderFillSpan} with the specified top, right,
         * bottom and left {@code FillSpan} instances.
         * @param t - the top {@code FillSpan} instance
         * @param r - the right {@code FillSpan} instance
         * @param b - the bottom {@code FillSpan} instance
         * @param l - the left {@code FillSpan} instance
         */
        private QuadBorderFillSpan(FillSpan t, FillSpan r, FillSpan b, FillSpan l) {
            super(t, r);
            bottom = b;
            left = l;
            
            special |= bottom.isSpecial();
            special |= left.isSpecial();
        }
        
        @Override
        protected int preComputeHash() {
//            System.out.println("QuadBorderFillSpan # preComputeHash()");
            return 31 * (31 * super.preComputeHash() + bottom.hashCode()) + left.hashCode();
        }
        
        @Override
        public FillSpan getBottom() { return bottom; }
        
        @Override
        public FillSpan getLeft() { return left; }
        
        @Override
        protected boolean equals(BorderFillSpan that) {
            if (!(that instanceof QuadBorderFillSpan)) { return false; }
            
            return FillSpanHelper.fillSpansAreEqual(bottom, ((QuadBorderFillSpan) that).bottom)
                   && FillSpanHelper.fillSpansAreEqual(left, ((QuadBorderFillSpan) that).left) && super.equals(that);
        }
        
    } // class QuadBorderFillSpan
    
} // class BorderFillSpan

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.jhenly.juifx.animation.JuiFillTransition;
import com.jhenly.juifx.control.Fillable;
import com.jhenly.juifx.css.SubCssMetaData;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * A {@code Fill} is an immutable object which encapsulates the
 * {@link FillSpan} instance(s) used to fill a {@link Fillable} instance over
 * the course of a {@link JuiFillTransition}.
 * <p>
 * Because this class is immutable, you can freely reuse the same {@code Fill}
 * in many different {@code Fillable} instances.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public class Fill {
    
    /***************************************************************************
     *                                                                         *
     * Constants                                                               *
     *                                                                         *
     **************************************************************************/
    
    // lazy, thread-safe instantiation
    private static class Holder {
        static final Color NULL_BG_FILL_SPAN = Color.TRANSPARENT;
        static final List<FillSpan> FILL_SPANS_NULL_EMPTY = List.of();
    }
    /**
    * Returns empty list of {@link FillSpan} when constructor's
    * {@code FillSpan} parameter is {@code null} or
    * {@code fillSpans.length == 0}.
    */
    static List<FillSpan> getFillSpansNullEmpty() { return Holder.FILL_SPANS_NULL_EMPTY; }
    
    
    // lazy, thread-safe instantiation
    private static class Default {
        static final Fill DEFAULT_FILL = new Fill(); // fill with no spans
        static final Color[] CSS_INITIAL = new Color[] {};
        static final Color[] CSS_TO_FROM_DEFAULT = new Color[] {};
    }
    
    /** 
     * Gets the default fill which contains no fill spans.
     * @return the default fill
     */
    public static Fill getDefault() { return Default.DEFAULT_FILL; }
    
    /**
     * Gets the CSS default value for {@code -*-fill-from} and
     * {@code -*-fill-to}.
     * <p>
     * This method simply returns the same empty {@code Color[]} instance each
     * time
     * @return the CSS default value for <i>fill-from</i> and <i>fill-to</i>
     */
    public static Color[] getCssFromToDefault() { return Default.CSS_TO_FROM_DEFAULT; }
    
    /**
     * Gets the CSS default value for {@code -*-fill-from} and
     * {@code -*-fill-to}.
     * <p>
     * This method simply returns the same empty {@code Color[]} instance each
     * time.
     * @return the CSS default value for <i>fill-from</i> and <i>fill-to</i>
     */
    static Color[] getCssInitial() { return Default.CSS_INITIAL; }
    
    
    /***************************************************************************
     *                                                                         *
     * Fill Type                                                               *
     *                                                                         *
     **************************************************************************/
    
//    /**
//     * {@code FillType} indicates what part(s) of a {@link Fillable} should be
//     * filled.
//     * 
//     * @since JuiFX 1.0
//     * @see #NONE
//     * @see #ALL
//     * @see #BG
//     * @see #TEXT
//     * @see #SHAPE
//     * @see #BG_TEXT
//     * @see #BG_SHAPE
//     * @see #TEXT_SHAPE
//     */
//    public static enum FillType {
//        /**
//         * Indicates the {@link Fillable} instance has nothing to be filled.
//         */
//        NONE,
//        /** 
//         * Indicates all parts of the {@link Fillable} instance should be
//         * filled.
//         */
//        ALL,
//        /** 
//         * Indicates the {@link Fillable} instance's background should be
//         * filled.
//         */
//        BG,
//        /**
//         * Indicates the {@link Fillable} instance's background and text
//         * should be filled.
//         */
//        BG_TEXT,
//        /**
//         * Indicates the {@link Fillable} instance's background and shape
//         * should be filled.
//         */
//        BG_SHAPE,
//        /**
//         * Indicates the {@link Fillable} instance's text should be filled.
//         */
//        TEXT,
//        /** 
//         * Indicates the {@link Fillable} instance's text and shape should
//         * be filled.
//         */
//        TEXT_SHAPE,
//        /**
//         * Indicates the {@link Fillable} instance's shape should be filled.
//         */
//        SHAPE;
//    }
    
    
    /***************************************************************************
     *                                                                         *
     * Private Members                                                         *
     *                                                                         *
     **************************************************************************/
    
//    private final FillType type;
    
    /** List of this fill's fill spans. */
    private final List<FillSpan> bgSpans;
    private final FillSpan textSpan;
    private final FillSpan shapeSpan;
    private final FillSpan strokeSpan;
    
    /**
     * Cached hash for improved performance on subsequent hash or equality
     * look ups.
     */
    private final int hash;
    
    
    /***************************************************************************
     *                                                                         *
     * Constructor(s)                                                          *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Creates a new {@code Fill} with the specified text, shape, stroke and
     * background {@link FillSpan} instances.
     * <p>
     * {@code null} parameters are allowed. If all parameters are {@code null}
     * then the constructed {@code Fill} will be empty.
     * <p>
     * If the specified array of background {@code FillSpan} instances is
     * {@code null} or empty, or if the specified array only contains
     * {@code null} instances, then this {@code Fill} instance's background
     * fill spans will be {@code null}.
     * Any {@code null} background {@code FillSpan} instances will be replaced
     * by a {@code FillSpan} instance with a <i>fill-from</i> and
     * <i>fill-to</i> of {@link Color#TRANSPARENT}.
     *
     * @param textFillSpan - the text fill span
     * @param shapeFillSpan - the shape fill span
     * @param strokeFillSpan - the stroke fill span
     * @param bgFillSpans - array of {@code FillSpan} instances, if {@code null}
     *        or empty, or contains only {@code null} instances, then
     *        background fill spans will be {@code null}
     */
    public Fill(FillSpan textFillSpan, FillSpan shapeFillSpan, FillSpan strokeFillSpan, FillSpan... bgFillSpans) {
        // used to precompute the hash code
        int preHash = 23;
        
        // assign text fill span and add to preHash
        textSpan = textFillSpan;
        preHash += (textSpan == null) ? 0 : 11 * preHash + textSpan.hashCode();
        // assign shape fill span and add to preHash
        shapeSpan = shapeFillSpan;
        preHash += (shapeSpan == null) ? 0 : 13 * preHash + shapeSpan.hashCode();
        // assign stroke fill span and add to preHash
        strokeSpan = strokeFillSpan;
        preHash += (strokeSpan == null) ? 0 : 17 * preHash + strokeSpan.hashCode();
        
        if (bgFillSpans == null || bgFillSpans.length == 0) {
            bgSpans = null;
        } else {
            // keep separate hash for bgSpans in case array is filled with null
            int bgSpansHash = 19;
            
            // used to replace nulls and create unmodifiable list
            List<FillSpan> tmpSpans = new ArrayList<>(bgFillSpans.length);
            
            // check for any null fill spans or for all null fill spans
            int nullCount = 0;
            for (int i = 0; i < bgFillSpans.length; i++) {
                FillSpan span = bgFillSpans[i];
                
                if (span == null) {
                    nullCount++;
                    // replace null fill span with Color.BLACK fill span
                    span = FillSpan.getNullArgsInstance();
                }
                
                bgSpansHash = 31 * bgSpansHash + span.hashCode();
                tmpSpans.add(span);
            }
            
            // if not all null, set bgSpans to unmodifiable list and add hash
            if (nullCount < (bgFillSpans.length - 1)) {
                bgSpans = Collections.unmodifiableList(tmpSpans);
                preHash += bgSpansHash;
            } else {
                // if given all null fill spans then set bgSpans to null
                bgSpans = null;
            }
            
        }
        
        // set hash to precomputed hash
        hash = preHash;
    }
    
    /**
     * Convenience constructor that only sets the background {@link FillSpan}
     * instances.
     * <p>
     * This constructor simply does the following:<pre>
     * this(null, null, null, bgFillSpans)</pre>
     * 
     * @param bgFillSpans - array of background {@code FillSpan} instances, if
     *        {@code null} or empty or only containing {@code null} instances,
     *        then background fill spans will be {@code null}, any {@code null}
     *        {@code FillSpan} instances will be replaced by a {@code FillSpan}
     *         with a fill-from and fill-to of {@link Color#TRANSPARENT}
     */
    public Fill(FillSpan... bgFillSpans) {
        this(null, null, null, bgFillSpans);
    }
    
    /**
     * Convenience constructor that only sets the background {@link FillSpan}
     * instances.
     * <p>
     * This constructor simply does the following:<pre>
     * this(bgFillSpans == null ? null : bgFillSpans.toArray(new FillSpan[bgFillSpans.size()]))</pre>
     * 
     * @param bgFillSpans - list of background {@code FillSpan} instances, if
     *        {@code null} or empty or only containing {@code null} instances,
     *        then background fill spans will be {@code null}, any {@code null}
     *        {@code FillSpan} instances will be replaced by a {@code FillSpan}
     *         with a fill-from and fill-to of {@link Color#TRANSPARENT}
     */
    public Fill(List<FillSpan> bgFillSpans) {
        this(bgFillSpans == null ? null : bgFillSpans.toArray(new FillSpan[bgFillSpans.size()]));
    }
    
    /**
     * Convenience constructor that sets the text and background
     * {@link FillSpan} instances.
     * <p>
     * This constructor simply does the following:<pre>
     * this(textFillSpan, null, null, bgFillSpans)</pre>
     * 
     * @param textFillSpan - the text fill span
     * @param bgFillSpans - array of background {@code FillSpan} instances, if
     *        {@code null} or empty or only containing {@code null} instances,
     *        then background fill spans will be {@code null}, any {@code null}
     *        {@code FillSpan} instances will be replaced by a {@code FillSpan}
     *         with a fill-from and fill-to of {@link Color#TRANSPARENT}
     */
    public Fill(FillSpan textFillSpan, FillSpan... bgFillSpans) {
        this(null, null, null, bgFillSpans);
    }
    
    /**
     * Convenience constructor that sets the text, shape and stroke
     * {@link FillSpan} instances, and sets the background fill spans to
     * {@code null}.
     * <p>
     * This constructor simply does the following:<pre>
     * this(textFillSpan, shapeFillSpan, strokeFillSpan, (FillSpan[]) null)</pre>
     * 
     * @param textFillSpan - the text fill span
     * @param shapeFillSpan - the shape fill span
     * @param strokeFillSpan - the stroke fill span
     */
    public Fill(FillSpan textFillSpan, FillSpan shapeFillSpan, FillSpan strokeFillSpan) {
        this(textFillSpan, shapeFillSpan, strokeFillSpan, (FillSpan[]) null);
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Gets this {@code Fill} instance's {@code FillType}.
     * @return the {@code FillType} of this {@code Fill} instance
     */
//    public final FillType getType() { return type; }
    
    /**
     * Gets whether or not this {@code Fill} instance is made up of any
     * {@link FillSpan} instances.
     * @return {@code true} if this {@code Fill} instance has fill spans,
     *         otherwise {@code false}
     */
    public final boolean hasFillSpans() { return !bgSpans.isEmpty(); }
    
    /**
     * Gets whether or not this {@code Fill} instance contains any background
     * {@link FillSpan} instances.
     * @return {@code true} if this {@code Fill} instance has background fill
     *         span(s), otherwise {@code false}
     */
    public final boolean hasBgFillSpans() { return !bgSpans.isEmpty(); }
    
    /**
     * Gets whether or not this {@code Fill} instance contains a text
     * {@link FillSpan} instance.
     * @return {@code true} if this {@code Fill} instance has a text fill span,
     *         otherwise {@code false}
     */
    public final boolean hasTextFillSpan() { return textSpan != null; }
    
    /**
     * Gets whether or not this {@code Fill} instance contains a shape
     * {@link FillSpan} instance.
     * @return {@code true} if this {@code Fill} instance has a shape fill
     *         span, otherwise {@code false}
     */
    public final boolean hasShapeFillSpan() { return shapeSpan != null; }
    
    /**
     * Gets whether or not this {@code Fill} instance contains a stroke
     * {@link FillSpan} instance.
     * @return {@code true} if this {@code Fill} instance has a stroke fill
     *         span, otherwise {@code false}
     */
    public final boolean hasStrokeFillSpan() { return shapeSpan != null; }
    
    /**
     * Gets the list of {@link FillSpan} instances making up this {@code Fill}.
     * <p>
     * <b>Note:</b> This List is unmodifiable and immutable. It will never be
     * {@code null}. The elements of this list will also never be {@code null}.
     * 
     * @return the list of {@code FillSpan} instances making up this
     *         {@code Fill}
     * @see Collections#unmodifiableList(List)
     */
    public final List<FillSpan> getFillSpans() { return bgSpans; }
    
    /**
     * Gets the list of background {@link FillSpan} instances making up this
     * {@code Fill}.
     * <p>
     * <b>Note:</b> This List is unmodifiable and immutable. It will never be
     * {@code null}. The elements of this list will also never be {@code null}.
     * 
     * @return the list of {@code FillSpan} instances making up this
     *         {@code Fill}
     * @see Collections#unmodifiableList(List)
     */
    public final List<FillSpan> getBgFillSpans() { return bgSpans; }
    
    /**
     * Gets the text {@link FillSpan} instance in this {@code Fill}, if it has
     * one.
     * 
     * @return the text {@code FillSpan} instance or {@code null}
     */
    public final FillSpan getTextFillSpan() { return textSpan; }
    
    /**
     * Gets the shape {@link FillSpan} instance in this {@code Fill}, if it has
     * one.
     * 
     * @return the shape {@code FillSpan} instance or {@code null}
     * @see Collections#unmodifiableList(List)
     */
    final FillSpan getShapeFillSpan() { return shapeSpan; }
    
    /**
     * Gets the stroke {@link FillSpan} instance in this {@code Fill}, if it
     * has one.
     * 
     * @return the stroke {@code FillSpan} instance or {@code null}
     */
    public final FillSpan getStrokeFillSpan() { return strokeSpan; }
    
    /**
     * Gets a {@link FillSpan} from the specified index.
     * 
     * @param index - position of the fill span to get
     * @return the fill span at the specified index
     * @throws IllegalArgumentException if the specified index is less than
     *         zero, or is greater than the number of fill spans in this fill
     */
    public final FillSpan getFillSpan(final int index) {
        Objects.checkIndex(index, bgSpans.size());
        return bgSpans.get(index);
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() { return hash; }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || this.getClass() != obj.getClass()) { return false; }
        
        final Fill that = (Fill) obj;
        
        // because the hash is cached, this can be a very fast check
        if (this.hash != that.hash) { return false; }
        
        // same hash, so now we check member equality
        if (this.textSpan != that.textSpan) { return false; }
        if (this.shapeSpan != that.shapeSpan) { return false; }
        if (this.strokeSpan != that.strokeSpan) { return false; }
        if (!this.bgSpans.equals(that.bgSpans)) { return false; }
        
        return true;
    }
    
    /**
     * 
     * @param fill - the fill to replace
     * @param fillable
     * @return
     */
    static final Fill getFillFromReplacingSpecialFills(Fill fill, Fillable fillable) {
        return null;
    }
    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/
    
//    /** The fill-enabled CSS style. */
//    static final CssMetaData<Node, Boolean> FILL_ENABLED
//        = new SubCssMetaData<>("-fill-enabled", BooleanConverter.getInstance(), true);
//    /** The fill-duration CSS style. */
//    private static final Duration DEFAULT_DURATION = Duration.millis(100.0);
//    static final CssMetaData<Node, Duration> FILL_DURATION
//        = new SubCssMetaData<>("-fill-duration", DurationConverter.getInstance(), DEFAULT_DURATION);
    
    /** The fill-from CSS style. */
    static final CssMetaData<Node, Color[]> FILL_FROM
        = new SubCssMetaData<>("-jui-fill-from", FillConverter.StringSequenceConverter.getInstance(), new Color[] {});
//        new Color[] { Color.TRANSPARENT });
    /** The fill-to CSS style. */
    static final CssMetaData<Node, Color[]> FILL_TO
        = new SubCssMetaData<>("-jui-fill-to", FillConverter.StringSequenceConverter.getInstance(), new Color[] {});
//        new Color[] { Color.TRANSPARENT });
    
    /** List of CSS styleables. */
    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES = List.of(FILL_FROM, FILL_TO);
    
    /**
     * Gets the {@code CssMetaData} associated with this class, which may
     * include the {@code CssMetaData} of its superclasses.
     * 
     * @return the CssMetaData associated with this class, which may include the
     *         CssMetaData of its superclasses
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return STYLEABLES; }
    
    /***************************************************************************
     *                                                                         *
     * Fill Helper / Accessor                                                  *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Store the singleton instance of the FillHelper subclass corresponding
     * to the subclass of this instance of Fill
     */
    private FillHelper fillHelper = null;
    
    static {
        // This is used by classes in different packages to get access to
        // private and package private methods.
        FillHelper.setFillAccessor(new FillHelper.FillAccessor()
        {
            @Override
            public FillHelper getHelper(Fill fill) {
                return fill.fillHelper;
            }
            
            @Override
            public void setHelper(Fill fill, FillHelper fillHelper) {
                fill.fillHelper = fillHelper;
            }
            
            @Override
            public Color[] getCssInitial() { return Fill.getCssInitial(); }
            
            @Override
            public boolean bgFillSpansContainSpecial(Fill fill) {
                return FillSpanHelper.fillSpansContainSpecial(fill.getBgFillSpans());
            }
            
            @Override
            public boolean textFillSpanIsSpecial(Fill fill) {
                return FillSpanHelper.fillSpanIsSpecial(fill.getTextFillSpan());
            }
            
            @Override
            public boolean shapeFillSpanIsSpecial(Fill fill) {
                return FillSpanHelper.fillSpanIsSpecial(fill.getShapeFillSpan());
            }
            
            @Override
            public boolean strokeFillSpanIsSpecial(Fill fill) {
                return FillSpanHelper.fillSpanIsSpecial(fill.getStrokeFillSpan());
            }
            
            @Override
            public FillSpan getTextFillSpanFromSpecial(Fill fill, Fillable fillable) {
                return FillSpanHelper.getFillSpanFromSpecial(fill, fillable, FillSpanHelper.REP_TEXT);
            }
            
            @Override
            public FillSpan getShapeFillSpanFromSpecial(Fill fill, Fillable fillable) {
                return FillSpanHelper.getFillSpanFromSpecial(fill, fillable, FillSpanHelper.REP_SHAPE);
            }
            
            @Override
            public FillSpan getStrokeFillSpanFromSpecial(Fill fill, Fillable fillable) {
                return FillSpanHelper.getFillSpanFromSpecial(fill, fillable, FillSpanHelper.REP_STROKE);
            }
            
            @Override
            public List<FillSpan> getBgFillSpansFromSpecial(Fill fill, Fillable fillable) {
                return FillSpanHelper.getBgFillSpansFromSpecial(fill, fillable);
            }
            
            
        });
        
    }
    
    
}
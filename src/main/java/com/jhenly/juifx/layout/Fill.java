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
        static final Fill DEFAULT_FILL = new Fill(); // fill with no spans
        static final List<FillSpan> FILL_SPANS_NULL_EMPTY = List.of();
        static final Color[] CSS_INITIAL = new Color[] {};
        static final Color[] CSS_TO_FROM_DEFAULT = new Color[] {};
    }
    /** 
     * Gets the default fill which contains no fill spans.
     * @return the default fill
     */
    public static Fill getDefault() { return Holder.DEFAULT_FILL; }
    
    /**
     * Gets the CSS default value for {@code -*-fill-from} and
     * {@code -*-fill-to}.
     * <p>
     * This method simply returns an empty {@code Color[]}.
     * @return the CSS default value for <i>fill-from</i> and <i>fill-to</i>
     */
    public static Color[] getCssFromToDefault() { return Holder.CSS_TO_FROM_DEFAULT; }
    
    /** 
     * Returns empty list of {@link FillSpan} when constructor's
     * {@code FillSpan} parameter is {@code null} or
     * {@code fillSpans.length == 0}.
     */
    static List<FillSpan> getFillSpansNullEmpty() { return Holder.FILL_SPANS_NULL_EMPTY; }
    
    static Color[] getCssInitial() { return Holder.CSS_INITIAL; }
    
    
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
    private final List<FillSpan> spans;
//    private final FillSpan textSpan;
//    private final FillSpan shapeSpan;
    
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
     * Creates a new {@code Fill} with the specified array of {@link FillSpan}
     * instances.
     * <p>
     * Any {@code null} {@code FillSpan} instances will be replaced by a
     * {@code FillSpan} instance with a <i>fill-from</i> and <i>fill-to</i> of
     * {@link Color#BLACK}.
     * <p>
     * If the specified array of {@code FillSpan} instances is {@code null} or
     * empty, or if the specified array only contains {@code null} instances,
     * then this {@code Fill} instance will have an empty list of
     * {@code FillSpan}.
     *
     * @param fillSpans - array of {@code FillSpan} instances, if {@code null}
     *        or empty, or contains only {@code null} instances, then
     *        constructed {@code Fill} instance will have an empty list of
     *        {@code FillSpan}
     */
    public Fill(FillSpan... fillSpans) {
        // used to precompute the hash code
        int preHash = 19;
        
        if (fillSpans == null || fillSpans.length == 0) {
            // if fill spans is null || empty then use empty list of FillSpan
            spans = getFillSpansNullEmpty();
            preHash = 31 * preHash + spans.hashCode(); // 31 * 19 + 1
        } else {
            // used to replace nulls and create unmodifiable list
            List<FillSpan> tmpSpans = new ArrayList<>(fillSpans.length);
            
            // check for any null fill spans or for all null fill spans
            int nullCount = 0;
            for (int i = 0; i < fillSpans.length; i++) {
                FillSpan span = fillSpans[i];
                
                if (span == null) {
                    nullCount++;
                    // replace null fill span with Color.BLACK fill span
                    span = FillSpan.getNullArgsInstance();
                }
                
                preHash = 31 * preHash + span.hashCode();
                tmpSpans.add(span);
            }
            
            
            if (nullCount < (fillSpans.length - 1)) {
                spans = Collections.unmodifiableList(tmpSpans);
            } else {
                // if all null spans then use empty list of fill spans
                spans = getFillSpansNullEmpty();
                preHash = 31 * preHash + spans.hashCode(); // 31 * 19 + 1
            }
            
        }
        
        // set hash to precomputed hash
        hash = preHash;
    }
    
    /**
     * Convenience constructor that simply calls {@link #Fill(FillSpan[])} with
     * an array converted from the passed in list.
     * <p>
     * This constructor simply does the following:<pre>
     * this(fillSpans == null ? null : fillSpans.toArray(new FillSpan[fillSpans.size()]))</pre>
     * 
     * @param fillSpans - list of {@code FillSpan} instances, if {@code null}
     *        or empty then a list of one {@code FillSpan} with a fill-from
     *        and fill-to of {@link Color#TRANSPARENT} is used
     */
    public Fill(List<FillSpan> fillSpans) {
        this(fillSpans == null ? null : fillSpans.toArray(new FillSpan[fillSpans.size()]));
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
    public final boolean hasFillSpans() { return !spans.isEmpty(); }
    
    /**
     * Gets whether or not this {@code Fill} instance contains any background
     * {@link FillSpan} instances.
     * @return {@code true} if this {@code Fill} instance has background fill
     *         span(s), otherwise {@code false}
     */
//    public final boolean hasBgFillSpans() {
//        switch (type) {
//            case ALL:
//            case BG:
//            case BG_TEXT:
//            case BG_SHAPE:
//                return true;
//            default:
//                return false;
//        }
//    }
    
    /**
     * Gets whether or not this {@code Fill} instance contains a text
     * {@link FillSpan} instance.
     * @return {@code true} if this {@code Fill} instance contains a text fill
     *         spans, otherwise {@code false}
     */
//    public final boolean hasTextFillSpan() {
//        switch (type) {
//            case ALL:
//            case BG_TEXT:
//            case TEXT:
//            case TEXT_SHAPE:
//                return true;
//            default:
//                return false;
//        }
//    }
    
    /**
     * Gets whether or not this {@code Fill} instance contains a shape
     * {@link FillSpan} instance.
     * @return {@code true} if this {@code Fill} instance contains a shape fill
     *         spans, otherwise {@code false}
     */
//    public final boolean hasShapeFillSpan() {
//        switch (type) {
//            case ALL:
//            case BG_SHAPE:
//            case TEXT_SHAPE:
//            case SHAPE:
//                return true;
//            default:
//                return false;
//        }
//    }
    
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
    public final List<FillSpan> getFillSpans() { return spans; }
    
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
//    public final List<FillSpan> getBgFillSpans() { return spans; }
    
    /**
     * Gets the text {@link FillSpan} instance in this {@code Fill}, if it has
     * one.
     * 
     * @return the text {@code FillSpan} instance or {@code null}
     * @see Collections#unmodifiableList(List)
     */
//    public final FillSpan getTextFillSpan() { return textSpan; }
    
    /**
     * Gets the shape {@link FillSpan} instance in this {@code Fill}, if it has
     * one.
     * 
     * @return the shape {@code FillSpan} instance or {@code null}
     * @see Collections#unmodifiableList(List)
     */
//    public final FillSpan getShapeFillSpan() { return shapeSpan; }
    
    /**
     * Gets a {@link FillSpan} from the specified index.
     * 
     * @param index - position of the fill span to get
     * @return the fill span at the specified index
     * @throws IllegalArgumentException if the specified index is less than
     *         zero, or is greater than the number of fill spans in this fill
     */
    public final FillSpan getFillSpan(final int index) {
        Objects.checkIndex(index, spans.size());
        return spans.get(index);
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
        if (!this.spans.equals(that.spans)) { return false; }
        
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
            public Color[] getCssInitial(Fill fill) { return fill.getCssInitial(); }
        });
        
    }
    
    
}

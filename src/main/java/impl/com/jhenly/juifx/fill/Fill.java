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
import java.util.Collections;
import java.util.List;

import com.jhenly.juifx.animation.JuiFillTransition;
import com.jhenly.juifx.control.Fillable;

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
            public boolean fillHasSpecial(Fill fill) { return fill.hasSpecial(); }
            
            @Override
            public Fill replaceSpecialsInFill(Fill fill, Fillable fable) {
                return Fill.replaceSpecialsInFill(fill, fable);
            }
            
        });
        
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Constants                                                               *
     *                                                                         *
     **************************************************************************/
    
    // lazy, thread-safe instantiation
    private static class Holder {
        static final List<FillSpan> FILL_SPANS_NULL_EMPTY = List.of();
        
        private Holder() { throw new IllegalAccessError("a Holder class should not be instantiated"); }
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
    }
    
    /** 
     * Gets the default fill which contains no fill spans.
     * @return the default fill
     */
    public static Fill getDefault() { return Default.DEFAULT_FILL; }
    
    
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
    
    {
        // initialize the class helper at the beginning of each constructor
        FillHelper.initHelper(this);
    }
    
    /***************************************************************************
     *                                                                         *
     * Private Members                                                         *
     *                                                                         *
     **************************************************************************/
    
//    private final FillType type;
    
    /** List of this fill's fill spans. */
    private final FillSpan textSpan;
    private final FillSpan shapeSpan;
    private final FillSpan strokeSpan;
    private final List<FillSpan> bgSpans;
    private final List<BorderFillSpan> bdSpans; // border spans
    
    private final boolean hasSpans;
    private final boolean hasSpecial; // signals that the fill has special spans
    
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
    
    /** Empty constructor only used by {@link Default#DEFAULT_FILL}. */
    private Fill() {
        textSpan = null;
        shapeSpan = null;
        strokeSpan = null;
        bgSpans = null;
        bdSpans = null;
        hasSpans = false;
        hasSpecial = false;
        hash = 23;
    }
    
    /**
     * Creates a new {@code Fill} with the specified text, shape, stroke,
     * background and border {@link FillSpan} instances.
     * <p>
     * {@code null} parameters are allowed. If all parameters are {@code null}
     * then the constructed {@code Fill} will be empty.
     * <p>
     * If a specified list of {@code FillSpan} instances is {@code null} or
     * empty, or if the list only contains {@code null} instances, then this
     * {@code Fill} instance's respective fill spans will be {@code null}. Any
     * {@code null} {@code FillSpan} instances in the list will be replaced by
     * a {@code FillSpan} instance with a <i>fill-from</i> and <i>fill-to</i>
     * of {@link Color#TRANSPARENT}.
     *
     * @param textFillSpan - the text fill span
     * @param shapeFillSpan - the shape fill span
     * @param strokeFillSpan - the stroke fill span
     * @param bgFillSpans - array of {@code FillSpan} instances, if
     *        {@code null} or empty, or contains only {@code null} instances,
     *        then background fill spans will be {@code null}, any {@code null}
     *        elements will be replaced by a {@code FillSpan} with a fill-from
     *        and fill-to of {@code Color.TRANSPARENT}
     * @param borderFillSpans - array of {@code BorderFillSpan} instances, if
     *        {@code null} or empty, or contains only {@code null} instances,
     *        then border fill spans will be {@code null}, any {@code null}
     *        elements will be replaced by a uniform {@code BorderFillSpan}
     *        with a fill-from and fill-to of {@code Color.TRANSPARENT}
     */
    public Fill(FillSpan textFillSpan, FillSpan shapeFillSpan, FillSpan strokeFillSpan, FillSpan[] bgFillSpans,
                BorderFillSpan[] borderFillSpans)
    {
        this(textFillSpan, shapeFillSpan, strokeFillSpan,
            ((bgFillSpans == null || bgFillSpans.length == 0) ? (List<FillSpan>) null : List.of(bgFillSpans)),
            ((borderFillSpans == null || borderFillSpans.length == 0) ? (List<BorderFillSpan>) null
                : List.of(borderFillSpans)));
    }
    
    /**
     * Creates a new {@code Fill} with the specified text, shape, stroke,
     * background and border {@link FillSpan} instances.
     * <p>
     * {@code null} parameters are allowed. If all parameters are {@code null}
     * then the constructed {@code Fill} will be empty.
     * <p>
     * If a specified list of {@code FillSpan} instances is {@code null} or
     * empty, or if the list only contains {@code null} instances, then this
     * {@code Fill} instance's respective fill spans will be {@code null}. Any
     * {@code null} {@code FillSpan} instances in the list will be replaced by
     * a {@code FillSpan} instance with a <i>fill-from</i> and <i>fill-to</i>
     * of {@link Color#TRANSPARENT}.
     *
     * @param textFillSpan - the text fill span
     * @param shapeFillSpan - the shape fill span
     * @param strokeFillSpan - the stroke fill span
     * @param bgFillSpans - list of {@code FillSpan} instances, if {@code null}
     *        or empty, or contains only {@code null} instances, then
     *        background fill spans will be {@code null}, any {@code null}
     *        elements will be replaced by a {@code FillSpan} with a fill-from
     *        and fill-to of {@code Color.TRANSPARENT}
     * @param borderFillSpans - list of {@code BorderFillSpan} instances, if
     *        {@code null} or empty, or contains only {@code null} instances,
     *        then border fill spans will be {@code null}, any {@code null}
     *        elements will be replaced by a uniform {@code BorderFillSpan}
     *        with a fill-from and fill-to of {@code Color.TRANSPARENT}
     */
    public Fill(FillSpan textFillSpan, FillSpan shapeFillSpan, FillSpan strokeFillSpan, List<FillSpan> bgFillSpans,
                List<BorderFillSpan> borderFillSpans)
    {
        // used to precompute the hash code
        int preHash = 23;
        // used to signal that the fill has specials
        boolean preHasSpecial = false;
        
        // assign text fill span, add to preHash and check for special
        textSpan = textFillSpan;
        preHash += (textSpan == null) ? 0 : 11 * preHash + textSpan.hashCode();
        preHasSpecial |= (textSpan == null) ? false : textSpan.isSpecial();
        
        // assign shape fill span, add to preHash and check for special
        shapeSpan = shapeFillSpan;
        preHash += (shapeSpan == null) ? 0 : 13 * preHash + shapeSpan.hashCode();
        preHasSpecial |= (shapeSpan == null) ? false : shapeSpan.isSpecial();
        
        // assign stroke fill span, add to preHash and check for special
        strokeSpan = strokeFillSpan;
        preHash += (strokeSpan == null) ? 0 : 17 * preHash + strokeSpan.hashCode();
        preHasSpecial |= (strokeSpan == null) ? false : strokeSpan.isSpecial();
        
        // check and possibly create list of background fill spans
        if (bgFillSpans == null || bgFillSpans.isEmpty()) {
            bgSpans = null;
        } else {
            // keep separate hash for bgSpans in case array is filled with null
            int bgSpansHash = 19;
            
            // used to replace nulls and create unmodifiable list
            List<FillSpan> tmpSpans = new ArrayList<>(bgFillSpans.size());
            
            // replace any null fill spans and check for all null fill spans
            int nullCount = 0;
            for (int i = 0; i < bgFillSpans.size(); i++) {
                FillSpan bgSpan = bgFillSpans.get(i);
                
                if (bgSpan == null) {
                    nullCount++;
                    // replace null fill span with transparent fill span
                    bgSpan = FillSpan.getNullArgsInstance();
                }
                
                // check if span is special
                preHasSpecial |= bgSpan.isSpecial();
                
                bgSpansHash = 31 * bgSpansHash + bgSpan.hashCode();
                tmpSpans.add(bgSpan);
            }
            
            // if not all null, set bgSpans to unmodifiable list and add hash
            if (nullCount < bgFillSpans.size()) {
                bgSpans = Collections.unmodifiableList(tmpSpans);
                preHash += bgSpansHash;
            } else {
                // if given all null fill spans then set bgSpans to null
                bgSpans = null;
            }
            
        }
        
        // check and possibly create list of border fill spans
        if (borderFillSpans == null || borderFillSpans.isEmpty()) {
            bdSpans = null;
        } else {
            // keep separate hash for bdSpans in case array is filled with null
            int borderSpansHash = 27;
            
            // used to replace nulls and create unmodifiable list
            List<BorderFillSpan> tmpSpans = new ArrayList<>(borderFillSpans.size());
            
            // replace any null fill spans and check for all null fill spans
            int nullCount = 0;
            for (int i = 0, n = borderFillSpans.size(); i < n; i++) {
                BorderFillSpan bdSpan = borderFillSpans.get(i);
                
                if (bdSpan == null) {
                    nullCount++;
                    // replace null fill span with transparent border fill span
                    bdSpan = BorderFillSpan.getNullFillSpan();
                }
                
                // check if span is special before null check
                preHasSpecial |= bdSpan.isSpecial();
                
                borderSpansHash = 31 * borderSpansHash + bdSpan.hashCode();
                tmpSpans.add(bdSpan);
            }
            
            // if not all null, set bdSpans to unmodifiable list and add hash
            if (nullCount < borderFillSpans.size()) {
                bdSpans = Collections.unmodifiableList(tmpSpans);
                preHash += borderSpansHash;
            } else {
                // if given all null fill spans then set bdSpans to null
                bdSpans = null;
            }
            
        }
        
        hasSpans = textSpan != null || shapeSpan != null || strokeSpan != null || bgSpans != null || bdSpans != null;
        
        // set if any fill span instances were special
        hasSpecial = preHasSpecial;
        
        // set hash to precomputed hash
        hash = preHash + (hasSpecial ? 1 : 0);
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/
//
    /**
     * Gets this {@code Fill} instance's {@code FillType}.
     * @return the {@code FillType} of this {@code Fill} instance
     */
//    public final FillType getType() { return type; }
    
    /**
     * Gets whether or not this {@code Fill} instance contains any
     * {@link FillSpan} instances.
     * @return {@code true} if this {@code Fill} instance contains any
     *         {@code FillSpan} instances, otherwise {@code false}
     */
    public final boolean hasFillSpans() {
        return hasSpans;
    }
    
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
     * Gets whether or not this {@code Fill} instance contains any background
     * {@link FillSpan} instances.
     * @return {@code true} if this {@code Fill} instance has background fill
     *         span(s), otherwise {@code false}
     */
    public final boolean hasBgFillSpans() { return bgSpans != null && !bgSpans.isEmpty(); }
    
    /**
     * Gets whether or not this {@code Fill} instance contains any background
     * {@link FillSpan} instances.
     * @return {@code true} if this {@code Fill} instance has background fill
     *         span(s), otherwise {@code false}
     */
    public final boolean hasBorderFillSpans() { return bdSpans != null && !bdSpans.isEmpty(); }
    
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
    public final FillSpan getShapeFillSpan() { return shapeSpan; }
    
    /**
     * Gets the stroke {@link FillSpan} instance in this {@code Fill}, if it
     * has one.
     * 
     * @return the stroke {@code FillSpan} instance or {@code null}
     */
    public final FillSpan getStrokeFillSpan() { return strokeSpan; }
    
    /**
     * Gets the list of background {@link FillSpan} instances making up this
     * {@code Fill}.
     * <p>
     * <b>Note:</b> This List is unmodifiable and immutable. It will never be
     * {@code null}. The elements of this list will also never be {@code null}.
     * 
     * @return the list of background{@code FillSpan} instances making up this
     *         {@code Fill}
     * @see Collections#unmodifiableList(List)
     */
    public final List<FillSpan> getBgFillSpans() { return bgSpans; }
    
    /**
     * Gets the list of border {@link FillSpan} instances making up this
     * {@code Fill}.
     * <p>
     * <b>Note:</b> This List is unmodifiable and immutable. It will never be
     * {@code null}. The elements of this list will also never be {@code null}.
     * 
     * @return the list of border {@code FillSpan} instances making up this
     *         {@code Fill}
     * @see Collections#unmodifiableList(List)
     */
    public final List<BorderFillSpan> getBorderFillSpans() { return bdSpans; }
    
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
        if (hash != that.hash) { return false; }
        if (hasSpecial != that.hasSpecial) { return false; }
        
        // if cache of fill spans is enabled then reference equality can be used
        return FillSpanHelper.fillSpansAreEqual(textSpan, that.textSpan)
               && FillSpanHelper.fillSpansAreEqual(shapeSpan, that.shapeSpan)
               && FillSpanHelper.fillSpanListsAreEqual(bgSpans, that.bgSpans)
               && FillSpanHelper.fillSpanListsAreEqual(bdSpans, that.bdSpans)
               && FillSpanHelper.fillSpansAreEqual(strokeSpan, that.strokeSpan);
    }
    
    /**
     * Gets whether or not this {@code Fill} instance has any fill spans with
     * special identifiers.
     * @return {@code true} if this {@code Fill} instance has any fill spans
     *         with special identifiers
     */
    final boolean hasSpecial() { return hasSpecial; }
    
    /**
     * 
     * @param fill - the {@code Fill} to replace the special identifiers in
     * @param fable - the {@code Fillable} to use to replace the special
     *        identifiers
     * @return a {@code Fill} instance resembling the replacements
     */
    static final Fill replaceSpecialsInFill(Fill fill, Fillable fable) {
        if (fill == null || fable == null || !fill.hasSpecial) { return fill; }
        
        FillSpan repText = FillSpanHelper.getFillSpanFromSpecial(fill.textSpan, fable);
        FillSpan repShape = FillSpanHelper.getFillSpanFromSpecial(fill.shapeSpan, fable);
        FillSpan repStroke = FillSpanHelper.getFillSpanFromSpecial(fill.strokeSpan, fable);
        List<FillSpan> repBg = FillSpanHelper.getFillSpanListFromSpecial(fill.bgSpans, fable);
        List<BorderFillSpan> repBd = FillSpanHelper.getBorderFillSpanListFromSpecial(fill.bdSpans, fable);
        
        return new Fill(repText, repShape, repStroke, repBg, repBd);
    }
    
}

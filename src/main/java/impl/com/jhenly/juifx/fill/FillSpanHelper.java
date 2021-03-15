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

import static impl.com.jhenly.juifx.fill.FillSpan.USE_BG;
import static impl.com.jhenly.juifx.fill.FillSpan.USE_BORDER;
import static impl.com.jhenly.juifx.fill.FillSpan.USE_SHAPE;
import static impl.com.jhenly.juifx.fill.FillSpan.USE_STROKE;
import static impl.com.jhenly.juifx.fill.FillSpan.USE_TEXT;

import java.util.ArrayList;
import java.util.List;

import com.jhenly.juifx.control.Fillable;

import impl.com.jhenly.juifx.fill.BorderFillSpan.BiBorderFillSpan;
import impl.com.jhenly.juifx.fill.BorderFillSpan.QuadBorderFillSpan;
import impl.com.jhenly.juifx.fill.FillSpan.BorderStrokePosition;
import impl.com.jhenly.juifx.util.Replacer;
import impl.com.jhenly.juifx.util.replacer.DisposableReplacer;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Shape;


/**
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public final class FillSpanHelper {
    
    /** Do not subclass this class. */
    private FillSpanHelper() {}
    
    
    /**************************************************************************
     *                                                                        *
     * Special Fill Span Section                                              *
     *                                                                        *
     *************************************************************************/
//
    /**
     * Checks whether a specified {@code Color} instance is a special
     * identifier.
     * @param c - the color to check
     * @return {@code true} if the specified color is a special identifier,
     *         otherwise {@code false}
     */
//    static boolean paintIsSpecialIdentifier(final Paint c) {
//        return FillSpan.paintIsSpecialIdentifier(c);
//    }
    
    /**
     * Checks whether a specified {@code FillSpan} instance contains a special
     * identifier.
     * <p>
     * <b>Note:</b> this method will return {@code false} if the specified
     * {@code FillSpan} instance is {@code null}.
     * @param span - the fill span to check
     * @return {@code true} if the specified fill span has a special
     *         identifier, otherwise {@code false}
     */
//    static boolean fillSpanIsSpecial(final FillSpan span) {
//        return span == null ? false : span instanceof SpecialFillSpan;
//    }
    
    /**
     * Checks whether a specified list of {@code FillSpan} instances contains a
     * fill span with a special identifier.
     * <p>
     * <b>Note:</b> this method will return {@code false} if the specified
     * list of {@code FillSpan} instances is {@code null}.
     * @param spans - the list of spans to check
     * @return {@code true} if the specified list of fill spans contains a fill
     *         span with a special identifier, otherwise {@code false}
     */
//    static boolean fillSpansContainSpecial(List<FillSpan> spans) {
//        if (spans == null) { return false; }
//        
//        for (FillSpan span : spans) {
//            if (fillSpanIsSpecial(span)) { return true; }
//        }
//        return false;
//    }
    
    
    /**************************************************************************
     *                                                                        *
     * Replace Special Methods                                                *
     *                                                                        *
     *************************************************************************/
    
    /**
     * 
     * @param span
     * @param fillable
     * @return
     */
    static FillSpan getFillSpanFromSpecial(FillSpan span, Fillable fillable) {
        if (span == null || !span.isSpecial() || fillable == null) { return span; }
        
        return fillSpanReplacer().using(fillable).replace(span);
    }
    
    /**
     * 
     * @param spans
     * @param fillable
     * @return
     */
    static List<FillSpan> getFillSpanListFromSpecial(List<FillSpan> spans, Fillable fillable) {
        if (spans == null || spans.isEmpty() || fillable == null) { return spans; }
        
        return fillSpanListReplacer().using(fillable).replace(spans);
    }
    
    /**
     * 
     * @param spans
     * @param fillable
     * @return
     */
    static List<BorderFillSpan> getBorderFillSpanListFromSpecial(List<BorderFillSpan> spans, Fillable fillable) {
        if (spans == null || spans.isEmpty() || fillable == null) { return spans; }
        
        return borderFillSpanListReplacer().using(fillable).replace(spans);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * FillSpan Replacer Implementations                                      *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Base {@link DisposableReplacer} implementation for
     * {@link FillSpanReplacer}, {@link FillSpanListReplacer} and
     * {@link BorderFillSpanListReplacer}.
     * @since JuiFX 1.0
     */
    private static abstract class FillSpanReplacerBase<T> extends DisposableReplacer<T> {
        protected static final List<BackgroundFill> NULL_BG = List.of();
        protected static final List<BorderStroke> NULL_BD = List.of();
        
        // members
        protected Fillable fable;
        
        /** {@inheritDoc} */
        @Override
        protected void dispose() {
            super.dispose();
            fable = null;
        }
        
        /**
         * Sets the {@link Fillable} instance which is used to get replacement
         * paints.
         * @param fillable - the {@code Fillable} to get replacement paints
         *        from
         * @return a reference to this instance
         */
        public Replacer<T> using(Fillable fillable) {
            fable = fillable;
            return this;
        }
        
        /**
         * Checks if the specified {@link FillSpan} contains any special
         * identifier paints and, if so, returns a new {@code FillSpan} with
         * the special identifiers replaced, if the specified {@code FillSpan}
         * contains no special identifiers then this method just returns the
         * specified {@code FillSpan}.
         * 
         * @param span - the fill span to check and, if need be, replace
         * @return a fill span with any specials replaced, or the specified
         *         fill span if it contains no specials
         */
        protected FillSpan replaceSpecial(SpecialFillSpan span) {
            // 'from' and 'to' can't be null
            Paint repFrom = span.from();
            Paint repTo = span.to();
            
            // replace any special id. paint with the fillable's replacement
            repFrom = span.fromIsSpecial() ? getReplacementPaint(repFrom, span.fromIndex(), span.fromBsPos()) : repFrom;
            repTo = span.toIsSpecial() ? getReplacementPaint(repTo, span.toIndex(), span.toBsPos()) : repTo;
            
            return FillSpan.of(repFrom, repTo);
        }
        
        /**
         * Gets the paint to replace the specified special paint identifier
         * with.
         * @param specialId - the special paint identifier
         * @param index - the index of the replacement fill or stroke to get,
         *        if any
         * @param pos - the border stroke position, if any
         * @return the paint to replace the special paint identifier with
         */
        protected Paint getReplacementPaint(Paint specialId, int index, int pos) {
            if (fable == null) {
                // this only happens if we mistakenly don't set 'fable'
                return getReplaceErrorIdentifier(
                    "JuiFX replace special identifier in Fill error: Fillable not set before replacing special identifiers");
            }
            
            if (specialId == USE_BG) {
                return getBgFillPaint(index);
                
            } else if (specialId == USE_BORDER) {
                return getBorderStrokePaint(index, pos);
                
            } else if (specialId == USE_TEXT) {
                return getTextFillPaint();
                
            } else if (specialId == USE_SHAPE) {
                return getShapeFillPaint();
                
            } else if (specialId == USE_STROKE) {
                return getStrokeFillPaint();
                
            }
            
            // if we make it here, we don't know what to replace specialId with
            return getReplaceErrorIdentifier(
                "JuiFX replace USE_UNKNOWN in Fill error: an unknown USE_* specifier was given");
        }
        
        /** 
         * Gets a {@code Color} from a {@link BackgroundFill}.
         * @param index - the index of the background fill to replace the color
         *        with
         * @return the background fill color
         */
        protected abstract Paint getBgFillPaint(int index);
        
        /** 
         * Gets a {@code Color} from a {@link BorderStroke}.
         * @param index - the index of the border stroke to replace the color
         *        with
         * @param pos - the border stroke position, if any
         * @return the border fill color
         */
        protected abstract Paint getBorderStrokePaint(int index, int pos);
        
        /**
         * Gets a border stroke from a specified {@link BorderStroke} at the
         * specified position.
         * @param stroke - the {@code BorderStroke} to get the border stroke
         *        from
         * @param pos - the position to get the border stroke from
         * @return a border stroke from a specified {@link BorderStroke} at the
         *         specified position
         */
        protected static Paint getBorderStroke(BorderStroke stroke, int pos) {
            /* See the 'FillSpan#BorderStrokePosition' enum to understand where the values
             * below are derived from. */
            switch (pos) {
                case 3:
                    return stroke.getLeftStroke();
                case 2:
                    return stroke.getBottomStroke();
                case 1:
                    return stroke.getRightStroke();
                case 0:
                default:
                    return stroke.getTopStroke();
            }
        }
        
        /** Helper that gets the text fill color. */
        protected Paint getTextFillPaint() {
            return paintOrReplaceError(fable.getTextFill());
        }
        
        /** Helper that gets the shape fill color. */
        protected Paint getShapeFillPaint() {
            final Shape shape = fable.getShape();
            if (shape == null) { return getReplaceErrorIdentifier(); }
            
            // 'shape.getFill()' can return null for certain shapes
            return paintOrReplaceError(shape.getFill());
        }
        
        /** Helper that gets the stroke fill color. */
        protected Paint getStrokeFillPaint() {
            final Shape shape = fable.getShape();
            if (shape == null) { return getReplaceErrorIdentifier(); }
            
            // 'shape.getStroke()' can return null for certain shapes
            return paintOrReplaceError(shape.getStroke());
        }
        
        private Paint paintOrReplaceError(Paint p) {
            return (p != null) ? p : getReplaceErrorIdentifier();
        }
        
    } // class FillSpanReplacerBase
    
    /**
     * {@link FillSpanReplacerBase} implementation used to replace special
     * color identifiers in a single {@link FillSpan}.
     */
    private static class FillSpanReplacer extends FillSpanReplacerBase<FillSpan> {
        
        @Override
        protected void dispose() {
            super.dispose();
        }
        
        @Override
        protected FillSpan replaceImpl(FillSpan fillSpan) {
            // return the fill span if neither 'from' nor 'to' is a special id.
            if (!fillSpan.isSpecial()) { return fillSpan; }
            
            return replaceSpecial((SpecialFillSpan) fillSpan);
        }
        
        @Override
        protected Paint getBgFillPaint(int index) {
            final Background bg = fable.getBackground();
            
            final List<BackgroundFill> bgFills = (bg == null) ? NULL_BG : bg.getFills();
            
            // set index to 0 if it is the default of -1
            final int idx = (index < 0) ? 0 : index;
            
            // don't write any error messages, this could get re-applied
            if (idx >= bgFills.size()) { return getReplaceErrorIdentifier(); }
            
            // get the innermost background fill paint
            return bgFills.get((bgFills.size() - 1) - idx).getFill();
        }
        
        @Override
        protected Paint getBorderStrokePaint(int index, int pos) {
            final Border bd = fable.getBorder();
            
            final List<BorderStroke> bdStrokes = (bd == null) ? NULL_BD : bd.getStrokes();
            
            // single span, so set index to 0 if it is the default of -1
            final int idx = (index < 0) ? 0 : index;
            
            // don't write any error messages, this could get re-applied
            if (idx >= bdStrokes.size()) { return getReplaceErrorIdentifier(); }
            
            // get the innermost border stroke color
            return getBorderStroke(bdStrokes.get((bdStrokes.size() - 1) - idx), pos);
        }
        
    } // class FillSpanReplacer
    
    /**
     * {@link FillSpanReplacerBase} implementation used to replace special
     * color identifiers in a list of {@link FillSpan}.
     */
    private static class FillSpanListReplacer extends FillSpanReplacerBase<List<FillSpan>> {
        protected List<FillSpan> ret; // set in replaceImpl
        
        int loopIndex; // current index in the replace loop
        
        protected List<BackgroundFill> bgFills; // background fills
        protected List<BorderStroke> bdStrokes; // border strokes
        
        @Override
        protected void dispose() {
            super.dispose();
            ret = null;
            loopIndex = 0;
            bgFills = NULL_BG;
            bdStrokes = NULL_BD;
        }
        
        @Override
        protected List<FillSpan> replaceImpl(List<FillSpan> spans) {
            // cache bg fills and bd strokes
            setBgFillsAndBdStrokes();
            
            ret = new ArrayList<>(spans);
            for (loopIndex = 0; loopIndex < ret.size(); loopIndex++) {
                final FillSpan span = getSpanFromRet(loopIndex);
                
                // if span isn't special then move to next fill span
                if (!span.isSpecial()) { continue; }
                
                replaceSpanInRet(replaceSpecial((SpecialFillSpan) span));
            }
            
            return ret;
        }
        
        /** Caches bg. fills and bd. strokes so we don't have to get them again. */
        protected void setBgFillsAndBdStrokes() {
            // fable should not be null
            final Background bg = fable.getBackground();
            final Border bd = fable.getBorder();
            
            bgFills = (bg == null) ? NULL_BG : bg.getFills();
            bdStrokes = (bd == null) ? NULL_BD : bd.getStrokes();
        }
        
        /** Replaces a fill span in 'ret' */
        protected void replaceSpanInRet(FillSpan span) {
            ret.set((ret.size() - 1) - loopIndex, span);
        }
        
        protected FillSpan getSpanFromRet(int index) {
            return ret.get((ret.size() - 1) - index);
        }
        
        @Override
        protected Paint getBgFillPaint(int index) {
            // index refers to the fill span's replacement index here
            final int idx = (index == -1) ? loopIndex : index;
            
            // don't write any error messages, this could get re-applied
            if (idx >= bgFills.size()) { return getReplaceErrorIdentifier(); }
            
            return bgFills.get((bgFills.size() - 1) - idx).getFill();
        }
        
        @Override
        protected Paint getBorderStrokePaint(int index, int pos) {
            // index refers to the fill span's replacement index here
            final int idx = (index == -1) ? loopIndex : index;
            
            // don't write any error messages, this could get re-applied
            if (idx >= bdStrokes.size()) { return getReplaceErrorIdentifier(); }
            
            return getBorderStroke(bdStrokes.get((bdStrokes.size() - 1) - idx), pos);
        }
        
        
    } // class FillSpanListReplacer
    
    /**
     * {@link FillSpanReplacerBase} implementation used to replace special
     * color identifiers in a list of {@link BorderFillSpan}.
     */
    private static class BorderFillSpanListReplacer extends FillSpanReplacerBase<List<BorderFillSpan>> {
        protected List<BorderFillSpan> ret; // set in replaceImpl
        
        int loopIndex; // current index in the replace loop
        
        protected List<BackgroundFill> bgFills; // background fills
        protected List<BorderStroke> bdStrokes; // border strokes
        
        @Override
        protected void dispose() {
            super.dispose();
            ret = null;
            loopIndex = 0;
            bgFills = NULL_BG;
            bdStrokes = NULL_BD;
        }
        
        @Override
        protected List<BorderFillSpan> replaceImpl(List<BorderFillSpan> spans) {
            // cache bg fills and bd strokes
            setBgFillsAndBdStrokes();
            
            ret = new ArrayList<>(spans);
            for (loopIndex = 0; loopIndex < ret.size(); loopIndex++) {
                BorderFillSpan span = getSpanFromRet(loopIndex);
                
                // if border fill span doesn't contain a special then continue
                if (!span.isSpecial()) { continue; }
                
                BorderFillSpan repl = null;
                
                if (span.isUniform()) {
                    // uniform border fill span means 't = r = b = l'
                    repl = replaceUni(span);
                } else if (span.getClass() == BiBorderFillSpan.class) {
                    // bi border fill span mean 't = b' and 'r = l'
                    repl = replaceBi(span);
                } else if (span.getClass() == QuadBorderFillSpan.class) {
                    repl = replaceQuad(span);
                }
                
                replaceSpanInRet(repl);
            }
            
            return ret;
        }
        
        
        /** Caches bg. fills and bd. strokes so we don't have to get them again. */
        protected void setBgFillsAndBdStrokes() {
            // fable should not be null
            final Background bg = fable.getBackground();
            final Border bd = fable.getBorder();
            
            bgFills = (bg == null) ? NULL_BG : bg.getFills();
            bdStrokes = (bd == null) ? NULL_BD : bd.getStrokes();
        }
        
        /** Replaces a fill span in 'ret' */
        protected void replaceSpanInRet(BorderFillSpan span) {
            ret.set((ret.size() - 1) - loopIndex, span);
        }
        
        protected BorderFillSpan getSpanFromRet(int index) {
            return ret.get((ret.size() - 1) - index);
        }
        
        @Override
        protected Paint getBgFillPaint(int index) {
            // index refers to the fill span's replacement index here
            final int idx = (index == -1) ? loopIndex : index;
            
            // don't write any error messages, this could get re-applied
            if (idx >= bgFills.size()) { return getReplaceErrorIdentifier(); }
            
            return bgFills.get((bgFills.size() - 1) - idx).getFill();
        }
        
        @Override
        protected Paint getBorderStrokePaint(int index, int pos) {
            // index refers to the fill span's replacement index here
            final int idx = (index == -1) ? loopIndex : index;
            
            // don't write any error messages, this could get re-applied
            if (idx >= bdStrokes.size()) { return getReplaceErrorIdentifier(); }
            
            return getBorderStroke(bdStrokes.get((bdStrokes.size() - 1) - idx), pos);
        }
        
        /** Replaces a uniform border fill span. */
        private BorderFillSpan replaceUni(BorderFillSpan span) {
            return BorderFillSpan.of(replaceSpecial((SpecialFillSpan) span.getTop()));
        }
        
        /** Replaces a bi-uniform (top == bottom, right == left) border fill span. */
        private BorderFillSpan replaceBi(BorderFillSpan span) {
            FillSpan replTop = span.getTop();
            FillSpan replRight = span.getRight();
            
            if (replTop.isSpecial()) {
                replTop = replaceSpecial((SpecialFillSpan) replTop);
            }
            if (replRight.isSpecial()) {
                replRight = replaceSpecial((SpecialFillSpan) replRight);
            }
            
            return BorderFillSpan.of(replTop, replRight);
        }
        
        /** Replaces a non-uniform (top != right != bottom != left) border fill span.  */
        private BorderFillSpan replaceQuad(BorderFillSpan span) {
            FillSpan replTop = span.getTop();
            FillSpan replRight = span.getRight();
            FillSpan replBottom = span.getBottom();
            FillSpan replLeft = span.getLeft();
            
            if (replTop.isSpecial()) {
                replTop = replaceSpecial((SpecialFillSpan) replTop);
            }
            if (replRight.isSpecial()) {
                replRight = replaceSpecial((SpecialFillSpan) replRight);
            }
            if (replBottom.isSpecial()) {
                replBottom = replaceSpecial((SpecialFillSpan) replBottom);
            }
            if (replLeft.isSpecial()) {
                replLeft = replaceSpecial((SpecialFillSpan) replLeft);
            }
            
            return BorderFillSpan.of(replTop, replRight, replBottom, replLeft);
        }
        
    } // class BorderFillSpanListReplacer
    
    /**************************************************************************
     *                                                                        *
     * Fill Span Replacer Holders                                             *
     *                                                                        *
     *************************************************************************/
//    
    // lazy, thread safe instantiation for fill span replacer
    private static class FillSpanReplacerHolder {
        static final FillSpanReplacer INSTANCE = new FillSpanReplacer();
        
        private FillSpanReplacerHolder() {
            throw new IllegalAccessError("the FillSpanReplacerHolder class should not be instantiated");
        }
    }
    private static FillSpanReplacer fillSpanReplacer() { return FillSpanReplacerHolder.INSTANCE; }
    
    // lazy, thread safe instantiation for fill span list replacer
    private static class FillSpanListReplacerHolder {
        static final FillSpanListReplacer INSTANCE = new FillSpanListReplacer();
        
        private FillSpanListReplacerHolder() {
            throw new IllegalAccessError("the FillSpanListReplacerHolder class should not be instantiated");
        }
    }
    private static FillSpanListReplacer fillSpanListReplacer() {
        return FillSpanListReplacerHolder.INSTANCE;
    }
    
    // lazy, thread safe instantiation for border fill span list replacer
    private static class BorderFillSpanListReplacerHolder {
        static final BorderFillSpanListReplacer INSTANCE = new BorderFillSpanListReplacer();
        
        private BorderFillSpanListReplacerHolder() {
            throw new IllegalAccessError("the BorderFillSpanListReplacerHolder class should not be instantiated");
        }
    }
    private static BorderFillSpanListReplacer borderFillSpanListReplacer() {
        return BorderFillSpanListReplacerHolder.INSTANCE;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Replace Special Error Methods                                          *
     *                                                                        *
     *************************************************************************/
//                                                                               
    /**
     * Gets the {@code Color} object that represents a replace error.
     * 
     * @return the {@code Color} object that represents a replace error
     */
    static Color getReplaceErrorIdentifier() { return ReplaceHolder.REP_ERROR; }
    
    /**
     * Gets the {@code Color} object that represents a replace error and writes
     * a specified error message to {@link System#err}.
     * @param errorMsg - the error message to write to {@code System.err}
     * @return the {@code Color} object that represents a replace error
     */
    static Color getReplaceErrorIdentifier(String errorMsg) {
        System.err.println(errorMsg);
        return ReplaceHolder.REP_ERROR;
    }
    
    // lazy, thread safe instantiation
    static class ReplaceHolder {
        static final Color REP_ERROR = new Color(1.0, 1.0, 1.0, 0.0);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Border Stroke Position                                                 *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets the {@link FillSpan.BorderStrokePosition BorderStrokePosition} enum
     * associated with the specified ordinal.
     * <p>
     * If no enum is associated with the specified ordinal, then
     * {@link FillSpan.BorderStrokePosition#TOP} will be returned.
     * 
     * @param ordinal - the {@code BorderStrokePosition} enum's ordinal
     * @return the {@code BorderStrokePosition} enum associated with the
     *         specified ordinal
     *         
     * @see Enum#ordinal()
     */
    static BorderStrokePosition getBsPosFromOrdinal(int ordinal) {
        switch (ordinal) {
            case 3:
                return BorderStrokePosition.LEFT;
            case 2:
                return BorderStrokePosition.BOTTOM;
            case 1:
                return BorderStrokePosition.RIGHT;
            case 0:
            default:
                return BorderStrokePosition.TOP;
        }
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Linear And Radial Gradient Checker                                     *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Checks whether a specified {@code Paint} instance is either a
     * {@link LinearGradient} or a {@link RadialGradient}.
     * 
     * @param p - the {@code Paint} to check
     * @return {@code true} if the specified {@code Paint} is a gradient,
     *         otherwise {@code false}
     */
    static boolean paintIsGradient(Paint p) {
        if (p == null) { return false; }
        
        return p.getClass() == LinearGradient.class || p.getClass() == RadialGradient.class;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * FillSpan Equals Methods                                                *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Used to check for equality between two {@code FillSpan} instances.
     * <p>
     * This method should only be used after the fill spans have been added to
     * the cache, if {@link FillSpanCache} is enabled.
     * @param a - the fill span to check against {@code b}
     * @param b - the fill span to check against {@code a}
     * @return {@code true} if {@code a} equals {@code b}, otherwise
     *         {@code false}
     */
    static <T extends FillSpan> boolean fillSpansAreEqual(T a, T b) {
        return a == null ? b == null : (FillSpanCache.isCacheEnabled() ? a == b : a.equals(b));
    }
    
    /**
     * Used to check for equality between two lists of {@code FillSpan}
     * instances.
     * <p>
     * This method should only be used after any fill spans have been added to
     * the cache, if {@link FillSpanCache} is enabled.
     * @param a - the fill span list to check against {@code b}
     * @param b - the fill span list to check against {@code a}
     * @return {@code true} if {@code a} equals {@code b}, otherwise
     *         {@code false}
     */
    static boolean fillSpanListsAreEqual(List<? extends FillSpan> a, List<? extends FillSpan> b) {
        if (a == b || a == null || b == null) { return a == b; }
        
        if (a.size() != b.size()) { return false; }
        
        if (FillSpanCache.isCacheEnabled()) {
            for (int i = 0, n = a.size(); i < n; i++) {
                if (a.get(i) != b.get(i)) { return false; }
            }
            
            // if we reach this point the lists are equal
            return true;
        }
        
        // if FillSpanCache is disabled then use object equality
        return a.equals(b);
    }
    
    /**
     * Used to check for equality between two arrays of {@code FillSpan}
     * instances.
     * <p>
     * This method should only be used after any fill spans have been added to
     * the cache, if {@link FillSpanCache} is enabled.
     * @param a - the fill span array to check against {@code b}
     * @param b - the fill span array to check against {@code a}
     * @return {@code true} if {@code a} equals {@code b}, otherwise
     *         {@code false}
     */
    static <T extends FillSpan> boolean fillSpanArraysAreEqual(T[] a, T[] b) {
        if (a == b || a == null || b == null) { return a == b; }
        
        if (a.length != b.length) { return false; }
        
        // use reference equality if FillSpanCache is enabled
        if (FillSpanCache.isCacheEnabled()) {
            for (int i = 0, n = a.length; i < n; i++) {
                if (a[i] != b[i]) { return false; }
            }
            return true;
        }
        
        for (int i = 0, n = a.length; i < n; i++) {
            if (!a[i].equals(b[i])) { return false; }
        }
        
        // if we reach this point the arrays are equal
        return true;
    }
    
    /**
     * Used to check for equality between two lists of {@code FillSpan}
     * instances.
     * <p>
     * This method should only be used after any fill spans have been added to
     * the cache, if {@link FillSpanCache} is enabled.
     * @param a - the fill span list to check against {@code b}
     * @param b - the fill span list to check against {@code a}
     * @return {@code true} if {@code a} equals {@code b}, otherwise
     *         {@code false}
     */
    static boolean borderFillSpanListsAreEqual(List<? extends BorderFillSpan> a, List<? extends BorderFillSpan> b) {
        if (a == b || a == null || b == null) { return a == b; }
        
        return a.equals(b);
    }
    
    /**
     * Used to check for equality between two arrays of {@code BorderFillSpan}
     * instances.
     * 
     * @param a - the border fill span array to check against {@code b}
     * @param b - the border fill span array to check against {@code a}
     * @return {@code true} if {@code a} equals {@code b}, otherwise
     *         {@code false}
     */
    static <T extends BorderFillSpan> boolean borderFillSpanArraysAreEqual(T[] a, T[] b) {
        if (a == b || a == null || b == null) { return a == b; }
        
        if (a.length != b.length) { return false; }
        
        for (int i = 0, n = a.length; i < n; i++) {
            if (!a[i].equals(b[i])) { return false; }
        }
        
        // if we reach this point the arrays are equal
        return true;
    }
    
}

package com.jhenly.juifx.fill;

import static com.jhenly.juifx.fill.FillSpan.USE_BG;
import static com.jhenly.juifx.fill.FillSpan.USE_BORDER;
import static com.jhenly.juifx.fill.FillSpan.USE_SHAPE;
import static com.jhenly.juifx.fill.FillSpan.USE_STROKE;
import static com.jhenly.juifx.fill.FillSpan.USE_TEXT;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jhenly.juifx.control.Fillable;
import com.jhenly.juifx.util.replacer.DisposableReplacer;

import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;


public final class FillSpanHelper {
    
    /** Do not subclass this class. */
    private FillSpanHelper() {}
    
    
    /**************************************************************************
     *                                                                        *
     * Special Fill Span Section                                              *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Checks whether a specified {@code Color} instance is a special
     * identifier.
     * @param c - the color to check
     * @return {@code true} if the specified color is a special identifier,
     *         otherwise {@code false}
     */
    static boolean colorIsSpecialIdentifier(final Color c) { return FillSpan.colorIsSpecialIdentifier(c); }
    
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
    static boolean fillSpanIsSpecial(final FillSpan span) {
        return span == null ? false : span instanceof SpecialFillSpan;
    }
    
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
    static boolean fillSpansContainSpecial(List<FillSpan> spans) {
        if (spans == null) { return false; }
        
        for (FillSpan span : spans) {
            if (fillSpanIsSpecial(span)) { return true; }
        }
        return false;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Replace Special Identifiers                                            *
     *                                                                        *
     *************************************************************************/
    
//    /** Replace text fill span with special. */
//    static final int REP_TEXT = 0;
//    /** Replace shape fill span with special. */
//    static final int REP_SHAPE = 1;
//    /** Replace stroke fill span with special. */
//    static final int REP_STROKE = 2;
//    
//    /** Replace special with unknown, i.e. replace error. */
//    static final int REP_WITH_UNKNOWN = -1;
//    /** Replace special with text fill. */
//    static final int REP_WITH_TEXT = 0;
//    /** Replace special with shape fill. */
//    static final int REP_WITH_SHAPE = 1;
//    /** Replace special with stroke fill. */
//    static final int REP_WITH_STROKE = 2;
//    /** Replace special with innermost background fill. */
//    static final int REP_WITH_BG = 3;
    
    
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
        if (span == null || !fillSpanIsSpecial(span)) { return span; }
        if (fillable == null) { return span; /* this shouldn't happen */ }
        
        return fillSpanReplacer().using(fillable).replace(span);
    }
    
    /**
     * 
     * @param spans
     * @param fillable
     * @return
     */
    static List<FillSpan> getFillSpanListFromSpecial(List<FillSpan> spans, Fillable fillable) {
        if (spans == null || spans.isEmpty()) { return spans; }
        if (fillable == null) { return spans; }
        
        return fillSpanListReplacer().using(fillable).replace(spans);
    }
    
    /**
     * Base {@link DisposableReplacer} implementation for
     * {@link FillSpanReplacer} and {@link FillSpanListReplacer}.
     * @since JuiFX 1.0
     */
    private static abstract class FillSpanReplacerBase<T> extends DisposableReplacer<T> {
        
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
         * colors.
         * @param fillable - the {@code Fillable} to get replacement colors
         *        from
         * @return a reference to this instance
         */
        public FillSpanReplacerBase<T> using(Fillable fillable) {
            fable = fillable;
            return this;
        }
        
        /**
         * Convenience method that simply returns the result of calling
         * {@code using(fillable).replace(fillSpan)}.
         * 
         * @param toReplace - the object to replace some aspect(s) of, or to
         *        replace entirely
         * @param fillable - the {@code Fillable} to get replacement colors
         *        from
         * @return an object with some aspect(s) replaced
         */
        public T replaceUsing(T toReplace, Fillable fillable) { return using(fillable).replace(toReplace); }
        
        /**
         * Checks if the specified {@link FillSpan} contains any special
         * identifier colors and, if so, returns a new {@code FillSpan} with
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
            Color repFrom = span.from();
            Color repTo = span.to();
            
            // replace any special id. color with the fillable's replacement
            repFrom = span.fromIsSpecial() ? getReplacementColor(repFrom, span.fromIndex(), span.fromBsPos()) : repFrom;
            repTo = span.toIsSpecial() ? getReplacementColor(repTo, span.toIndex(), span.toBsPos()) : repTo;
            
            return FillSpan.of(repFrom, repTo);
        }
        
        /**
         * Gets the color to replace the specified special color identifier
         * with.
         * @param specialId - the special color identifier
         * @param index - the index of the replacement fill or stroke to get,
         *        if any
         * @param pos - the border stroke position, if any
         * @return the color to replace the special color identifier with
         */
        protected Color getReplacementColor(Color specialId, int index, int pos) {
            if (fable == null) {
                // this only happens if we mistakenly don't set 'fable'
                return getReplaceErrorIdentifier(
                    "JuiFX replace special identifier in Fill error: Fillable not set before replacing special identifiers");
            }
            
            if (specialId == USE_BG) {
                return getBgFillColor(index);
                
            } else if (specialId == USE_BORDER) {
                return getBorderStrokeColor(index, pos);
                
            } else if (specialId == USE_TEXT) {
                return getTextFillColor();
                
            } else if (specialId == USE_SHAPE) {
                return getShapeFillColor();
                
            } else if (specialId == USE_STROKE) {
                return getStrokeFillColor();
                
            }
            
            // if we make it here, we don't know what to replace specialId with
            return getReplaceErrorIdentifier(
                "JuiFX replace USE_UNKNOWN in Fill error: an unknown USE_* specifier was used");
        }
        
        /** 
         * Gets a {@code Color} from a {@link BackgroundFill}.
         * @param index - the index of the background fill to replace the color
         *        with
         * @return the background fill color
         */
        protected abstract Color getBgFillColor(int index);
        
        /** 
         * Gets a {@code Color} from a {@link BorderStroke}.
         * @param index - the index of the border stroke to replace the color
         *        with
         * @param pos - the border stroke position, if any
         * @return the border fill color
         */
        protected abstract Color getBorderStrokeColor(int index, int pos);
        
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
            /* See the 'FillSpan#BorderStrokePosition' enum to understand where
             * the values below are derived from. */
            switch (pos) {
                case 0:
                    return stroke.getTopStroke();
                case 1:
                    return stroke.getRightStroke();
                case 2:
                    return stroke.getBottomStroke();
                case 3:
                    return stroke.getLeftStroke();
                default:
                    return stroke.getTopStroke();
            }
        }
        
        /** Helper that gets the text fill color. */
        protected Color getTextFillColor() {
            final Paint tFill = fable.getTextFill();
            if (tFill == null) {
                return getReplaceErrorIdentifier(
                    "JuiFX replace USE_TEXT in Fill error: Fillable.getTextFill() returned 'null'");
            }
            
            // text fill might not be a Color (i.e. LinearGradient, ...)
            try {
                return (Color) tFill;
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_TEXT in Fill error: " + e.getLocalizedMessage());
            }
        }
        
        /** Helper that gets the shape fill color. */
        protected Color getShapeFillColor() {
            final Shape shape = fable.getShape();
            if (shape == null) {
                return getReplaceErrorIdentifier(
                    "JuiFX replace USE_SHAPE in Fill error: Fillable.getShape() returned 'null'");
            }
            
            // shape fill might not be a Color (i.e. LinearGradient, ...)
            try {
                // 'shape.getFill()' returns 'Color.BLACK' if null
                return (Color) shape.getFill();
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_SHAPE in Fill error: " + e.getLocalizedMessage());
            }
        }
        
        /** Helper that gets the stroke fill color. */
        protected Color getStrokeFillColor() {
            final Paint stroke = fable.getStroke();
            if (stroke == null) {
                return getReplaceErrorIdentifier(
                    "JuiFX replace USE_STROKE in Fill error: Fillable.getStroke() returned 'null'.");
            }
            
            // stroke might not be a Color (i.e. LinearGradient, ...)
            try {
                return (Color) stroke;
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_STROKE in Fill error: " + e.getLocalizedMessage());
            }
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
            if (!fillSpanIsSpecial(fillSpan)) { return fillSpan; }
            
            return replaceSpecial((SpecialFillSpan) fillSpan);
        }
        
        @Override
        protected Color getBgFillColor(int index) {
            final List<BackgroundFill> bgFills = fable.getBackground().getFills();
            
            // set index to 0 if it is the default of -1
            index = (index < 0) ? 0 : index;
            
            // don't write any error messages, this could get re-applied
            if (index >= bgFills.size()) { return getReplaceErrorIdentifier(); }
            
            
            // background fill might not be a Color (i.e. LinearGradient, ...)
            try {
                // get the innermost background fill color
                return (Color) bgFills.get((bgFills.size() - 1) - index).getFill();
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_BG in Fill error: " + e.getLocalizedMessage());
            }
        }
        
        @Override
        protected Color getBorderStrokeColor(int index, int pos) {
            final List<BorderStroke> bStrokes = fable.getBorder().getStrokes();
            
            // single span, so set index to 0 if it is the default of -1
            index = (index < 0) ? 0 : index;
            
            // don't write any error messages, this could get re-applied
            if (index >= bStrokes.size()) { return getReplaceErrorIdentifier(); }
            
            
            // border stroke might not be a Color (i.e. LinearGradient, ...)
            try {
                // get the innermost border stroke color
                return (Color) getBorderStroke(bStrokes.get((bStrokes.size() - 1) - index), pos);
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_BORDER in Fill error: " + e.getLocalizedMessage());
            }
        }
        
    } // class FillSpanReplacer
    
    
    private static class FillSpanListReplacer extends FillSpanReplacerBase<List<FillSpan>> {
        protected List<FillSpan> ret; // set in replaceImpl
        
        int loopIndex; // current index in the replace loop
        
        // lists only set if needed
        protected List<BackgroundFill> bgFills; // background fills
        protected List<BorderStroke> bStrokes; // border strokes
        
        @Override
        protected void dispose() {
            super.dispose();
            ret = null;
            loopIndex = 0;
            bgFills = null;
            bStrokes = null;
        }
        
        @Override
        protected List<FillSpan> replaceImpl(List<FillSpan> spans) {
            // fable must not be null or empty
            bgFills = fable.getBackground().getFills();
            bStrokes = fable.getBorder().getStrokes();
            
            ArrayDeque<FillSpan> fillSpans = new ArrayDeque<>(spans);
            
            ret = new ArrayList<>(Math.min(bgFills.size(), fillSpans.size()));
            for (loopIndex = 0; loopIndex < ret.size(); loopIndex++) {
                FillSpan span = getSpanFromRet(loopIndex);
                
                // if span isn't special then move to next fill span
                if (!fillSpanIsSpecial(span)) { continue; }
                
                replaceSpanInRet(loopIndex, replaceSpecial((SpecialFillSpan) span));
            }
            
            return Collections.unmodifiableList(ret);
        }
        
        /** Replaces a fill span in 'ret' */
        private void replaceSpanInRet(int index, FillSpan span) {
            ret.set((ret.size() - 1) - index, span);
        }
        
        private FillSpan getSpanFromRet(int index) {
            return ret.get((ret.size() - 1) - index);
        }
        
        @Override
        protected Color getBgFillColor(int index) {
            // index refers to the fill span's replacement index here
            index = (index == -1) ? loopIndex : index;
            
            // don't write any error messages, this could get re-applied
            if (index >= bgFills.size()) { return getReplaceErrorIdentifier(); }
            
            // background fill might not be a Color (i.e. LinearGradient, ...)
            try {
                return (Color) bgFills.get((bgFills.size() - 1) - index).getFill();
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_BG error: " + e.getLocalizedMessage());
            }
        }
        
        @Override
        protected Color getBorderStrokeColor(int index, int pos) {
            // index refers to the fill span's replacement index here
            index = (index == -1) ? loopIndex : index;
            
            // don't write any error messages, this could get re-applied
            if (index >= bStrokes.size()) { return getReplaceErrorIdentifier(); }
            
            // border stroke might not be a Color (i.e. LinearGradient, ...)
            try {
                return (Color) getBorderStroke(bStrokes.get((bStrokes.size() - 1) - index), pos);
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_BORDER in Fill error: " + e.getLocalizedMessage());
            }
        }
        
        
    } // class FillSpanListReplacer
    
    
    /**************************************************************************
     *                                                                        *
     * Fill Span Replacer Holders                                             *
     *                                                                        *
     *************************************************************************/
    
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
    private static FillSpanListReplacer fillSpanListReplacer() { return FillSpanListReplacerHolder.INSTANCE; }
    
    
    /**************************************************************************
     *                                                                        *
     * Replace Special Error Methods                                          *
     *                                                                        *
     *************************************************************************/
    
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
     * Equals Methods                                                         *
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
    static boolean fillSpansAreEqual(FillSpan a, FillSpan b) {
        return a == null ? b == null : FillSpanCache.isCacheEnabled() ? a == b : a.equals(b);
    }
    
    /**
     * Used to check for equality between two lists {@code FillSpan} instances.
     * <p>
     * This method should only be used after any fill spans have been added to
     * the cache, if {@link FillSpanCache} is enabled.
     * @param a - the fill span to check against {@code b}
     * @param b - the fill span to check against {@code a}
     * @return {@code true} if {@code a} equals {@code b}, otherwise
     *         {@code false}
     */
    static boolean fillSpanListsAreEqual(List<FillSpan> a, List<FillSpan> b) {
        if (a == b || a == null || b == null) { return a == b; }
        
        if (!FillSpanCache.isCacheEnabled()) { return a.equals(b); }
        
        if (a.size() != b.size()) { return false; }
        for (int i = 0, n = a.size(); i < n; i++) {
            if (a.get(i) != b.get(i)) { return false; }
        }
        
        // if we reach this point the lists are equal
        return true;
    }
    
}

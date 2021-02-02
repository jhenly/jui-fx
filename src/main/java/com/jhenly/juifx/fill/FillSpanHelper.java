package com.jhenly.juifx.fill;

import static com.jhenly.juifx.fill.SpecialFillSpan.INHERIT;
import static com.jhenly.juifx.fill.SpecialFillSpan.USE_BG;
import static com.jhenly.juifx.fill.SpecialFillSpan.USE_SHAPE;
import static com.jhenly.juifx.fill.SpecialFillSpan.USE_STROKE;
import static com.jhenly.juifx.fill.SpecialFillSpan.USE_TEXT;

import java.util.List;

import com.jhenly.juifx.control.Fillable;
import com.jhenly.juifx.util.Replacer;
import com.jhenly.juifx.util.replacer.AbstractReplacer;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

public final class FillSpanHelper {
    
    /** Do not subclass this class. */
    private FillSpanHelper() {}
    
    
    /**************************************************************************
     *                                                                        *
     * Replace Special Identifiers                                            *
     *                                                                        *
     *************************************************************************/
    
    /** Replace text fill span with special. */
    static final int REP_TEXT = 0;
    /** Replace shape fill span with special. */
    static final int REP_SHAPE = 1;
    /** Replace stroke fill span with special. */
    static final int REP_STROKE = 2;
    
    /** Replace special with unknown, i.e. replace error. */
    static final int REP_WITH_UNKNOWN = -1;
    /** Replace special with text fill. */
    static final int REP_WITH_TEXT = 0;
    /** Replace special with shape fill. */
    static final int REP_WITH_SHAPE = 1;
    /** Replace special with stroke fill. */
    static final int REP_WITH_STROKE = 2;
    /** Replace special with innermost background fill. */
    static final int REP_WITH_BG = 3;
    
    /**************************************************************************
     *                                                                        *
     * Replace Special Methods                                                *
     *                                                                        *
     *************************************************************************/
    
    static final FillSpan getFillSpanFromSpecial(FillSpan span, Fillable fillable) {
        if (span == null) { return FillSpan.getNullArgsInstance(); }
        if (!fillSpanIsSpecial(span)) { return span; }
        if (fillable == null) { return span; /* this shouldn't happen */ }
        
        Replacer<FillSpan> r = () -> { return using(new FillSpanReplacer(span, fillable)); };
        
        return new FillSpanReplacer(span, fillable).replace();
    }
    
    private static abstract class FillSpanReplacerBase<T> extends AbstractReplacer<T> {
        protected Fillable fable;
        protected List<BackgroundFill> bgFillCache; // only created if needed
        
        /** 
         * Constructor that sets the object that {@link #replaceImpl()} will
         * act on and the {@link Fillable} that will be used to help replace
         * an aspect, or aspects, of said object.
         * 
         * @param toReplace - the object to replace an aspect, or aspects, of
         * @param fillable - the {@code Fillable} to help with replacing
         */
        protected FillSpanReplacerBase(T toReplace, Fillable fillable) {
            super(toReplace);
            fable = fillable;
        }
        
        /** Help out the garbage collector. */
        @Override
        protected void dispose() {
            super.dispose();
            fable = null;
            bgFillCache = null;
        }
        
        /**
         * Gets the color to replace the specified special color identifier
         * with.
         * <p>
         * The {@code index} parameter is only used when the special color
         * identifier relates to replacement with a background fill color. In
         * that situation the value of {@code index} should be {@code 0} for a
         * single fill span, or the index of the fill span in a list of fill
         * spans.
         * 
         * @param specialId - the special color identifier
         * @param index - the index of the fill span
         * @return the color to replace the special color identifier with
         */
        protected Color getReplacementColor(Color specialId, int index) {
            
            if (specialId == USE_TEXT) {
                return getTextFillColor();
                
            } else if (specialId == USE_SHAPE) {
                return getShapeFillColor();
                
            } else if (specialId == USE_STROKE) {
                return getStrokeFillColor();
                
            } else if (specialId == USE_BG) {
                // use a cached value so we don't have to keep getting bgFills
                if (bgFillCache == null) {
                    // first we check if Fillable's background is null
                    final Background bg = fable.getBackground();
                    if (bg == null) {
                        return getReplaceErrorIdentifier(
                            "JuiFX replace USE_BG error: Fillable.getBackground() returned 'null'");
                    }
                    
                    // then if Fillable's list of background fills is null
                    final List<BackgroundFill> bgFills = bg.getFills();
                    if (bg.getFills() == null) {
                        return getReplaceErrorIdentifier(
                            "JuiFX replace USE_BG error: Fillable.getBackground().getFills() returned 'null'");
                    }
                    
                    // if we make it here then bg != null and bgFills != null
                    bgFillCache = bgFills;
                }
                
                return getBgFillColor(bgFillCache, index);
            }
            
            // if we make it here, we don't know what to replace specialId with
            return getReplaceErrorIdentifier("JuiFX replace USE_UNKNOWN error: an unknown USE_* specifier was used");
            
        }
        
        /** 
         * Gets the {@code Color} from a {@code BackgroundFill} instance at the
         * specified <b><i>reverse index</i></b> in a list.
         * <p>
         * The <b><i>reverse index</i></b> is the index of an element in an
         * array, or list, if the array, or list, were to be reversed. That is
         * to say, given an array {@code arr} and a reverse index {@code ri},
         * the array {@code arr} is accessed via
         * {@code arr[(arr.length - 1) - ri]}.
         * <p>
         * For example, the following code block shows the
         * <i>regular indexes</i> and <i>reverse indexes</i> of an array of
         * strings: <pre>
         * String[] strs = { "a", "b", "c" }
         * 
         * // the normal indexes
         * strs[0] == "a" strs[1] == "b" strs[2] == "c"
         * 
         * // the reverse indexes
         * strs[0] == "c" strs[1] == "b" strs[2] == "a"</pre>
         * 
         * @param bgFills - the list of background fills
         * @param reverseIndex - the reverse index of the background fill to
         *        get the color from
         */
        protected Color getBgFillColor(List<BackgroundFill> bgFills, int reverseIndex) {
            // bounds checking
            if (reverseIndex < 0 || reverseIndex >= bgFills.size()) {
                // we should never be out of bounds, but just in case
                return getReplaceErrorIdentifier(
                    "JuiFX replace USE_BG error: index " + reverseIndex + "is out of bounds");
            }
            
            // background fill might not be a Color (i.e. LinearGradient, ...)
            try {
                return (Color) bgFills.get((bgFills.size() - 1) - reverseIndex).getFill();
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_BG error: " + e.getLocalizedMessage());
            }
        }
        
        /** Helper that gets the text fill color. */
        protected Color getTextFillColor() {
            final Paint tFill = fable.getTextFill();
            if (tFill == null) {
                return getReplaceErrorIdentifier(
                    "JuiFX replace USE_TEXT error: Fillable.getTextFill() returned 'null'");
            }
            
            // text fill might not be a Color (i.e. LinearGradient, ...)
            try {
                return (Color) tFill;
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_TEXT error: " + e.getLocalizedMessage());
            }
        }
        
        /** Helper that gets the shape fill color. */
        protected Color getShapeFillColor() {
            final Shape shape = fable.getShape();
            if (shape == null) {
                return getReplaceErrorIdentifier("JuiFX replace USE_SHAPE error: Fillable.getShape() returned 'null'");
            }
            
            final Paint shFill = shape.getFill();
            if (shFill == null) {
                return getReplaceErrorIdentifier(
                    "JuiFX replace USE_SHAPE error: Fillable.getShape().getFill() returned 'null'");
            }
            
            // shape fill might not be a Color (i.e. LinearGradient, ...)
            try {
                return (Color) shFill;
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_SHAPE error: " + e.getLocalizedMessage());
            }
        }
        
        /** Helper that gets the stroke fill color. */
        protected Color getStrokeFillColor() {
            final Paint stroke = fable.getStroke();
            if (stroke == null) {
                return getReplaceErrorIdentifier(
                    "JuiFX replace USE_STROKE error: Fillable.getStroke() returned 'null'.");
            }
            
            // stroke might not be a Color (i.e. LinearGradient, ...)
            try {
                return (Color) stroke;
            } catch (Exception e) {
                return getReplaceErrorIdentifier("JuiFX replace USE_STROKE error: " + e.getLocalizedMessage());
            }
        }
        
        
    } // class FillSpanReplacerBase
    
    
    private static class FillSpanReplacer extends FillSpanReplacerBase<FillSpan> {
        
        FillSpanReplacer(FillSpan fillSpan, Fillable fillable) {
            // sets 'toReplace' to 'fillSpan' and 'fable' to 'fillable'
            super(fillSpan, fillable);
        }
        
        @Override
        protected FillSpan replaceImpl() {
            // 'from' and 'to' can't be null
            Color repFrom = toReplace.from();
            Color repTo = toReplace.to();
            
            if (colorIsSpecialIdentifier(repFrom)) {
                repFrom = getReplacementColor(repFrom, 0);
            }
            
            if (colorIsSpecialIdentifier(repTo)) {
                repTo = getReplacementColor(repTo, 0);
            }
            
            return FillSpan.of(repFrom, repTo);
        }
        
    } // class FillSpanReplacer
    
    
    static final List<FillSpan> getBgFillSpansFromSpecial(List<FillSpan> spans, Fillable fillable) {
        if (spans == null || !fillSpansContainSpecial(spans)) { return spans; }
        if (fillable == null || fillable.getBackground() == null) { return spans; }
        
        final List<BackgroundFill> bgFills = fillable.getBackground().getFills();
        if (bgFills == null || bgFills.isEmpty()) { return spans; }
        
        
        return newFillSpansReplacer(spans, fillable).replace();
    }
    
    
    private static class FillSpansReplacer extends FillSpanReplacerBase<List<FillSpan>> {
        List<FillSpan> spans;
        List<BackgroundFill> bgFills;
        int spansSize;
        int bgFillsSize;
        int minSize;
        
        /** Help out the garbage collector. */
        @Override
        protected void dispose() {
            super.dispose();
            
            spans = null;
            bgFills = null;
        }
        
        FillSpansReplacer(List<FillSpan> fillSpans, Fillable fillable, List<BackgroundFill> fills) {
            super(null, fillable);
            spans = fillSpans;
            bgFills = fills;
            spansSize = spans.size();
            bgFillsSize = bgFills.size();
            minSize = Math.min(spansSize, bgFillsSize);
        }
        
        FillSpansReplacer(List<FillSpan> fillSpans, Fillable fillable) {
            this(fillSpans, fillable, fillable.getBackground().getFills());
        }
        
        @Override
        protected List<FillSpan> replaceImpl() {
            
            for (int i = 0; i < minSize; i++) {
                
            }
            
            return null;
        }
        
        private Color getBgFillColor(int index) {
            try {
                return (Color) bgFills.get(index).getFill();
            } catch (Exception e) {
                return getReplaceErrorIdentifier();
            }
        }
        
    } // class FillSpansReplacer
    
    static final List<FillSpan> replaceUseBgWithBgFills(List<FillSpan> spans, Background bg) {
        
        if (spans == null || spans.isEmpty()) { return FillSpan.getNullListArgsInstance(); }
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
            
            Color newFrom = getBgFillColor(span.from(), bgFills, size - i);
            Color newTo = getBgFillColor(span.to(), bgFills, size - i);
            
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
        
        return FillSpan.of(fromColors, toColors);
    }
    
    private static final List<FillSpan> replaceHelperBgLarger(List<FillSpan> spans, List<BackgroundFill> bgFills) {
        final int ssize = spans.size();
        final int bgSize = bgFills.size();
        final Color[] fromColors = new Color[ssize];
        final Color[] toColors = new Color[ssize];
        
        for (int i = 0; i < ssize; i++) {
            final FillSpan span = spans.get(ssize - i);
            
            Color newFrom = getBgFillColor(span.from(), bgFills, bgSize - i);
            Color newTo = getBgFillColor(span.to(), bgFills, bgSize - i);
            
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
        
        return FillSpan.of(fromColors, toColors);
    }
    
    private static final List<FillSpan> replaceHelperBgSmaller(List<FillSpan> spans, List<BackgroundFill> bgFills) {
        final int ssize = spans.size();
        final int bgSize = bgFills.size();
        final Color[] fromColors = new Color[ssize];
        final Color[] toColors = new Color[ssize];
        
        for (int i = 0; i < bgSize; i++) {
            final FillSpan span = spans.get(ssize - i);
            
            Color newFrom = getBgFillColor(span.from(), bgFills, bgSize - i);
            Color newTo = getBgFillColor(span.to(), bgFills, bgSize - i);
            
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
            
            Color from = (span.from() == USE_BG) ? Color.TRANSPARENT : span.from();
            Color to = (span.to() == USE_BG) ? Color.TRANSPARENT : span.to();
            
            fromColors[i] = from;
            toColors[i] = to;
        }
        
        return FillSpan.of(fromColors, toColors);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Replace Special Error Methods                                          *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets the {@code Color} object that represents a replace error.
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
     * FillSpanSpecial Section                                                *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Checks whether a specified {@code Color} instance is a special
     * identifier.
     * @param c - the color to check
     * @return {@code true} if the specified color is a special identifier,
     *         otherwise {@code false}
     */
    static final boolean colorIsSpecialIdentifier(final Color c) {
        return c == INHERIT || c == USE_BG || c == USE_TEXT || c == USE_SHAPE || c == USE_STROKE;
    }
    
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
    static final boolean fillSpanIsSpecial(final FillSpan span) {
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
    static final boolean fillSpansContainSpecial(List<FillSpan> spans) {
        if (spans == null) { return false; }
        
        for (FillSpan span : spans) {
            if (fillSpanIsSpecial(span)) { return true; }
        }
        return false;
    }
    
    
}

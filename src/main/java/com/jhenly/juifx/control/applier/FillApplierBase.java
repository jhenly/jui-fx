package com.jhenly.juifx.control.applier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jhenly.juifx.control.Fillable;

import impl.com.jhenly.juifx.fill.BorderFillSpan;
import impl.com.jhenly.juifx.fill.Fill;
import impl.com.jhenly.juifx.fill.FillHelper;
import impl.com.jhenly.juifx.fill.FillSpan;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;


public class FillApplierBase<F extends Fillable> implements FillApplier<F> {
    
    /**
     * The {@code Fillable} that is referencing this {@code FillApplier}. There
     * is a one-to-one relationship between a {@code FillApplier} and a
     * {@code Fillable}. When a {@code FillApplier} is set on a
     * {@code Fillable}, this variable is automatically updated.
     */
    private F fable;
    private Fill fill;
    
    private final InvalidationListener fillInvalidated;
    private final InvalidationListener propInvalidated;
    private final ChangeListener<Shape> shapeChanged;
    
    private boolean fillInvalid;
    private boolean cachesInvalid;
    private boolean applying;
    private List<FillApplier<?>> attached;
    
    protected List<BackgroundFill> bgFillsCache;
    protected List<BorderStroke> bdStrokesCache;
    
    
    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Constructor for all {@code FillApplierBase} instances.
     *
     * @param fillable - the {@code Fillable} for which this
     *        {@code FillApplier} should attach to.
     */
    protected FillApplierBase(F fillable) {
        if (fillable == null) { throw new IllegalArgumentException("cannot pass null for fillable"); }
        
        fable = fillable;
        
        //
        fillInvalidated = obv -> updateFill();
        
        //
        propInvalidated = obv -> {
            if (applying || cachesInvalid) { return; }
            
            // fill is invalid if any of its 5 properties change
            fillInvalid = true;
            
            Object val = ((ObservableObjectValue<?>) obv).get();
            if (val.getClass() == Background.class || val.getClass() == Border.class) {
                // caches are invalid if fable's background or border change
                cachesInvalid = true;
            }
        };
        
        // handles shape's stroke property
        shapeChanged = (obv, o, n) -> {
            if (o != null) { o.strokeProperty().removeListener(propInvalidated); }
            if (n != null) { n.strokeProperty().addListener(propInvalidated); }
        };
        
        fable.fillProperty().addListener(fillInvalidated);
        
        fillInvalid = cachesInvalid = true;
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API (from FillApplier)                                           *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        if (fable == null) { return; }
        
        if (attached != null) {
            attached.clear();
            attached = null;
        }
        
        removePropListeners();
        
        fable.fillProperty().removeListener(fillInvalidated);
        
        fable = null;
        fill = null;
    }
    
    /** {@inheritDoc} */
    @Override
    public final F getFillable() { return fable; }
    
    /** {@inheritDoc} */
    @Override
    public final boolean isApplying() { return applying; }
    
    /** {@inheritDoc} */
    @Override
    public final boolean attach(FillApplier<? extends Fillable> applier) {
        if (attached == null) { attached = new LinkedList<FillApplier<? extends Fillable>>(); }
        
        // do a simple circular reference check, applying should cover the rest
        if (attached.stream().anyMatch(a -> a == applier)) { return false; }
        
        return attached.add(applier);
    }
    
    /** {@inheritDoc} */
    @Override
    public final void interpolateAndApply(double frac) {
        if (applying) { return; }
        
        // signal that fill applier is changing text, shape, etc. fill
        applying = true;
        
        if (fill != null && fill.hasFillSpans()) {
            
            // update fill and caches if invalid (cachesInvalid -> fillInvalid)
            if (fillInvalid) { updateInvalid(); }
            
            interpolateAndApplyImpl(frac);
        }
        
        if (attached != null) {
            attached.forEach(applier -> applier.interpolateAndApply(frac));
        }
        
        // signal that fill applier is no longer changing text, etc. fill
        applying = false;
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/
    
    protected void interpolateAndApplyImpl(double frac) {
        // apply the text fill span
        if (fill.hasTextFillSpan()) { applyTextFillSpan(fill.getTextFillSpan(), frac); }
        
        // apply the shape and stroke fill spans
        final Shape shape = fable.getShape();
        if (shape != null) {
            if (fill.hasShapeFillSpan()) { applyShapeFillSpan(shape, fill.getShapeFillSpan(), frac); }
            if (fill.hasStrokeFillSpan()) { applyStrokeFillSpan(shape, fill.getStrokeFillSpan(), frac); }
        }
        
        // apply the background fill span(s)
        if (fill.hasBgFillSpans() && bgFillsCache != null) {
            applyBgFillSpans(bgFillsCache, fill.getBgFillSpans(), frac);
        }
        
        // apply the border fill span(s)
        if (fill.hasBorderFillSpans() && bdStrokesCache != null) {
            applyBorderFillSpans(bdStrokesCache, fill.getBorderFillSpans(), frac);
        }
    }
    
    /**
     * Applies the {@code Fillable} instance's text {@code FillSpan}, if
     * any, to the {@code Fillable} instance.
     * 
     * @param span - the {@code Fillable} instance's text {@code FillSpan}
     * @param frac - the interpolate fraction
     */
    protected void applyTextFillSpan(FillSpan span, double frac) {
        fable.setTextFill(span.interpolate(frac));
    }
    
    
    /**
     * Applies the {@code Fillable} instance's shape {@code FillSpan}, if
     * any, to the {@code Fillable} instance's {@code shape} property.
     * 
     * @param shape - the {@code Fillable} instance's shape
     * @param span - the {@code Fillable} instance's shape {@code FillSpan}
     * @param frac - the interpolate fraction
     */
    protected void applyShapeFillSpan(Shape shape, FillSpan span, double frac) {
        shape.setFill(span.interpolate(frac));
    }
    
    
    /**
     * Applies the {@code Fillable} instance's stroke {@code FillSpan}, if
     * any, to the {@code Fillable} instance's {@code shape} property.
     * 
     * @param shape - the {@code Fillable} instance's shape
     * @param span - the {@code Fillable} instance's stroke {@code FillSpan}
     * @param frac - the interpolate fraction
     */
    protected void applyStrokeFillSpan(Shape shape, FillSpan span, double frac) {
        shape.setStroke(span.interpolate(frac));
    }
    
    
    /**
     * Applies the {@code Fillable} instance's list of {@code FillSpan}, if
     * any, to the {@code Fillable}.
     * 
     * @param bgSpans - the {@code Fillable} instance's list of background
     *        {@code FillSpan} instances
     * @param frac - the interpolate fraction
     */
    protected void applyBgFillSpans(List<BackgroundFill> bgFills, List<FillSpan> bgSpans, double frac) {
        fable.setBackground(
            new Background(interpolateBgFillSpans(bgFills, bgSpans, frac), fable.getBackground().getImages()));
    }
    
    /**
     * Applies the {@code Fillable} instance's list of {@code BorderFillSpan},
     * if any, to the {@code Fillable}.
     * 
     * @param bdSpans - the {@code Fillable} instance's list of
     *        {@code BorderFillSpan} instances
     * @param frac - the interpolate fraction
     */
    protected void applyBorderFillSpans(List<BorderStroke> bdStrokes, List<BorderFillSpan> bdSpans, double frac) {
        fable
            .setBorder(new Border(interpolateBorderFillSpans(bdStrokes, bdSpans, frac), fable.getBorder().getImages()));
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Private Implementation                                                  *
     *                                                                         *
     **************************************************************************/
    
    /** Adds 'propInvalidated' listeners to fillable's properties. */
    private void addPropListeners(boolean hasSpecial) {
        if (hasSpecial) {
            fable.textFillProperty().addListener(propInvalidated);
            fable.shapeProperty().addListener(propInvalidated);
            // handle fable's shape property's stroke property
            fable.shapeProperty().addListener(shapeChanged);
        }
        
        fable.borderProperty().addListener(propInvalidated);
        fable.backgroundProperty().addListener(propInvalidated);
    }
    
    /** Removes 'propInvalidated' listeners from fillable's properties. */
    private void removePropListeners() {
        fable.textFillProperty().removeListener(propInvalidated);
        fable.shapeProperty().removeListener(propInvalidated);
        fable.backgroundProperty().removeListener(propInvalidated);
        fable.borderProperty().removeListener(propInvalidated);
        
        // handle fable's shape property's stroke property
        fable.shapeProperty().removeListener(shapeChanged);
        final Shape shape = fable.getShape();
        if (shape != null) {
            shape.strokeProperty().removeListener(propInvalidated);
        }
    }
    
    /** */
    private void updateFill() {
        final Fill newFill = fable.getFill();
        
        if (newFill != null && newFill.equals(fill)) { return; }
        
        // some aspect of fill has changed, need to reset property listeners
        removePropListeners();
        if (newFill == null) {
            fill = null;
            return;
        }
        
        final boolean hasSpecial = FillHelper.fillHasSpecial(newFill);
        fill = hasSpecial ? FillHelper.replaceSpecialsInFill(newFill, fable) : newFill;
        
        addPropListeners(hasSpecial);
    }
    
    /** Updates 'fill' and caches if they are invalid. */
    private void updateInvalid() {
        if (cachesInvalid) { updateCaches(); }
        
        final Fill f = fable.getFill();
        fill = FillHelper.fillHasSpecial(f) ? FillHelper.replaceSpecialsInFill(f, fable) : fill;
        
        fillInvalid = false;
    }
    
    /**
     * Updates this {@code FillApplier} instance's cached background fills and
     * border strokes.
     */
    private void updateCaches() {
        final Background bg = fable.getBackground();
        bgFillsCache = (bg != null) ? bg.getFills() : null;
        
        final Border bd = fable.getBorder();
        bdStrokesCache = (bd != null) ? bd.getStrokes() : null;
        
        cachesInvalid = false;
    }
    
    
    /***************************************************************************
     *                                                                         *
     * SubApplier Implementation                                               *
     *                                                                         *
     **************************************************************************/
    
    /** */
    protected interface SubApplier {
        /**
         * 
         * @return
         */
        Fillable getFillable();
        
        /**
         * 
         * @return
         */
        Fill getFill();
        
        /**
         * 
         * @param frac
         */
        void interpolateAndApply(final double frac);
    }
    
    /** */
    protected abstract class SubApplierBase implements SubApplier {
        @Override
        public Fillable getFillable() { return FillApplierBase.this.fable; }
        @Override
        public final Fill getFill() { return FillApplierBase.this.fill; }
    }
    
    
    /**
     * Applies the {@code Fillable} instance's text {@code FillSpan}, if
     * any, to the {@code Fillable} instance.
     */
    protected class TextFillApplier extends SubApplierBase {
        
        /**
         * Applies the {@code Fillable} instance's text {@code FillSpan}, if
         * any, to the {@code Fillable} instance.
         * 
         * @param frac - the interpolate fraction
         */
        @Override
        public void interpolateAndApply(final double frac) {
            if (fable.getTextFill() != null) {
                fable.setTextFill(fill.getTextFillSpan().interpolate(frac));
            }
        }
        
    } // class TextFillApplier
    
    
    /**
     *
     */
    protected class ShapeFillApplier extends SubApplierBase {
        
        @Override
        public void interpolateAndApply(double frac) {
            final Shape shape = fable.getShape();
            if (shape != null) {
                shape.setFill(fill.getShapeFillSpan().interpolate(frac));
            }
        }
        
    } // class ShapeFillApplier
    
    
    /**
     *
     */
    protected class StrokeFillApplier extends SubApplierBase {
        
        @Override
        public void interpolateAndApply(double frac) {
            final Shape shape = fable.getShape();
            if (shape != null && shape.getStroke() != null) {
                shape.setStroke(fill.getStrokeFillSpan().interpolate(frac));
            }
        }
        
    } // class StrokeFillApplier
    
    
    /**
     * 
     */
    protected class BgFillApplier extends SubApplierBase {
        
        @Override
        public void interpolateAndApply(double frac) {
            if (bgFillsCache != null) {
                List<BackgroundFill> interped = interpolateBgFillSpans(bgFillsCache, fill.getBgFillSpans(), frac);
                
                fable.setBackground(new Background(interped, fable.getBackground().getImages()));
            }
        }
        
        /** Helper used by 'applyBgFillSpans'. */
        private List<BackgroundFill>
        interpolateBgFillSpans(List<BackgroundFill> bgFills, List<FillSpan> bgSpans, double frac)
        {
            final List<BackgroundFill> newBgFills = new ArrayList<>(bgFills);
            final int n = Math.min(bgSpans.size(), bgFills.size());
            
            for (int i = 0; i < n; i++) {
                // traverse over bg fills and spans in reverse
                final int fillIdx = (newBgFills.size() - 1) - i;
                final int spanIdx = (bgSpans.size() - 1) - i;
                
                // set old fill to new fill created from old fill and bg span
                newBgFills.set(fillIdx, createBgFill(newBgFills.get(fillIdx), bgSpans.get(spanIdx), frac));
            }
            
            return newBgFills;
        }
        
        
        /** Helper used by 'interpolateBgFillSpans'. */
        private BackgroundFill createBgFill(BackgroundFill oldFill, FillSpan bgSpan, double frac) {
            return new BackgroundFill(bgSpan.interpolate(frac), oldFill.getRadii(), oldFill.getInsets());
        }
    } // class BgFillApplier
    
    
    /**
     * 
     */
    protected class BorderFillApplier extends SubApplierBase {
        
        @Override
        public void interpolateAndApply(double frac) {
            if (bdStrokesCache != null) {
                List<BorderStroke> interped =
                interpolateBorderFillSpans(bdStrokesCache, fill.getBorderFillSpans(), frac);
                
                fable.setBorder(new Border(interped, fable.getBorder().getImages()));
            }
        }
        
        /** Helper used by 'applyBorderFillSpans'. */
        private List<BorderStroke>
        interpolateBorderFillSpans(List<BorderStroke> bdStrokes, List<BorderFillSpan> bdSpans, double frac)
        {
            final List<BorderStroke> newBdStrokes = new ArrayList<>(bdStrokes);
            final int n = Math.min(bdSpans.size(), bdStrokes.size());
            
            for (int i = 0; i < n; i++) {
                // traverse over strokes and spans in reverse
                final int strokeIdx = (newBdStrokes.size() - 1) - i;
                final int spanIdx = (bdSpans.size() - 1) - i;
                
                // set old stroke to new stroke created from old stroke and bd span
                newBdStrokes.set(strokeIdx,
                    createBorderStroke(newBdStrokes.get(strokeIdx), bdSpans.get(spanIdx), frac));
            }
            
            return newBdStrokes;
        }
        
        
        /** Helper used by 'interpolateBorderFillSpans'. */
        private BorderStroke createBorderStroke(BorderStroke os, BorderFillSpan span, double frac) {
            if (span.isUniform()) {
                return new BorderStroke(span.getTop().interpolate(frac), os.getTopStyle(), os.getRadii(),
                    os.getWidths(), os.getInsets());
            }
            
            // 'is' stands for 'interpolated span'
            Color[] is = span.interpolate(frac);
            return new BorderStroke(is[0], is[1], is[2], is[3], os.getTopStyle(), os.getRightStyle(),
                os.getBottomStyle(), os.getLeftStyle(), os.getRadii(), os.getWidths(), os.getInsets());
        }
        
    } // class BorderFillApplier
    
    
}

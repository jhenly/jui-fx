package com.jhenly.juifx.control.applier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jhenly.juifx.control.Fillable;

import impl.com.jhenly.juifx.fill.BorderFillSpan;
import impl.com.jhenly.juifx.fill.Fill;
import impl.com.jhenly.juifx.fill.FillHelper;
import impl.com.jhenly.juifx.fill.FillSpan;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.css.StyleOrigin;
import javafx.css.StyleableObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;


public abstract class FillApplierBase<F extends Fillable> implements FillApplier<F> {
    
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
    private boolean applying;
    private AttachedList attached;
    private Map<Class<? extends SubApplier>, SubApplier> subAppliers;
    
    protected Paint textCache;
    protected Paint shapeCache;
    protected Paint strokeCache;
    protected Background bgCache;
    protected Border bdCache;
    
    
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
    public FillApplierBase(F fillable) {
        if (fillable == null) { throw new IllegalArgumentException("the 'fillable' parameter cannot be null"); }
        
        fable = fillable;
        
        //
        fillInvalidated = obv -> updateFill();
        
        //
        propInvalidated = obv -> {
            if (applying || fillInvalid) { return; }
            
            // fill is invalid if any of its 5 properties change
            fillInvalid = true;
        };
        
        // handles shape's stroke property
        shapeChanged = (obv, o, n) -> {
            if (o != null) { o.strokeProperty().removeListener(propInvalidated); }
            if (n != null) { n.strokeProperty().addListener(propInvalidated); }
        };
        
        fable.fillProperty().addListener(fillInvalidated);
        
        fillInvalid = true;
        updateFill();
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API (from FillApplier)                                           *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public final F getFillable() { return fable; }
    
    /** {@inheritDoc} */
    @Override
    public final boolean isApplying() { return applying; }
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        // remove any attached appliers
        detachAll();
        
        if (fable == null) { return; }
        
        removePropListeners();
        
        fable.fillProperty().removeListener(fillInvalidated);
        
        fable = null;
        fill = null;
    }
    
    /** {@inheritDoc} */
    @Override
    public final void interpolateAndApply(double frac) {
        if (applying) { return; }
        
        // signal that fill applier is changing some fill
        applying = true;
        
        if (fill != null && fill.hasFillSpans()) {
            
            // update fill and caches if invalid (cachesInvalid -> fillInvalid)
            if (fillInvalid) { updateInvalid(); }
            
            if (subAppliers != null) {
                subAppliers.values().forEach(applier -> applier.interpolateAndApply(frac));
            }
            
        }
        
        if (attached != null) {
            attached.interpolateAndApply(frac);
        }
        
        // signal that fill applier is no longer changing some fill
        applying = false;
    }
    
    @Override
    public void resetFillable() {
        applying = true;
        
        if (textCache != null) { fable.setTextFill(textCache); }
        
        final Shape shape = fable.getShape();
        if (shape != null) {
            shape.setFill(shapeCache);
            shape.setStroke(strokeCache);
        }
        
        if (bgCache != null) { fable.setBackground(bgCache); }
        if (bdCache != null) { fable.setBorder(bdCache); }
        
        applying = false;
    }
    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/
    
    protected void updateSubAppliers() {
        registerTextFillApplier();
        registerShapeFillApplier();
        registerStrokeFillApplier();
        registerBgFillApplier();
        registerBorderFillApplier();
    }
    
    private void registerTextFillApplier() {
        if (fill.hasTextFillSpan()) {
            if (!subApplierIsRegistered(TextFillApplier.class)) {
                registerSubApplier(new TextFillApplier());
            }
        } else {
            unregisterSubApplier(TextFillApplier.class);
        }
    }
    
    private void registerShapeFillApplier() {
        if (fill.hasShapeFillSpan()) {
            if (!subApplierIsRegistered(ShapeFillApplier.class)) {
                registerSubApplier(new ShapeFillApplier());
            }
        } else {
            unregisterSubApplier(ShapeFillApplier.class);
        }
    }
    
    private void registerStrokeFillApplier() {
        if (fill.hasStrokeFillSpan()) {
            if (!subApplierIsRegistered(StrokeFillApplier.class)) {
                registerSubApplier(new StrokeFillApplier());
            }
        } else {
            unregisterSubApplier(StrokeFillApplier.class);
        }
    }
    
    private void registerBgFillApplier() {
        if (fill.hasBgFillSpans()) {
            if (!subApplierIsRegistered(BgFillApplier.class)) {
                registerSubApplier(new BgFillApplier());
            }
        } else {
            unregisterSubApplier(BgFillApplier.class);
        }
    }
    
    private void registerBorderFillApplier() {
        if (fill.hasBorderFillSpans()) {
            if (!subApplierIsRegistered(BorderFillApplier.class)) {
                registerSubApplier(new BorderFillApplier());
            }
        } else {
            unregisterSubApplier(BorderFillApplier.class);
        }
    }
    
    /***************************************************************************
     *                                                                         *
     * Attach/Detach API                                                       *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public final boolean attach(FillApplier<?> applier) {
        if (applier == null) { return false; }
        
        if (attached == null) {
            attached = new AttachedList(applier);
            return true;
        }
        
        // does a simple circular reference check, applying should cover the
        // rest
        return attached.add(applier);
    }
    
    /** {@inheritDoc} */
    @Override
    public final boolean attachAll(Collection<FillApplier<?>> appliers) {
        if (appliers == null) { return false; }
        
        final int preAttachSize = (attached == null) ? 0 : attached.size();
        
        appliers.forEach(a -> attach(a));
        
        return (attached == null) ? false : attached.size() != preAttachSize;
    }
    
    /** {@inheritDoc} */
    @Override
    public final boolean detach(FillApplier<?> applier) {
        if (applier == null || attached == null) { return false; }
        
        final boolean removed = attached.remove(applier);
        if (removed && attached.size() <= 0) { attached = null; }
        
        return removed;
    }
    
    /** {@inheritDoc} */
    @Override
    public final boolean detachAll(Collection<FillApplier<?>> appliers) {
        if (appliers == null || attached == null) { return false; }
        
        final int preDetachSize = attached.size();
        
        appliers.forEach(a -> detach(a));
        
        return attached.size() != preDetachSize;
    }
    
    /** {@inheritDoc} */
    @Override
    public final void detachAll() {
        if (attached == null) { return; }
        attached.clear();
        attached = null;
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Attached List Implementation                                            *
     *                                                                         *
     **************************************************************************/
    
    /** Helper doubly linked list node class. */
    private static class AttachedList {
        private AttachedApplier head;
        private int size;
        
        AttachedList(FillApplier<?> first) {
            head = new AttachedApplier(first);
            size = 1;
        }
        
        boolean add(FillApplier<?> toAdd) {
            // create and set head if it was removed
            if (head == null) {
                head = new AttachedApplier(toAdd);
                size += 1;
                return true;
            }
            
            AttachedApplier cur = head;
            
            // circular reference check
            while (cur.next != null) {
                if (cur.attached.equals(toAdd)) { return false; }
                cur = cur.next;
            }
            // need to check the last node in the list
            if (cur.attached.equals(toAdd)) { return false; }
            
            // create and add fill applier to the list
            AttachedApplier tmp = new AttachedApplier(toAdd);
            cur.next = tmp;
            tmp.prev = cur;
            
            size += 1;
            return true;
        }
        
        boolean remove(FillApplier<?> toRemove) {
            if (head == null) { return false; }
            
            AttachedApplier cur = head;
            
            while (cur != null) {
                if (cur.attached.equals(toRemove)) {
                    if (cur == head) {
                        if (cur.next != null) {
                            head = cur.next;
                        } else {
                            head = null;
                        }
                    }
                    
                    cur.dispose();
                    size -= 1;
                    return true;
                }
                
                cur = cur.next;
            }
            
            return false;
        }
        
        void clear() {
            if (head == null) { return; }
            
            AttachedApplier cur = head;
            
            while (cur != null) {
                AttachedApplier tmp = cur.next;
                cur.dispose();
                cur = tmp;
            }
            
            head = null;
            size = 0;
        }
        
        int size() { return size; }
        
        void interpolateAndApply(double frac) {
            if (head == null) { return; }
            
            AttachedApplier cur = head;
            
            while (cur != null) {
                cur.interpolateAndApply(frac);
                cur = cur.next;
            }
        }
        
        /** Helper doubly linked list node class. */
        private static class AttachedApplier {
            private AttachedApplier next, prev;
            private FillApplier<?> attached;
            private final ChangeListener<Skin<?>> skinChange;
            private final ChangeListener<FillApplier<?>> applierChange;
            
            AttachedApplier(FillApplier<?> toAttach) {
                attached = toAttach;
                
                skinChange = (obv, o, n) -> dispose();
                applierChange = (obv, o, n) -> dispose();
                
                attached.getFillable().skinProperty().addListener(skinChange);
                attached.getFillable().getFillableSkin().fillApplierProperty().addListener(applierChange);
            }
            
            void dispose() {
                if (attached != null) {
                    attached.getFillable().skinProperty().removeListener(skinChange);
                    attached.getFillable().getFillableSkin().fillApplierProperty().removeListener(applierChange);
                }
                
                if (prev != null) {
                    prev.next = next;
                }
                
                if (next != null) {
                    next.prev = prev;
                }
                
                prev = null;
                next = null;
                
                attached = null;
            }
            
            void interpolateAndApply(double frac) {
                if (attached == null) { return; }
                
                attached.interpolateAndApply(frac);
            }
            
        } // class AttachedApplier
        
        
    } // class AttachedList
    
    
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
            clearSubAppliers();
            fill = null;
            return;
        }
        
        if (!newFill.hasFillSpans()) {
            fill = newFill;
            clearSubAppliers();
            return;
        }
        
        final boolean hasSpecial = FillHelper.fillHasSpecial(newFill);
        fill = hasSpecial ? FillHelper.replaceSpecialsInFill(newFill, fable) : newFill;
        addPropListeners(hasSpecial);
        
        updateSubAppliers();
    }
    
    private void clearSubAppliers() {
        if (subAppliers != null) { subAppliers.clear(); }
    }
    
    /** Updates 'fill' and caches if they are invalid. */
    private void updateInvalid() {
        updateCaches();
        
        final Fill f = fable.getFill();
        fill = FillHelper.fillHasSpecial(f) ? FillHelper.replaceSpecialsInFill(f, fable) : fill;
        
        fillInvalid = false;
    }
    
    /** Updates this {@code FillApplier} instance's cached properties. */
    private void updateCaches() {
        textCache = fable.getTextFill();
        
        final Shape shape = fable.getShape();
        shapeCache = (shape == null) ? null : shape.getFill();
        strokeCache = (shape == null) ? null : shape.getStroke();
        
        bgCache = fable.getBackground();
        bdCache = fable.getBorder();
    }
    
    
    /***************************************************************************
     *                                                                         *
     * SubApplier Implementation                                               *
     *                                                                         *
     **************************************************************************/
    
    /**
     * 
     * @param subApplier - the {@code SubApplier} to register
     */
    protected final void registerSubApplier(SubApplier subApplier) {
        if (subAppliers == null) { subAppliers = new LinkedHashMap<>(5); }
        
        subAppliers.put(subApplier.getClass(), subApplier);
    }
    
    /**
     * 
     * @param subApplierClass - the class of the {@code SubApplier} to unregister
     */
    protected final void unregisterSubApplier(Class<? extends SubApplier> subApplierClass) {
        if (subAppliers == null) { return; }
        
        subAppliers.remove(subApplierClass);
    }
    
    /**
     * 
     * @param subApplierClass - the class of the {@code SubApplier} to see if
     *        it's registered
     */
    protected final boolean subApplierIsRegistered(Class<? extends SubApplier> subApplierClass) {
        if (subAppliers == null) { return false; }
        
        return subAppliers.containsKey(subApplierClass);
    }
    
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
     * Applies the {@code Fillable} instance's shape {@code FillSpan}, if
     * any, to the {@code Fillable} instance's {@code shape} property.
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
     * Applies the {@code Fillable} instance's stroke {@code FillSpan}, if
     * any, to the {@code Fillable} instance's {@code shape} property.
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
     * Applies the {@code Fillable} instance's list of {@code FillSpan}, if
     * any, to the {@code Fillable}.
     */
    protected class BgFillApplier extends SubApplierBase {
        
        @Override
        public void interpolateAndApply(double frac) {
            if (bgCache != null) {
                List<BackgroundFill> interped = interpolateBgFillSpans(bgCache.getFills(), fill.getBgFillSpans(), frac);
                
                StyleableObjectProperty<Background> fableBg
                    = (StyleableObjectProperty<Background>) fable.backgroundProperty();
                
                fableBg.applyStyle(StyleOrigin.AUTHOR, new Background(interped, fable.getBackground().getImages()));
            }
        }
        
        /** Helper used by 'applyBgFillSpans'. */
        private List<BackgroundFill> interpolateBgFillSpans(List<BackgroundFill> bgFills, List<FillSpan> bgSpans,
            double frac) {
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
     * Applies the {@code Fillable} instance's list of {@code BorderFillSpan},
     * if any, to the {@code Fillable}.
     */
    protected class BorderFillApplier extends SubApplierBase {
        
        @Override
        public void interpolateAndApply(double frac) {
            if (bdCache != null) {
                List<BorderStroke> interped
                    = interpolateBorderFillSpans(bdCache.getStrokes(), fill.getBorderFillSpans(), frac);
                
                fable.setBorder(new Border(interped, fable.getBorder().getImages()));
            }
        }
        
        /** Helper used by 'applyBorderFillSpans'. */
        private List<BorderStroke> interpolateBorderFillSpans(List<BorderStroke> bdStrokes,
            List<BorderFillSpan> bdSpans, double frac) {
            final List<BorderStroke> newBdStrokes = new ArrayList<>(bdStrokes);
            final int n = Math.min(bdSpans.size(), bdStrokes.size());
            
            for (int i = 0; i < n; i++) {
                // traverse over strokes and spans in reverse
                final int strokeIdx = (newBdStrokes.size() - 1) - i;
                final int spanIdx = (bdSpans.size() - 1) - i;
                
                // set old stroke to new stroke created from old stroke and bd
                // span
                newBdStrokes.set(strokeIdx,
                    createBorderStroke(newBdStrokes.get(strokeIdx), bdSpans.get(spanIdx), frac));
            }
            
            return newBdStrokes;
        }
        
        
        /** Helper used by 'interpolateBorderFillSpans'. */
        private BorderStroke createBorderStroke(BorderStroke os, BorderFillSpan span, double frac) {
            // span.isUniform implies top == right == bottom == left
            if (span.isUniform()) {
                return new BorderStroke(span.getTop().interpolate(frac), os.getTopStyle(), os.getRadii(),
                    os.getWidths(), os.getInsets());
            }
            
            // 'is' stands for 'interpolated span'
            Paint[] is = span.interpolate(frac);
            return new BorderStroke(is[0], is[1], is[2], is[3], os.getTopStyle(), os.getRightStyle(),
                os.getBottomStyle(), os.getLeftStyle(), os.getRadii(), os.getWidths(), os.getInsets());
        }
        
    } // class BorderFillApplier
    
}

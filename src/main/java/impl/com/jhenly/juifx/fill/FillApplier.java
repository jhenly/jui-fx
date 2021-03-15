package impl.com.jhenly.juifx.fill;

import java.util.ArrayList;
import java.util.List;

import com.jhenly.juifx.control.Fillable;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;


/**
 * Handles the applying of a {@link Fillable} instance's {@link Fill}.
 * 
 * @param <F> - the {@code Fillable} type
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public class FillApplier<F extends Fillable> {
    private final InvalidationListener fillInvalidated;
    private final InvalidationListener propInvalidated;
    private final ChangeListener<Shape> shapeChanged;
    
    private F fable;
    private Fill fill;
    private boolean fillInvalid;
    private boolean cachesInvalid;
    
    protected boolean applying;
    protected List<BackgroundFill> bgFillsCache;
    protected List<BorderStroke> bdStrokesCache;
    
    /**
     * Constructs a {@code FillApplier} which applies a specified
     * {@link Fillable} instances {@link Fill} via
     * {@link #interpolateAndApply(double)}.
     * 
     * @param fillable - the {@code Fillable} instance to apply the
     *        {@code Fill} of
     */
    public FillApplier(final F fillable) {
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
    
    /** Add 'propInvalidated' listeners to fillable's properties. */
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
        removePropListeners();
        
        final Fill newFill = fable.getFill();
        if (newFill == null) {
            fill = null;
            return;
        }
        
        if (newFill.equals(fill)) { return; }
        
        final boolean hasSpecial = newFill.hasSpecial();
        fill = hasSpecial ? Fill.replaceSpecialsInFill(newFill, fable) : newFill;
        
        addPropListeners(hasSpecial);
    }
    
    /**
     * Gets whether or not this {@code FillApplier} instance is currently
     * applying the {@code Fillable} instance's {@code Fill}.
     * 
     * @return {@code true} if this fill applier is currently applying the
     *         fillable's fill, otherwise {@code false}
     */
    public final boolean isApplying() { return applying; }
    
    /**
     * Gets the {@link Fillable} to which this {@code FillApplier} is assigned.
     * 
     * A {@code FillApplier} must be created for one and only one Fillable.
     * This value will only ever go from a non-null to null value when the
     * {@code FillApplier} is removed from the {@code Fillable}, and only as a
     * consequence of a call to {@link #dispose()}.
     * <p>
     * The caller who constructs a Fillable must also construct a
     * {@code FillApplier} and properly establish the relationship between the
     * {@code Fillable} and its {@code FillApplier}.
     *
     * @return a non-null {@code Fillable}, or {@code null} if disposed
     */
    public final F getFillable() { return fable; }
    
    /***
     * Allows a {@code FillApplier} to implement any logic necessary to clean
     * itself up after the {@code FillApplier} is no longer needed.
     * <p>
     * The method {@link #getFillable()} should return {@code null} following a
     * call to dispose. Calling dispose twice has no effect.
     */
    public void dispose() {
        if (fable == null) { return; }
        
        fable = null;
        fill = null;
        bgFillsCache = null;
        bdStrokesCache = null;
    }
    
    /** Updates 'fill' and caches if they are invalid. */
    protected void updateInvalid() {
        if (cachesInvalid) { updateCaches(); }
        
        final Fill f = fable.getFill();
        fill = f.hasSpecial() ? Fill.replaceSpecialsInFill(f, fable) : fill;
        
        fillInvalid = false;
    }
    
    /**
     * Updates this {@code FillApplier} instance's cached background fills and
     * border strokes.
     */
    protected void updateCaches() {
        final Background bg = fable.getBackground();
        bgFillsCache = (bg != null) ? bg.getFills() : null;
        
        final Border bd = fable.getBorder();
        bdStrokesCache = (bd != null) ? bd.getStrokes() : null;
        
        cachesInvalid = false;
    }
    
    /**
     * Applies the {@code Fillable} instance's text, shape, stroke, background
     * and border {@code FillSpan} and {@code BorderFillSpan} instances to the
     * {@code Fillable}.
     * 
     * @param frac - the interpolate fraction
     */
    public void interpolateAndApply(double frac) {
        if (fill == null || !fill.hasFillSpans()) { return; }
        
        // signal that fill applier is changing text, shape, etc. fill
        applying = true;
        
        // update fill and caches if invalid (cachesInvalid -> fillInvalid)
        if (fillInvalid) { updateInvalid(); }
        
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
        
        // signal that fill applier is no longer changing text, etc. fill
        applying = false;
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
            newBdStrokes.set(strokeIdx, createBorderStroke(newBdStrokes.get(strokeIdx), bdSpans.get(spanIdx), frac));
        }
        
        return newBdStrokes;
    }
    
    /** Helper used by 'interpolateBorderFillSpans'. */
    private BorderStroke createBorderStroke(BorderStroke os, BorderFillSpan span, double frac) {
        if (span.isUniform()) {
            return new BorderStroke(span.getTop().interpolate(frac), os.getTopStyle(), os.getRadii(), os.getWidths(),
                os.getInsets());
        }
        
        // 'is' stands for 'interpolated span'
        Paint[] is = span.interpolate(frac);
        return new BorderStroke(is[0], is[1], is[2], is[3], os.getTopStyle(), os.getRightStyle(), os.getBottomStyle(),
            os.getLeftStyle(), os.getRadii(), os.getWidths(), os.getInsets());
    }
    
}

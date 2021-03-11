package impl.com.jhenly.juifx.fill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import impl.com.jhenly.juifx.fill.FillConverter.BorderFillSpanHalf;
import impl.com.jhenly.juifx.fill.FillConverter.FillSpanHalf;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;


/**
 * A partial implementation of {@link CssMetaData} for {@link Fill} properties
 * which includes the fill sub-properties: from, to.
 * 
 * @param <T> - the type of {@code Styleable}
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public abstract class FillCssMetaData<T extends Styleable> extends CssMetaData<T, Fill> {
    
    /**
     * Constructs a {@code FillCssMetaData} object from the specified property
     * and initial {@link Fill}.
     * <p>
     * The property name is concatenated with "-text-from", "-text-to",
     * "-shape-from", "-shape-to", "-stroke-from", "-stroke-to", "-bg-from",
     * "-bg-to", "-border-from" and "-border-to" to create the sub-properties.
     * <p>
     * For example,<pre>
     * new FillCssMetaData&lt;FillButton&gt;("-fill", Fill.getDefault());</pre>
     * <p>
     * will create a {@code FillCssMetaData} for {@code "-fill"} with
     * sub-properties: "-fill-text-from", "-fill-text-to", "-fill-shape-from"
     * "-fill-shape-to", "-fill-stroke-from", "-fill-stroke-to",
     * "-fill-bg-from", "-fill-bg-to", "-fill-border-from" and
     * "-fill-border-to".
     * 
     * @param property - the property name
     * @param initial - the initial {@code Fill}
     */
    public FillCssMetaData(String property, Fill initial) {
        super(property, FillConverter.getInstance(), initial, false, createSubProperties(property, initial));
    }
    
    static final String TEXT_FROM = "-text-from";
    static final String TEXT_TO = "-text-to";
    static final String SHAPE_FROM = "-shape-from";
    static final String SHAPE_TO = "-shape-to";
    static final String STROKE_FROM = "-stroke-from";
    static final String STROKE_TO = "-stroke-to";
    static final String BG_FROM = "-bg-from";
    static final String BG_TO = "-bg-to";
    static final String BORDER_FROM = "-border-from";
    static final String BORDER_TO = "-border-to";
    
    /** Creates all of the styleable {@code Fill} sub-properties. */
    private static <T extends Styleable> List<CssMetaData<? extends Styleable, ?>>
    createSubProperties(String property, Fill initial)
    {
        // list to hold fill's sub-properties, i.e. -fill-bg-from, etc.
        final List<CssMetaData<? extends Styleable, ?>> subProperties = new ArrayList<>();
        
        // grab fill halves from any supplied initial fill, or use the default
        final InitialFill def =
        (initial == null || initial.equals(Fill.getDefault())) ? getDefault() : new InitialFill(initial);
        
        // --- Text From ---
        final CssMetaData<T, FillSpanHalf> textFrom = new CssMetaData<T, FillSpanHalf>(property.concat(TEXT_FROM),
            FillConverter.StringConverter.getInstance(), def.textFrom, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            @Override
            public StyleableProperty<FillSpanHalf> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(textFrom);
        
        // --- Text To ---
        final CssMetaData<T, FillSpanHalf> textTo = new CssMetaData<T, FillSpanHalf>(property.concat(TEXT_TO),
            FillConverter.StringConverter.getInstance(), def.textTo, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            @Override
            public StyleableProperty<FillSpanHalf> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(textTo);
        
        // --- Shape From ---
        final CssMetaData<T, FillSpanHalf> shapeFrom = new CssMetaData<T, FillSpanHalf>(property.concat(SHAPE_FROM),
            FillConverter.StringConverter.getInstance(), def.shapeFrom, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            @Override
            public StyleableProperty<FillSpanHalf> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(shapeFrom);
        
        // --- Shape To ---
        final CssMetaData<T, FillSpanHalf> shapeTo = new CssMetaData<T, FillSpanHalf>(property.concat(SHAPE_TO),
            FillConverter.StringConverter.getInstance(), def.shapeTo, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            @Override
            public StyleableProperty<FillSpanHalf> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(shapeTo);
        
        // --- Stroke From ---
        final CssMetaData<T, FillSpanHalf> strokeFrom = new CssMetaData<T, FillSpanHalf>(property.concat(STROKE_FROM),
            FillConverter.StringConverter.getInstance(), def.strokeFrom, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            
            @Override
            public StyleableProperty<FillSpanHalf> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(strokeFrom);
        
        // --- Stroke To ---
        final CssMetaData<T, FillSpanHalf> strokeTo = new CssMetaData<T, FillSpanHalf>(property.concat(STROKE_TO),
            FillConverter.StringConverter.getInstance(), def.strokeTo, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            
            @Override
            public StyleableProperty<FillSpanHalf> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(strokeTo);
        
        // --- Background From ---
        final CssMetaData<T, FillSpanHalf[]> bgFrom = new CssMetaData<T, FillSpanHalf[]>(property.concat(BG_FROM),
            FillConverter.StringSequenceConverter.getInstance(), def.bgFrom, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            @Override
            public StyleableProperty<FillSpanHalf[]> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(bgFrom);
        
        // --- Background To ---
        final CssMetaData<T, FillSpanHalf[]> bgTo = new CssMetaData<T, FillSpanHalf[]>(property.concat(BG_TO),
            FillConverter.StringSequenceConverter.getInstance(), def.bgTo, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            @Override
            public StyleableProperty<FillSpanHalf[]> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(bgTo);
        
        // --- Border From ---
        final CssMetaData<T, BorderFillSpanHalf[]> borderFrom = new CssMetaData<T, BorderFillSpanHalf[]>(
            property.concat(BORDER_FROM), FillConverter.BorderStringSequenceConverter.getInstance(), def.bdFrom, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            @Override
            public StyleableProperty<BorderFillSpanHalf[]> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(borderFrom);
        
        // --- Border To ---
        final CssMetaData<T, BorderFillSpanHalf[]> borderTo = new CssMetaData<T, BorderFillSpanHalf[]>(
            property.concat(BORDER_TO), FillConverter.BorderStringSequenceConverter.getInstance(), def.bdTo, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            @Override
            public StyleableProperty<BorderFillSpanHalf[]> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(borderTo);
        
        return Collections.unmodifiableList(subProperties);
    }
    
    // lazy, thread safe instantiation
    private static class Holder {
        static final InitialFill DEFAULT = new InitialFill(Fill.getDefault());
        
        private Holder() { throw new IllegalStateException("a Holder class should not be instantiated"); }
    } // class Holder
    private static InitialFill getDefault() { return Holder.DEFAULT; }
    
    /** Used to hold an initial fill's colors to pass as default parameters. */
    private static class InitialFill {
        FillSpanHalf textFrom, textTo;
        FillSpanHalf shapeFrom, shapeTo;
        FillSpanHalf strokeFrom, strokeTo;
        FillSpanHalf[] bgFrom, bgTo;
        BorderFillSpanHalf[] bdFrom, bdTo;
        
        InitialFill(Fill init) {
            setText(init.getTextFillSpan());
            setShape(init.getShapeFillSpan());
            setStroke(init.getStrokeFillSpan());
            setBackground(init.getBgFillSpans());
            setBorder(init.getBorderFillSpans());
        }
        
        private void setText(FillSpan span) {
            if (span == null) { return; }
            textFrom = FillSpanHalf.getFrom(span);
            textTo = FillSpanHalf.getTo(span);
        }
        
        private void setShape(FillSpan span) {
            if (span == null) { return; }
            shapeFrom = FillSpanHalf.getFrom(span);
            shapeTo = FillSpanHalf.getTo(span);
        }
        
        private void setStroke(FillSpan span) {
            if (span == null) { return; }
            strokeFrom = FillSpanHalf.getFrom(span);
            strokeTo = FillSpanHalf.getTo(span);
        }
        
        private void setBackground(List<FillSpan> spans) {
            if (spans == null) { return; }
            
            final int n = spans.size();
            bgFrom = new FillSpanHalf[n];
            bgTo = new FillSpanHalf[n];
            
            for (int i = 0; i < n; i++) {
                final FillSpan span = spans.get(i);
                bgFrom[i] = FillSpanHalf.getFrom(span);
                bgTo[i] = FillSpanHalf.getTo(span);
            }
        }
        
        private void setBorder(List<BorderFillSpan> spans) {
            if (spans == null) { return; }
            
            final int n = spans.size();
            bdFrom = new BorderFillSpanHalf[n];
            bdTo = new BorderFillSpanHalf[n];
            
            for (int i = 0; i < n; i++) {
                final BorderFillSpan span = spans.get(i);
                bdFrom[i] = BorderFillSpanHalf.getFrom(span);
                bdTo[i] = BorderFillSpanHalf.getTo(span);
            }
        }
        
    } // class InitialFill
    
}

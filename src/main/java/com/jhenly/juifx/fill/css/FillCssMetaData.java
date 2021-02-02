package com.jhenly.juifx.fill.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jhenly.juifx.fill.Fill;
import com.jhenly.juifx.fill.FillConverter;
import com.jhenly.juifx.fill.Fill.FillType;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.FontConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * A partial implementation of {@link CssMetaData} for {@link Fill} properties
 * which includes the fill sub-properties: type, from, to.
 * @param <T> The type of Styleable
 * @since JuiFX 1.0
 */
public abstract class FillCssMetaData<T extends Styleable> extends CssMetaData<T, Fill> {
    
    /**
     * Constructs a FontCSSMetaData object from the specified property and initial Font.
     * {@literal The property name is concatenated with "-weight", "-style",
     * "-family" and "-size" to create the sub-properties.}
     * For example,
     * {@code new FontCssMetaData<Text>("-fx-font", Font.getDefault());}
     * {@literal will create a CssMetaData for "-fx-font" with sub-properties:
     * "-fx-font-weight", "-fx-font-style", "-fx-font-family" and "-fx-font-size"}
     * @param property the property name
     * @param initial the initial font
     */
    public FillCssMetaData(String property, Fill initial) {
        super(property, FillConverter.getInstance(), initial, true, createSubProperties(property, initial));
    }
    
    private static <T extends Styleable> List<CssMetaData<? extends Styleable, ?>> createSubProperties(String property,
        Fill initial) {
        
        final List<CssMetaData<T, ?>> subProperties = new ArrayList<>();
        
        final Fill defaultFill = initial != null ? initial : Fill.getDefault();
        
        final CssMetaData<T, Color[]> BG_FROM = new CssMetaData<T, Color[]>(property.concat("bg-from"),
            FillConverter.StringSequenceConverter.getInstance(), new Color[] {}, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            
            @Override
            public StyleableProperty<Color[]> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(BG_FROM);
        
        final CssMetaData<T, Color[]> BG_TO = new CssMetaData<T, Color[]>(property.concat("bg-to"),
            FillConverter.StringSequenceConverter.getInstance(), new Color[] {}, true)
        {
            @Override
            public boolean isSettable(T styleable) { return false; }
            
            @Override
            public StyleableProperty<Color[]> getStyleableProperty(T styleable) { return null; }
        };
        subProperties.add(BG_TO);
        
        final CssMetaData<S, String> FAMILY = new CssMetaData<S, String>(property.concat("-from"),
            StringConverter.getInstance(), defaultFont.getFamily(), true)
        {
            @Override
            public boolean isSettable(S styleable) {
                return false;
            }
            
            @Override
            public StyleableProperty<String> getStyleableProperty(S styleable) {
                return null;
            }
        };
        subProperties.add(FAMILY);
        
        final CssMetaData<S, Number> SIZE = new CssMetaData<S, Number>(property.concat("-to"),
            SizeConverter.getInstance(), defaultFont.getSize(), true)
        {
            @Override
            public boolean isSettable(S styleable) {
                return false;
            }
            
            @Override
            public StyleableProperty<Number> getStyleableProperty(S styleable) {
                return null;
            }
        };
        subProperties.add(SIZE);
        
        final CssMetaData<S, FontPosture> STYLE = new CssMetaData<S, FontPosture>(property.concat("-style"),
            FontConverter.FontStyleConverter.getInstance(), FontPosture.REGULAR, true)
        {
            @Override
            public boolean isSettable(S styleable) {
                return false;
            }
            
            @Override
            public StyleableProperty<FontPosture> getStyleableProperty(S styleable) {
                return null;
            }
        };
        subProperties.add(STYLE);
        
        final CssMetaData<S, FontWeight> WEIGHT = new CssMetaData<S, FontWeight>(property.concat("-weight"),
            FontConverter.FontWeightConverter.getInstance(), FontWeight.NORMAL, true)
        {
            @Override
            public boolean isSettable(S styleable) {
                return false;
            }
            
            @Override
            public StyleableProperty<FontWeight> getStyleableProperty(S styleable) {
                return null;
            }
        };
        subProperties.add(WEIGHT);
        
        return Collections.<CssMetaData<? extends Styleable, ?>>unmodifiableList(subProperties);
    }
    
    private static class InitialFillHolder {
        private FillType type;
        private Color[] bgFillFrom;
        private Color[] bgFillTo;
        private Color textFillFrom;
        private Color textFillTo;
        private Color shapeFillFrom;
        private Color shapeFillTo;
        
        InitialFillHolder(Fill initial) {
            
        }
        
    }
    
}

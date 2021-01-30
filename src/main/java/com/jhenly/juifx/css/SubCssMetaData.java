package com.jhenly.juifx.css;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.StyleableProperty;
import javafx.scene.Node;

/**
 * A CssMetaData which is used for sub-properties, such as -fill-from, etc.
 */
public class SubCssMetaData<T> extends CssMetaData<Node, T> {
    
    /**
     * Construct a {@code SubCssMetaData} with the given parameters.
     * @param property - the CSS property
     * @param converter - the {@code StyleConverter} used to convert the CSS
     *        parsed value to a Java object
     * @param initialValue - the initial or default value of the corresponding
     *        {@code StyleableProperty}
     */
    public SubCssMetaData(String property, StyleConverter<?, T> converter, T initialValue) {
        super(property, converter, initialValue);
    }
    
    /**
     * Construct a {@code SubCssMetaData} with the given parameters and no
     * initial value.
     * @param property - the CSS property
     * @param converter - the {@code StyleConverter} used to convert the CSS
     *        parsed value to a Java object.
     */
    public SubCssMetaData(String property, StyleConverter<?, T> converter) {
        super(property, converter);
    }
    
    /**
     * This overridden method always returns {@code false}.
     * <p>
     * {@inheritDoc}
     * @return {@code false}
     */
    @Override
    public boolean isSettable(Node node) { return false; }
    
    /**
     * This overridden method always returns {@code null}.
     * <p>
     * {@inheritDoc}
     * @return {@code null}
     */
    @Override
    public StyleableProperty<T> getStyleableProperty(Node node) { return null; }
    
}

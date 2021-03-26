/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
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
 * the License.
 */
package com.jhenly.juifx.control;

import java.util.List;

import com.jhenly.juifx.control.skin.FillableSkin;

import impl.com.jhenly.juifx.fill.BorderFillSpan;
import impl.com.jhenly.juifx.fill.Fill;
import impl.com.jhenly.juifx.fill.FillCssMetaData;
import impl.com.jhenly.juifx.fill.FillSpan;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.DurationConverter;
import javafx.scene.control.Skinnable;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.util.Duration;


/**
 * The {@code Fillable} interface.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public interface Fillable extends Skinnable, Styleable {
    
    /**************************************************************************
     *                                                                        *
     * Fillable Related Properties                                            *
     *                                                                        *
     *************************************************************************/
    
    /* --- Fill Enabled --- */
    /**
     * The fill enabled property, which indicates if this {@code Fillable}
     * instance's fill transition should run or not.
     * 
     * @defaultValue {@code true}
     */
    BooleanProperty fillEnabledProperty();
    /**
     * Sets whether this {@code Fillable} instance's fill transition should run
     * or not.
     * @param value - whether the fill transition should run or not
     */
    default void setFillEnabled(boolean value) { fillEnabledProperty().set(value); }
    /**
     * Gets whether this {@code Fillable} instance's fill transition should run
     * or not.
     * @return {@code true} if the fill transition should run, otherwise
     *         {@code false}
     */
    default boolean isFillEnabled() { return DEFAULT_FILL_ENABLED; }
    /** The default {@code fillEnabled} value. */
    boolean DEFAULT_FILL_ENABLED = true;
    
    /* --- Fill Duration --- */
    /**
     * The fill transition duration property, which specifies how long it will
     * take the fill transition to go from start to end.
     * <p>
     * The duration must be greater than or equal to {@link Duration#ZERO}.
     * 
     * @defaultValue {@code Duration.millis(200.0)}
     */
    ObjectProperty<Duration> fillDurationProperty();
    /**
     * Sets the fill transition duration to the specified millisecond value.
     * <p>
     * The specified millisecond value must be greater than or equal to
     * {@code 0}.
     * @param millis - the duration of the fill transition, in milliseconds
     */
    default void setFillDuration(double millis) { fillDurationProperty().set(Duration.millis(millis)); }
    /**
     * Sets the fill transition duration to a specified duration.
     * <p>
     * The specified duration must be greater than or equal to
     * {@link Duration#ZERO}.
     * @param value - the duration of the fill transition
     */
    default void setFillDuration(Duration value) { fillDurationProperty().set(value); }
    default Duration getFillDuration() { return DEFAULT_DURATION; }
    
    /** The default {@code Fill} duration. */
    Duration DEFAULT_DURATION = Duration.millis(200.0);
    
    
    /* --- Fill --- */
    /**
     * This {@code Fillable} instance's {@link Fill} property, which
     * encapsulates the {@link FillSpan} instance(s) used by the fill transition.
     * 
     * @defaultValue {@link #DEFAULT_FILL}
     */
    ObjectProperty<Fill> fillProperty();
    /** 
     * Sets this {@code Fillable} instance's {@link Fill} to the specified
     * fill.
     * @param value - the new {@code Fill} to set
     */
    default void setFill(Fill value) { fillProperty().set(value); }
    /** 
     * Gets this {@code Fillable} instance's {@link Fill}.
     * @return this {@code Fillable} instance's {@code Fill}
     */
    Fill getFill();
    
    /** 
     * The {@linkplain Fill#getDefault() default Fill}, which contains no {@link FillSpan} or
     * {@link BorderFillSpan} instances.
     */
    Fill DEFAULT_FILL = Fill.getDefault();
    
    
    /* --- Fill On Focus --- */
    /**
     * The fill on focus property, which indicates if this {@code Fillable}
     * instance's fill transition should run when it gains focus.
     * 
     * @defaultValue {@code false}
     */
    BooleanProperty fillOnFocusProperty();
    /**
     * Sets whether this {@code Fillable} instance's fill transition should run
     * when it gains focus.
     * @param value - whether the fill transition should run when it gains
     *        focus or not
     */
    default void setFillOnFocus(boolean value) { fillOnFocusProperty().set(value); }
    /**
     * Gets whether this {@code Fillable} instance's fill transition should run
     * when it gains focus.
     * @return {@code true} if the fill transition should run when it gains
     *         focus, otherwise {@code false}
     */
    default boolean isFillOnFocus() { return DEFAULT_FILL_ON_FOCUS; }
    /** The default {@code fillOnFocus} value. */
    boolean DEFAULT_FILL_ON_FOCUS = false;
    
    /* --- Fillable Skin --- */
    /**
     * The fillable skin property, which encapsulates this {@code Fillable}
     * instance's underlying {@link FillableSkin} instance.
     */
    <F extends FillableSkin<? extends Fillable>> ReadOnlyObjectProperty<F> fillableSkinProperty();
    /**
     * Gets the {@link FillableSkin} attached to this {@code Fillable}
     * instance.
     * @return the {@code FillableSkin} attached to this {@code Fillable}
     *         instance
     */
    @SuppressWarnings("unchecked")
    default <F extends FillableSkin<? extends Fillable>> F getFillableSkin() { return (F) getSkin(); }
    
    
    /**************************************************************************
     *                                                                        *
     * Region Related Properties                                              *
     *                                                                        *
     *************************************************************************/
    
    /* --- Background --- */
    /**
     * The background of the Region, which is made up of zero or more BackgroundFills, and
     * zero or more BackgroundImages. It is possible for a Background to be empty, where it
     * has neither fills nor images, and is semantically equivalent to null.
     * @since JavaFX 8.0
     */
    ObjectProperty<Background> backgroundProperty();
    Background getBackground();
    void setBackground(Background value);
    
    
    /* --- Border --- */
    /**
     * The border of the Region, which is made up of zero or more BorderStrokes, and
     * zero or more BorderImages. It is possible for a Border to be empty, where it
     * has neither strokes nor images, and is semantically equivalent to null.
     * @since JavaFX 8.0
     */
    ObjectProperty<Border> borderProperty();
    Border getBorder();
    void setBorder(Border value);
    
    
    /* --- Shape --- */
    /**
     * When specified, the {@code Shape} will cause the region to be
     * rendered as the specified shape rather than as a rounded rectangle.
     * When null, the Region is rendered as a rounded rectangle. When rendered
     * as a Shape, any Background is used to fill the shape, although any
     * background insets are ignored as are background radii. Any BorderStrokes
     * defined are used for stroking the shape. Any BorderImages are ignored.
     *
     * @defaultValue null
     * @since JavaFX 8.0
     */
    ObjectProperty<Shape> shapeProperty();
    Shape getShape();
    
    
    /**************************************************************************
     *                                                                        *
     * Labeled Related Properties                                             *
     *                                                                        *
     *************************************************************************/
    
    /* --- Text --- */
    /**
     * The {@link Paint} used to fill the text.
     *
     * @defaultValue {@code Color.BLACK}
     */
    ObjectProperty<Paint> textFillProperty();
    Paint getTextFill();
    void setTextFill(Paint value);
    
    
    /**************************************************************************
     *                                                                        *
     * Methods                                                                *
     *                                                                        *
     *************************************************************************/
    
    
    /**************************************************************************
     *                                                                        *
     * Stylesheet Handling                                                    *
     *                                                                        *
     *************************************************************************/
    
    /** 
     * CSS ':fill-disabled' signals that the {@code Fillable} instance's fill
     * transition is disabled. 
     */
    PseudoClass FILL_DISABLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("fill-disabled"); //$NON-NLS-1$
    
    
    /** Instantiates the {@link Fillable} interface's CSS styleable properties. */
    static class StyleableProperties {
        
        /* --- Fill Enabled --- */
        public static final CssMetaData<Fillable, Boolean> FILL_ENABLED
            = new CssMetaData<Fillable, Boolean>("-fill-enabled", BooleanConverter.getInstance(), true)
            {
                @Override
                public boolean isSettable(Fillable fillable) {
                    return !fillable.fillEnabledProperty().isBound();
                }
                @SuppressWarnings("unchecked")
                @Override
                public StyleableProperty<Boolean> getStyleableProperty(Fillable fillable) {
                    return (StyleableProperty<Boolean>) fillable.fillEnabledProperty();
                }
            };
        
        /* --- Fill Duration --- */
        public static final CssMetaData<Fillable, Duration> FILL_DURATION
            = new CssMetaData<Fillable, Duration>("-fill-duration", DurationConverter.getInstance(), DEFAULT_DURATION)
            {
                @Override
                public boolean isSettable(Fillable fillable) {
                    return fillable.getFillDuration() == DEFAULT_DURATION || !fillable.fillDurationProperty().isBound();
                }
                @SuppressWarnings("unchecked")
                @Override
                public StyleableProperty<Duration> getStyleableProperty(Fillable fillable) {
                    return (StyleableProperty<Duration>) fillable.fillDurationProperty();
                }
            };
        
        /* --- Fill --- */
        public static final FillCssMetaData<Fillable> FILL = new FillCssMetaData<Fillable>("-fill", Fill.getDefault())
        {
            @Override
            public boolean isSettable(Fillable fillable) {
                return fillable.getFill() == DEFAULT_FILL || !fillable.fillProperty().isBound();
            }
            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Fill> getStyleableProperty(Fillable fillable) {
                return (StyleableProperty<Fill>) fillable.fillProperty();
            }
        };
        
        /* --- Fill On Focus --- */
        public static final CssMetaData<Fillable, Boolean> FILL_ON_FOCUS
            = new CssMetaData<Fillable, Boolean>("-fill-on-focus", BooleanConverter.getInstance(), false)
            {
                @Override
                public boolean isSettable(Fillable fillable) {
                    return !fillable.fillOnFocusProperty().isBound();
                }
                @SuppressWarnings("unchecked")
                @Override
                public StyleableProperty<Boolean> getStyleableProperty(Fillable fillable) {
                    return (StyleableProperty<Boolean>) fillable.fillOnFocusProperty();
                }
            };
        
        /* --- Styleables --- */
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES
            = List.of(FILL_ENABLED, FILL_DURATION, FILL, FILL_ON_FOCUS);
    }
    
    /**
     * Gets the {@code CssMetaData} associated with this interface.
     *
     * @return the {@code CssMetaData} associated with this interface 
     */
    static List<CssMetaData<? extends Styleable, ?>> getFillableCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
    
    /**
    * Gets an unmodifiable list of this {@code Fillable} control's CSS
    * styleable properties.
    *
    * @return unmodifiable list of this control's CSS styleable properties
    */
    default List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() { return getFillableCssMetaData(); }
    
}

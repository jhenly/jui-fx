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
package com.jhenly.juifx.control;

import java.util.List;

import com.jhenly.juifx.control.skin.FillableSkin;

import impl.com.jhenly.juifx.fill.Fill;
import impl.com.jhenly.juifx.fill.FillApplier;
import impl.com.jhenly.juifx.fill.FillCssMetaData;
import impl.com.jhenly.juifx.fill.FillProperty;
import impl.com.jhenly.juifx.fill.FillSpan;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
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
     * Properties                                                             *
     *                                                                        *
     *************************************************************************/
    
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
    default boolean isFillEnabled() { return true; }
    
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
    
    /**
     * This {@code Fillable} instance's {@link Fill} property, which
     * encapsulates the {@link FillSpan} instance(s) used by the fill transition.
     */
    FillProperty fillProperty();
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
    
//    /**
//     * The {@link FillType} property, which specifies what type of fill(s) the
//     * {@code Fillable} contains.
//     * 
//     * @defaultValue {@link FillType#BG}
//     */
//    ObjectProperty<FillType> fillTypeProperty();
//    /**
//     * Sets this {@code Fillable} instance's {@link FillType}.
//     * @param value - the new {@code FillType}
//     */
//    default void setFillType(FillType value) { fillTypeProperty().set(value); }
//    /**
//     * Gets this {@code Fillable} instance's {@link FillType}.
//     * @return this {@code Fillable} instance's {@code FillType}
//     */
//    default FillType getFillType() { return fillTypeProperty().get(); }
    
    
    /**
     * The background of the Region, which is made up of zero or more BackgroundFills, and
     * zero or more BackgroundImages. It is possible for a Background to be empty, where it
     * has neither fills nor images, and is semantically equivalent to null.
     * @since JavaFX 8.0
     */
    ObjectProperty<Background> backgroundProperty();
    Background getBackground();
    void setBackground(Background value);
    
    /**
     * The border of the Region, which is made up of zero or more BorderStrokes, and
     * zero or more BorderImages. It is possible for a Border to be empty, where it
     * has neither strokes nor images, and is semantically equivalent to null.
     * @since JavaFX 8.0
     */
    ObjectProperty<Border> borderProperty();
    Border getBorder();
    void setBorder(Border value);
    
    /**
     * The {@link Paint} used to fill the text.
     *
     * @defaultValue {@code Color.BLACK}
     */
    ObjectProperty<Paint> textFillProperty();
    Paint getTextFill();
    void setTextFill(Paint value);
    
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
     * Methods                                                                *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets this {@code Fillable} instance's {@code FillApplier} instance.
     * @return this fillable's fill applier
     */
    FillApplier<? extends Fillable> getFillApplier();
    
    /**
     * Gets the {@link FillableSkin} attached to this {@code Fillable}
     * instance.
     * @return the {@code FillableSkin} attached to this {@code Fillable}
     *         instance
     */
    default FillableSkin<? extends Fillable> getFillableSkin() { return (FillableSkin<?>) getSkin(); }
    
    
    /**************************************************************************
     *                                                                        *
     * Stylesheet Handling                                                    *
     *                                                                        *
     *************************************************************************/
    
    /** Instantiates the {@link Fillable} interface's CSS styleable properties. */
    static class StyleableProperties {
        
        public static final FillCssMetaData<Fillable> FILL = new FillCssMetaData<Fillable>("-fill", Fill.getDefault())
        {
            @Override
            public boolean isSettable(Fillable fillable) {
                return fillable.getFill() == null || !fillable.fillProperty().isBound();
            }
            @Override
            public StyleableProperty<Fill> getStyleableProperty(Fillable fillable) {
                return (StyleableProperty<Fill>) fillable.fillProperty();
            }
        };
        
        /* --- Fill Enabled --- */
        public static final CssMetaData<Fillable, Boolean> FILL_ENABLED =
        new CssMetaData<Fillable, Boolean>("-fill-enabled", BooleanConverter.getInstance(), true)
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
        public static final CssMetaData<Fillable, Duration> FILL_DURATION =
        new CssMetaData<Fillable, Duration>("-fill-duration", DurationConverter.getInstance(), DEFAULT_DURATION)
        {
            @Override
            public boolean isSettable(Fillable fillable) {
                return fillable.getFillDuration() == null || !fillable.fillDurationProperty().isBound();
            }
            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Duration> getStyleableProperty(Fillable fillable) {
                return (StyleableProperty<Duration>) fillable.fillDurationProperty();
            }
        };
        
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES =
        List.of(FILL, FILL_ENABLED, FILL_DURATION);
    }
    
    /**
     * Gets the {@code CssMetaData} associated with this interface..
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

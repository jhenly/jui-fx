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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jhenly.juifx.control.skin.FillButtonSkin;
import com.jhenly.juifx.control.skin.FillableSkin;

import impl.com.jhenly.juifx.fill.Fill;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.util.Duration;


/**
 * A button control that fills over time to a specified color on mouse enter
 * and changes back to a specified color on mouse exit.
 * <p>
 * This class extends {@code SelectButton} and does not override any of its
 * public API.
 * 
 * @author Jonathan Henly
 * @see SelectableButton
 * @since JuiFX 1.0
 */
public class FillButton extends SelectableButton implements Fillable {
    
    /**
     * The default {@link #fillEnabledProperty() fill enabled} value.
     */
    public static final boolean DEFAULT_FILL_ENABLED = true;
    
    /**
     * The default {@link #fillDurationProperty() fill duration} value.
     */
    public static final Duration DEFAULT_FILL_DURATION = Duration.millis(100.0);
    
    
    // new Fill(FillSpan.of(Color.valueOf("#f4f4f6"), Color.valueOf("#70787d")));
    
    
    /***************************************************************************
     *                                                                         *
     * Private Members                                                         *
     *                                                                         *
     **************************************************************************/
    
    
    /***************************************************************************
     *                                                                         *
     * Constructor(s)                                                          *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Creates a {@code FillButton} instance, with an empty string for its
     * text.
     */
    public FillButton() {
        super();
        initialize();
    }
    
    /**
     * Creates a {@code FillButton} instance with the specified text.
     * @param text - the button's text
     */
    public FillButton(String text) {
        super(text);
        initialize();
    }
    
    /**
     * Creates a {@code FillButton} instance with the specified text and
     * graphic.
     * @param text - the button's text
     * @param graphic - the button's graphic
     */
    public FillButton(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }
    
    /**
     * Method used by all constructors.
     * <p>
     * Adds the default style class.
     */
    private void initialize() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
    
    /* --- fillEnabled --- */
    /** {@inheritDoc} */
    @Override
    public final BooleanProperty fillEnabledProperty() {
        if (fillEnabled == null) {
            fillEnabled = new StyleableBooleanProperty(DEFAULT_FILL_ENABLED)
            {
                @Override
                protected void invalidated() { pseudoClassStateChanged(FILL_DISABLED_PSEUDO_CLASS, get()); }
                @Override
                public Object getBean() { return FillButton.this; }
                @Override
                public String getName() { return "fillEnabled"; } //$NON-NLS-1$
                @Override
                public CssMetaData<Fillable, Boolean> getCssMetaData() {
                    return Fillable.StyleableProperties.FILL_ENABLED;
                }
                
            };
        }
        return fillEnabled;
    }
    @Override
    public final void setFillEnabled(boolean enabled) { fillEnabledProperty().set(enabled); }
    @Override
    public final boolean isFillEnabled() { return fillEnabled == null ? DEFAULT_FILL_ENABLED : fillEnabled.get(); }
    private BooleanProperty fillEnabled;
    
    /* --- fillDuration --- */
    @Override
    public final ObjectProperty<Duration> fillDurationProperty() {
        if (fillDuration == null) {
            fillDuration = new StyleableObjectProperty<Duration>(Fillable.DEFAULT_DURATION)
            {
                private Duration oldValue = get();
                
                @Override
                protected void invalidated() {
                    Duration value = get();
                    
                    if (value == null || value.lessThan(Duration.ZERO)) {
                        if (isBound()) { unbind(); }
                        set(oldValue);
                        String msg = (value == null) ? "value cannot be null" : "value cannot be negative";
                        throw new IllegalArgumentException(msg);
                    }
                    oldValue = value;
                }
                @Override
                public Object getBean() { return FillButton.this; }
                @Override
                public String getName() { return "fillDuration"; } //$NON-NLS-1$
                @Override
                public CssMetaData<Fillable, Duration> getCssMetaData() {
                    return Fillable.StyleableProperties.FILL_DURATION;
                }
                
            };
        }
        return fillDuration;
    }
    @Override
    public final void setFillDuration(Duration duration) { fillDurationProperty().set(duration); }
    @Override
    public final Duration getFillDuration() {
        return fillDuration == null ? Fillable.DEFAULT_DURATION : fillDuration.get();
    }
    private ObjectProperty<Duration> fillDuration;
    
    /* --- fill --- */
    @Override
    public final ObjectProperty<Fill> fillProperty() {
        if (fill == null) {
            fill = new StyleableObjectProperty<Fill>(Fill.getDefault())
            {
                @Override
                public String getName() { return "fill"; } // $NON-NLS-1$
                @Override
                public Object getBean() { return FillButton.this; }
                
                @Override
                public CssMetaData<Fillable, Fill> getCssMetaData() { return Fillable.StyleableProperties.FILL; }
                
            };
        }
        return fill;
    }
    @Override
    public final void setFill(Fill value) { fillProperty().set(value); }
    @Override
    public final Fill getFill() { return fill == null ? null : fill.get(); }
    private ObjectProperty<Fill> fill;
    
    /* --- fill on focus --- */
    @Override
    public BooleanProperty fillOnFocusProperty() {
        if (fillOnFocus == null) {
            fillOnFocus = new StyleableBooleanProperty(false)
            {
                @Override
                public String getName() { return "fillOnFocus"; } // $NON-NLS-1$
                @Override
                public Object getBean() { return FillButton.this; }
                
                @Override
                public CssMetaData<Fillable, Boolean> getCssMetaData() {
                    return Fillable.StyleableProperties.FILL_ON_FOCUS;
                }
                
            };
        }
        return fillOnFocus;
    }
    @Override
    public final void setFillOnFocus(boolean value) { fillOnFocusProperty().set(value); }
    @Override
    public final boolean isFillOnFocus() { return fillOnFocus == null ? false : fillOnFocus.get(); }
    private BooleanProperty fillOnFocus;
    
    /* --- fillable skin --- */
    /**
     * The fillable skin property, which encapsulates this {@code Fillable}
     * instance's underlying {@link FillableSkin} instance.
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <F extends FillableSkin<? extends Fillable>> ReadOnlyObjectProperty<F> fillableSkinProperty() {
        return (ReadOnlyObjectProperty<F>) fillableSkin.getReadOnlyProperty();
    }
    /**
     * Gets the {@link FillableSkin} attached to this {@code Fillable}
     * instance.
     * @return the {@code FillableSkin} attached to this {@code Fillable}
     *         instance
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <F extends FillableSkin<? extends Fillable>> F getFillableSkin() {
        return (F) fillableSkin.getValue();
    }
    
    @SuppressWarnings("unchecked") ReadOnlyObjectWrapper<? extends FillableSkin<? extends Fillable>> fillableSkin =
    new ReadOnlyObjectWrapper<>(this, "fillableSkin", (FillButtonSkin<FillButton>) getSkin());
    
    
    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new FillButtonSkin<FillButton>(this);
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/
    
    /** This control's CSS style class. */
    private static final String DEFAULT_STYLE_CLASS = "fill-button"; //$NON-NLS-1$
    
    private static String stylesheet;
    
    /** {@inheritDoc} */
    @Override
    public String getUserAgentStylesheet() {
        if (stylesheet == null) {
            stylesheet = FillButton.class.getResource("fillbutton.css").toExternalForm(); // $NON-NLS-1$
        }
        return stylesheet;
    }
    
    /** Instantiates all of the this control's CSS styleable properties. */
    private static class StyleableProperties {
        
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Button.getClassCssMetaData());
            styleables.addAll(Fillable.getFillableCssMetaData());
            
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        
    } // class StyleableProperties
    
    /**
     * Gets the {@code CssMetaData} associated with this class, which may include
     * the {@code CssMetaData} of its super classes.
     *
     * @return the {@code CssMetaData} associated with this class
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
    
    /**
     * Gets an unmodifiable list of this {@code FillButton} control's CSS
     * styleable properties.
     *
     * @return unmodifiable list of this control's CSS styleable properties
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() { return getClassCssMetaData(); }
    
}

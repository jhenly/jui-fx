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

import impl.com.jhenly.juifx.fill.Fill;
import impl.com.jhenly.juifx.fill.FillApplier;
import impl.com.jhenly.juifx.fill.FillHelper;
import impl.com.jhenly.juifx.fill.FillProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
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
    
    private InvalidationListener fillInvalidated;
    private final FillApplier<FillButton> applier;
    
    
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
        applier = new FillApplier<FillButton>(this);
        initialize();
    }
    
    /**
     * Creates a {@code FillButton} instance with the specified text.
     * @param text - the button's text
     */
    public FillButton(String text) {
        super(text);
        applier = new FillApplier<FillButton>(this);
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
        applier = new FillApplier<FillButton>(this);
        initialize();
    }
    
    /**
     * Method used by all constructors.
     * <p>
     * Adds the default style class.
     */
    private void initialize() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        
        fillInvalidated = (obv) -> {
            if (fill != null && !applier.isApplying()) { fill.fireValueChanged(); }
        };
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
    public FillProperty fillProperty() {
        if (fill == null) {
            fill = new FillProperty(Fill.getDefault(), FillButton.this)
            {
                private Fill special;
                
                @Override
                void fireValueChanged() {
                    applier.updateCaches();
                    
                    if (special != null) {
                        Fill newRepl = FillHelper.replaceSpecialsInFill(special, FillButton.this);
                        if (!newRepl.equals(get())) {
                            super.set(newRepl);
                        }
                    }
                    
                }
                
                @Override
                public void set(Fill val) {
                    if (val == null) {
                        special = null;
                        super.set(val);
                        return;
                    }
                    
                    if (special != null && special.equals(val)) { return; }
                    
                    final Fill old = get();
                    if (old != null && old.equals(val)) { return; }
                    special = null;
                    
                    if (!FillHelper.fillHasSpecial(val)) {
                        super.set(val);
                        return;
                    }
                    
                    special = val;
                    super.set(FillHelper.replaceSpecialsInFill(val, FillButton.this));
                }
                
                @Override
                public Object getBean() { return FillButton.this; }
                
                @Override
                protected Fillable getFillable() { return FillButton.this; }
            };
        }
        return fill;
    }
    @Override
    public final void setFill(Fill value) { fillProperty().set(value); }
    @Override
    public final Fill getFill() { return fill == null ? null : fill.get(); }
    private FillProperty fill;
    
    
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
    
    /** {@inheritDoc} */
    @Override
    public FillApplier<?> getFillApplier() { return applier; }
    
    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/
    
    /** This control's CSS style class. */
    private static final String DEFAULT_STYLE_CLASS = "fill-button"; //$NON-NLS-1$
    
    /** 
     * CSS ':fill-disabled' signals that the button's fill transition is
     * disabled. 
     */
    private static final PseudoClass FILL_DISABLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("fill-disabled"); //$NON-NLS-1$
    
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
//        
//        /* --- Fill Enabled --- */
//        private static final String FILL_ENABLED_CSS = "-fill-enabled"; //$NON-NLS-1$
//        private static final CssMetaData<FillButton, Boolean> FILL_ENABLED =
//        new CssMetaData<FillButton, Boolean>(FILL_ENABLED_CSS, BooleanConverter.getInstance(), DEFAULT_FILL_ENABLED)
//        {
//            @Override
//            public boolean isSettable(FillButton fb) { return fb.fillEnabled == null || !fb.fillEnabled.isBound(); }
//            @Override
//            public StyleableProperty<Boolean> getStyleableProperty(FillButton fb) {
//                return (StyleableProperty<Boolean>) fb.fillEnabledProperty();
//            }
//        };
//        
//        /* --- Fill Duration --- */
//        private static final String FILL_DURATION_CSS = "-fill-duration"; //$NON-NLS-1$
//        private static final CssMetaData<FillButton, Duration> FILL_DURATION =
//        new CssMetaData<FillButton, Duration>(FILL_DURATION_CSS, DurationConverter.getInstance(), DEFAULT_FILL_DURATION)
//        {
//            @Override
//            public boolean isSettable(FillButton fb) { return fb.fillDuration == null || !fb.fillDuration.isBound(); }
//            @Override
//            public StyleableProperty<Duration> getStyleableProperty(FillButton fb) {
//                return (StyleableProperty<Duration>) fb.fillDurationProperty();
//            }
//        };
        
        
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Button.getClassCssMetaData());
//            styleables.add(FILL_ENABLED);
//            styleables.add(FILL_DURATION);
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

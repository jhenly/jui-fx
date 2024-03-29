/** Copyright (c) 2021, JuiFX All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of JuiFX, any associated website,
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL JUIFX BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package com.jhenly.juifx.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jhenly.juifx.control.skin.TopPromptTextFieldSkin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;


/**
 * This control behaves exactly like a {@link TextField}, however this control's
 * text prompt moves above the text field when focused.
 * <p>
 * This control also allows for a default stub and default text to be displayed
 * below the textfield.
 *
 * @author Jonathan Henly
 * @since JuiFX 0.1
 */
public class TopPromptTextField extends Control {
    /**
     * The default value for {@link #promptTransitionDurationProperty()
     * prompTransitionDuration}.
     */
    public static final double DEFAULT_PROMPT_TRANSLATE_DURATION = 200.0;
    
    /**
     * The default value for {@link #promptTranslateXProperty() promptOffsetX}.
     */
    public static final double DEFAULT_PROMPT_TRANSLATE_X = -4.0;
    
    /**
     * The default value for {@link #promptTranslateYProperty() promptOffsetY}.
     */
    public static final double DEFAULT_PROMPT_TRANSLATE_Y = 0.0;
    
    /**
     * The default value for {@link #promptScaleXProperty() promptScaleX}.
     */
    public static final double DEFAULT_PROMPT_SCALE_X = 0.9;
    
    /**
     * The default value for {@link #promptScaleYProperty() promptScaleY}.
     */
    public static final double DEFAULT_PROMPT_SCALE_Y = 0.9;
    
    /**
     * The default value for {@link #useDefaultValueProperty() useDefaultText}.
     */
    public static final boolean DEFAULT_USE_DEFAULT_VALUE = false;
    
    /**
     * The default value for {@link #usePromptAsPromptTextProperty() usePromptAsPromptText}.
     */
    public static final boolean DEFAULT_USE_PROMPT_AS_PROMPT_TEXT = false;
    
    
    /***************************************************************************
     *                                                                         *
     * Constructor(s)                                                          *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Creates a {@code TopPromptTextField} instance with no prompt, default
     * stub or default text.
     */
    public TopPromptTextField() {
        super();
        
        initialize();
    }
    
    /**
     * Creates a {@code TopPromptTextField} instance with a prompt, default
     * stub and default text.
     *
     * @param prompt - the prompt node
     * @param stub - the default stub node
     * @param text - the default text node
     */
    public TopPromptTextField(Labeled prompt, Labeled stub, Labeled text) {
        super();
        
        initialize();
        setPrompt(prompt);
        setDefaultStub(stub);
        setDefaultValue(text);
    }
    
    /** Helper method used by constructors. */
    private void initialize() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
    
    /* --- Prompt --- */
    private ObjectProperty<Labeled> prompt = new SimpleObjectProperty<>(TopPromptTextField.this, "prompt"); //$NON-NLS-1$ ;
    /**
     * The {@linkplain Labeled} that is placed over top this
     * {@code TopPromptTextField} instance's underlying {@code TextField}.
     * <p>
     * This is used to show the textfield's prompt once it has content.
     *
     * @return an {@code ObjectProperty} wrapping the {@linkplain Labeled} that is
     *         placed over the textfield
     */
    public final ObjectProperty<Labeled> promptProperty() { return prompt; }
    /**
     * Gets the {@linkplain Labeled} that is placed over the textfield.
     *
     * @return the {@linkplain Labeled} that is placed over the textfield, or
     *         {@code null} if it was not set
     */
    public final Labeled getPrompt() { return prompt.get(); }
    /**
     * Sets the {@linkplain Labeled} that is placed over the textfield.
     *
     * @param value - the {@linkplain Labeled} that is placed over the textfield
     */
    public final void setPrompt(Labeled value) { prompt.set(value); }
    
    
    /* --- Default Stub --- */
    private ObjectProperty<Labeled> defaultStub = new SimpleObjectProperty<>(TopPromptTextField.this, "defaultStub"); //$NON-NLS-1$ ;
    /**
     * The {@linkplain Labeled} that is placed under the textfield to the left of
     * the default text.
     * <p>
     * This is used to label the default value or input. For example in the string
     * {@code "def: 192.168.0.1"}, {@code "def:"} would be the default stub and
     * {@code "192.168.0.1"} would be the default text.
     *
     * @return an {@code ObjectProperty} wrapping the {@linkplain Labeled} that is
     *         placed under the textfield to the left of the default text
     */
    public final ObjectProperty<Labeled> defaultStubProperty() { return defaultStub; }
    /**
     * @return the {@linkplain Labeled} that {@code defaultStubPropery()} wraps
     */
    public final Labeled getDefaultStub() { return defaultStub.get(); }
    /**
     * Sets the {@linkplain Labeled} that {@link #defaultStubProperty()} wraps.
     *
     * @param value - the {@linkplain Labeled} to wrap
     */
    public final void setDefaultStub(Labeled value) { defaultStub.set(value); }
    
    
    /* --- Default Value --- */
    private ObjectProperty<Labeled> defaultValue = new SimpleObjectProperty<>(TopPromptTextField.this, "defaultValue"); //$NON-NLS-1$ ;
    /**
     * The {@linkplain Labeled} that is placed under this
     * {@code TopPromptTextField}'s {@code TextField}.
     * <p>
     * This is used to show the default value or input after the text field's
     * initial contents have changed.
     *
     * @return an {@code ObjectProperty} wrapping the {@linkplain Labeled} that
     *         is placed under the text field.
     */
    public final ObjectProperty<Labeled> defaultValueProperty() { return defaultValue; }
    /**
     * @return the {@linkplain Labeled} that is placed under the text field, or
     *         {@code null} if it was not set
     */
    public final Labeled getDefaultValue() { return defaultValue.get(); }
    /**
     * Sets the {@linkplain Labeled} that is placed under the text field.
     *
     * @param value - the {@linkplain Labeled} that is placed under the
     *        text field
     */
    public final void setDefaultValue(Labeled value) { defaultValue.set(value); }
    
    
    /* --- Scene Builder Label Setters --- */
    
    /* --- Prompt Text --- */
    public final StringProperty promptTextProperty() {
        if (promptText == null) {
            promptText = new SimpleStringProperty(TopPromptTextField.this, "promptText"); //$NON-NLS-1$
        }
        return promptText;
    }
    private StringProperty promptText;
    public final String getPromptText() { return promptText == null ? null : promptText.get(); }
    public final void setPromptText(String value) {
        // only change the label if the new value is different
        Label valLabel = createLabelIfValueIsDifferent(value, promptTextProperty().get());
        if (valLabel == VALUE_IS_NOT_DIFFERENT) { return; }
        
        // set use prompt as prompt text to false if removing prompt
        if (valLabel == null) { usePromptAsPromptTextProperty().set(false); }
        
        promptTextProperty().set(value);
        setPrompt(valLabel);
    }
    
    /* --- Default Stub Text --- */
    public final StringProperty defaultStubTextProperty() {
        if (defaultStubText == null) {
            defaultStubText = new SimpleStringProperty(TopPromptTextField.this, "defaultStubText"); //$NON-NLS-1$
        }
        return defaultStubText;
    }
    private StringProperty defaultStubText;
    public final String getDefaultStubText() { return defaultStubText == null ? null : defaultStubText.get(); }
    public final void setDefaultStubText(String value) {
        // only change the label if the new value is different
        Label valLabel = createLabelIfValueIsDifferent(value, defaultStubTextProperty().get());
        if (valLabel == VALUE_IS_NOT_DIFFERENT) { return; }
        
        defaultStubTextProperty().set(value);
        setDefaultStub(valLabel);
    }
    
    /* --- Default Value Text --- */
    public final StringProperty defaultValueTextProperty() {
        if (defaultValueText == null) {
            defaultValueText = new SimpleStringProperty(TopPromptTextField.this, "defaultValueText", null); //$NON-NLS-1$
        }
        return defaultValueText;
    }
    private StringProperty defaultValueText;
    public final String getDefaultValueText() { return defaultValueText == null ? null : defaultValueText.get(); }
    public final void setDefaultValueText(String value) {
        // only change the label if the new value is different
        Label valLabel = createLabelIfValueIsDifferent(value, defaultValueTextProperty().get());
        if (valLabel == VALUE_IS_NOT_DIFFERENT) { return; }
        
        // set use default value to false if removing default value
        if (valLabel == null) { useDefaultValueProperty().set(false); }
        
        defaultValueTextProperty().set(value);
        setDefaultValue(valLabel);
    }
    
    /**
     * Helper object used to denote that set*Text() methods should return
     * without doing anything.
     */
    private static final Label VALUE_IS_NOT_DIFFERENT = new Label();
    
    /** helper method used by set*Text() methods */
    private Label createLabelIfValueIsDifferent(String newValue, String oldValue) {
        // treat null value the same as empty value
        String nValue = (newValue == null) ? "" : newValue;
        String oValue = (oldValue == null) ? "" : oldValue;
        
        // don't do anything if new value isn't different
        if (nValue.equals(oValue)) { return VALUE_IS_NOT_DIFFERENT; }
        
        // empty value means no label, therefore return null
        return nValue.equals("") ? null : new Label(nValue);
    }
    
    /* --- Use Prompt As Prompt Text --- */
    /**
     * Specifies whether or not the {@code TextField} instance should use
     * {@linkplain #promptProperty() prompt's} text as its prompt text.
     */
    private BooleanProperty usePromptAsPromptText;
    /**
     * @return the use prompt as prompt text property
     */
    public final BooleanProperty usePromptAsPromptTextProperty() {
        if (usePromptAsPromptText == null) {
            usePromptAsPromptText =
            new SimpleBooleanProperty(this, "usePromptAsPromptText", DEFAULT_USE_PROMPT_AS_PROMPT_TEXT) //$NON-NLS-1$
            {
                @Override
                protected void invalidated() {
                    boolean value = get();
                    
                    if (value) {
                        Labeled tmpPrompt = promptProperty().get();
                        if (tmpPrompt == null || tmpPrompt.getText() == null) { return; }
                        
                        String tmpTFPromptText = tfPromptTextProperty().get();
                        if (tmpTFPromptText == null || tmpTFPromptText.isEmpty()) {
                            tfPromptTextProperty().set(tmpPrompt.getText());
                        }
                    }
                    
                }
            };
        }
        return usePromptAsPromptText;
    }
    /**
     * @param value - whether or not to use
     *        {@linkplain #promptProperty() prompt's} text as the
     *        {@code TextField's} prompt text
     */
    public final void setUsePromptAsPromptText(boolean value) {
        usePromptAsPromptTextProperty().set(value);
    }
    /**
     * @return {@code true} if {@linkplain #promptProperty() prompt's} text
     *         should be used as the {@code TextField's} prompt text, otherwise
     *         {@code false}
     */
    public final boolean getUsePromptAsPromptText() {
        return usePromptAsPromptText == null ? false : usePromptAsPromptText.get();
    }
    
    /* --- Use Default Value --- */
    
    private BooleanProperty useDefaultValue;
    
    /**
     * Specifies whether or not to fill the underlying {@code TextField}
     * instance with {@linkplain #defaultValueProperty() defaultValue's}
     * text.
     *
     * @return the use default value property
     */
    public final BooleanProperty useDefaultValueProperty() {
        if (useDefaultValue == null) {
            useDefaultValue =
            new SimpleBooleanProperty(TopPromptTextField.this, "useDefaultValue", DEFAULT_USE_DEFAULT_VALUE) //$NON-NLS-1$
            {
                @Override
                protected void invalidated() {
                    boolean value = get();
                    
                    if (value) {
                        Labeled tmpDefValue = defaultValueProperty().get();
                        if (tmpDefValue == null || tmpDefValue.getText() == null) { return; }
                        
                        String tmpTFText = textProperty().get();
                        if (tmpTFText == null || tmpTFText.isEmpty()) {
                            textProperty().set(tmpDefValue.getText());
                        }
                    }
                    
                }
            };
        }
        return useDefaultValue;
    }
    /**
     * Specifies whether or not to fill the underlying {@code TextField}
     * instance with {@linkplain #defaultValueProperty() defaultValue's}
     * text.
     *
     * @param value - whether or not to fill the text field with
     *        {@linkplain #defaultValueProperty() defaultValue's} text
     */
    public final void setUseDefaultValue(boolean value) { useDefaultValueProperty().set(value); }
    
    /**
     * Whether or not to fill the underlying {@code TextField} instance with
     * {@linkplain #defaultValueProperty() defaultValue's} text.
     *
     * @return {@code true} if the textfield should use
     *         {@linkplain #defaultValueProperty() defaultValue's} text, otherwise
     *         {@code false}
     */
    public final boolean getUseDefaultValue() {
        return useDefaultValue == null ? DEFAULT_USE_DEFAULT_VALUE : useDefaultValue.get();
    }
    
    
    /* --- Settled --- */
    /**
     * Indicates whether this {@code TopPromptTextField} instance's prompt has
     * settled.
     *
     * @return the settled property
     */
    public final ReadOnlyBooleanProperty settledProperty() {
        if (settled == null) {
            settled = new ReadOnlyBooleanWrapper(TopPromptTextField.this, "settled", false)
            {
                @Override
                protected void invalidated() {
                    pseudoClassStateChanged(SETTLED_PSEUDO_CLASS, get() && (getPrompt() != null));
                }
            };
        }
        return settled.getReadOnlyProperty();
    }
    private ReadOnlyBooleanWrapper settled;
    /**
     * Gets whether this {@code TopPromptTextField} instance's prompt has settled.
     *
     * @return {@code true} if the prompt has settled, otherwise {@code false}
     */
    public final boolean isSettled() { return settledProperty().get(); }
    
    
    /***************************************************************************
     *                                                                         *
     * TextField Properties                                                    *
     *                                                                         *
     **************************************************************************/
    
    private TextField textField = new TextField();
    
    /* --- TextField Editable --- */
    /**
     * The editable property associated with this {@code TopPromptTextField}
     * instance's text field.
     * <p>
     * @defaultValue {@code true}
     */
    public final BooleanProperty editableProperty() { return textField.editableProperty(); }
    public final boolean isEditable() { return textField.isEditable(); }
    public final void setEditable(boolean value) { textField.setEditable(value); }
    
    /* --- TextField Font --- */
    public final ObjectProperty<Font> fontProperty() { return textField.fontProperty(); }
    public final Font getFont() { return textField.getFont(); }
    public final void setFont(Font value) { textField.setFont(value); }
    
    /* --- TextField Text --- */
    public final StringProperty textProperty() { return textField.textProperty(); }
    public final String getText() { return textField.getText(); }
    public final void setText(String value) { textField.setText(value); }
    
    /* --- TextField Length --- */
    public final ReadOnlyIntegerProperty lengthProperty() { return textField.lengthProperty(); }
    public final int getLength() { return textField.getLength(); }
    
    /* --- TextField Prompt Text --- */
    private StringProperty tfPromptText;
    public final StringProperty tfPromptTextProperty() {
        if (tfPromptText == null) {
            tfPromptText = new SimpleStringProperty(TopPromptTextField.this, "tfPromptText", ""); //$NON-NLS-1$
        }
        return tfPromptText;
    }
    public final String getTFPromptText() { return tfPromptText == null ? "" : tfPromptText.get(); }
    public final void setTFPromptText(String value) { tfPromptTextProperty().set(value); }
    
    /* --- TextField Alignment --- */
    public final ObjectProperty<Pos> alignmentProperty() { return textField.alignmentProperty(); }
    public final Pos getAlignment() { return textField.getAlignment(); }
    public final void setAlignment(Pos value) { textField.setAlignment(value); }
    
    /* --- TextField Preferred Column Count --- */
    public final IntegerProperty prefColumnCountProperty() { return textField.prefColumnCountProperty(); }
    public final int getPrefColumnCount() { return textField.getPrefColumnCount(); }
    public final void setPrefColumnCount(int value) { textField.setPrefColumnCount(value); }
    
    /* --- TextField OnAction --- */
    /**
     * The action handler associated with this {@code TopPromptTextField} instance's
     * text field, or {@code null} if no action handler is assigned.
     * <p>
     * The action handler is normally called when the user types the ENTER key.
     */
    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return textField.onActionProperty();
    }
    public final EventHandler<ActionEvent> getOnAction() { return textField.getOnAction(); }
    public final void setOnAction(EventHandler<ActionEvent> value) { textField.setOnAction(value); }
    
    
    /* --- Prompt Transition Duration --- */
    private DoubleProperty promptTransitionDuration;
    /**
     * Specifies the prompt's transition duration in milliseconds.
     *
     * @return the prompt's transition duration property
     */
    public final DoubleProperty promptTransitionDurationProperty() {
        if (promptTransitionDuration == null) {
            promptTransitionDuration = new StyleableDoubleProperty(DEFAULT_PROMPT_TRANSLATE_DURATION)
            {
                private double oldValue = get();
                
                @Override
                protected void invalidated() {
                    double value = get();
                    if (value < 0) {
                        if (isBound()) { unbind(); }
                        set(oldValue);
                        throw new IllegalArgumentException("value cannot be negative.");
                    }
                    oldValue = value;
                }
                @Override
                public Object getBean() { return TopPromptTextField.this; }
                @Override
                public String getName() { return "promptTransitionDuration"; } //$NON-NLS-1$
                @Override
                public CssMetaData<TopPromptTextField, Number> getCssMetaData() {
                    return StyleableProperties.PROMPT_TRANSLATE_DURATION;
                }
                
            };
        }
        return promptTransitionDuration;
    }
    /**
     * Gets the prompt's transition duration.
     *
     * @return the duration of the transition, in milliseconds
     */
    public final double getPromptTransitionDuration() {
        return promptTransitionDuration != null ? promptTransitionDuration.get() : DEFAULT_PROMPT_TRANSLATE_DURATION;
    }
    /**
     * Sets the prompt's transition duration to a specified value.
     * <p>
     * The specified value must be greater than zero.
     *
     * @param milliseconds - the length of the the translation transition, in
     *        milliseconds
     */
    public final void setPromptTransitionDuration(double milliseconds) {
        promptTransitionDurationProperty().set(milliseconds);
    }
    
    /* --- Prompt Translate X --- */
    private DoubleProperty promptTranslateX;
    /**
     * Specifies the prompt's ending X-translate coordinate.
     *
     * @return the prompt's ending X-translate coordinate property
     */
    public final DoubleProperty promptTranslateXProperty() {
        if (promptTranslateX == null) {
            promptTranslateX = new StyleableDoubleProperty(DEFAULT_PROMPT_TRANSLATE_X)
            {
                @Override
                public CssMetaData<TopPromptTextField, Number> getCssMetaData() {
                    return StyleableProperties.PROMPT_TRANSLATE_X;
                }
                @Override
                public Object getBean() { return TopPromptTextField.this; }
                @Override
                public String getName() { return "promptTranslateX"; } //$NON-NLS-1$
            };
        }
        
        return promptTranslateX;
    }
    /**
     * Sets the prompt's ending X-translate coordinate.
     *
     * @param value - the new ending X-translate coordinate
     */
    public final void setPromptTranslateX(double value) {
        promptTranslateXProperty().setValue(value);
    }
    /**
     * Gets the prompt's ending X-translate coordinate.
     *
     * @return the prompt's ending X-translate coordinate
     */
    public final double getPromptTranslateX() {
        return promptTranslateX != null ? promptTranslateX.getValue() : DEFAULT_PROMPT_TRANSLATE_X;
    }
    
    /* --- Prompt Translate Y --- */
    private DoubleProperty promptTranslateY;
    /**
     * Specifies the prompt's ending Y-translate coordinate.
     *
     * @return the prompt's ending Y-translate coordinate property
     */
    public final DoubleProperty promptTranslateYProperty() {
        if (promptTranslateY == null) {
            promptTranslateY = new StyleableDoubleProperty(DEFAULT_PROMPT_TRANSLATE_Y)
            {
                @Override
                public CssMetaData<TopPromptTextField, Number> getCssMetaData() {
                    return StyleableProperties.PROMPT_TRANSLATE_Y;
                }
                @Override
                public Object getBean() { return TopPromptTextField.this; }
                @Override
                public String getName() { return "promptTranslateY"; } //$NON-NLS-1$
            };
        }
        
        return promptTranslateY;
    }
    /**
     * Sets the prompt's ending Y-translate coordinate.
     *
     * @param value - the new ending Y-translate coordinate
     */
    public final void setPromptTranslateY(double value) {
        promptTranslateYProperty().setValue(value);
    }
    /**
     * Gets the prompt's ending Y-translate coordinate.
     *
     * @return the prompt's ending Y-translate coordinate
     */
    public final double getPromptTranslateY() {
        return promptTranslateY != null ? promptTranslateY.getValue() : DEFAULT_PROMPT_TRANSLATE_Y;
    }
    
    /* --- Prompt Scale X --- */
    /**
     * Specifies the prompt's ending X-scale value.
     *
     * @return the prompt's ending X-scale property
     */
    public final DoubleProperty promptScaleXProperty() {
        if (promptScaleX == null) {
            promptScaleX = new StyleableDoubleProperty(DEFAULT_PROMPT_SCALE_X)
            {
                private double oldValue = get();
                
                @Override
                protected void invalidated() {
                    double value = get();
                    if (value < 0) {
                        if (isBound()) { unbind(); }
                        set(oldValue);
                        throw new IllegalArgumentException("value cannot be negative.");
                    }
                    oldValue = value;
                }
                @Override
                public CssMetaData<TopPromptTextField, Number> getCssMetaData() {
                    return StyleableProperties.PROMPT_SCALE_X;
                }
                @Override
                public Object getBean() { return TopPromptTextField.this; }
                @Override
                public String getName() { return "promptScaleX"; } //$NON-NLS-1$
            };
        }
        return promptScaleX;
    }
    private DoubleProperty promptScaleX;
    /**
     * Sets the prompt's ending X-scale value.
     *
     * @param value - the prompt's new ending X-scale value
     */
    public final void setPromptScaleX(double value) {
        promptScaleXProperty().setValue(value);
    }
    /**
     * Gets the prompt's ending X-scale value.
     *
     * @return the prompt's ending X-scale value
     */
    public final double getPromptScaleX() {
        return promptScaleX != null ? promptScaleX.getValue() : DEFAULT_PROMPT_SCALE_X;
    }
    
    /* --- Prompt Scale Y --- */
    /**
     * Specifies the prompt's ending Y-scale value.
     *
     * @return the prompt's ending Y-scale property
     */
    public final DoubleProperty promptScaleYProperty() {
        if (promptScaleY == null) {
            promptScaleY = new StyleableDoubleProperty(DEFAULT_PROMPT_SCALE_Y)
            {
                private double oldValue = get();
                
                @Override
                protected void invalidated() {
                    double value = get();
                    if (value < 0) {
                        if (isBound()) { unbind(); }
                        set(oldValue);
                        throw new IllegalArgumentException("value cannot be negative.");
                    }
                    oldValue = value;
                }
                @Override
                public CssMetaData<TopPromptTextField, Number> getCssMetaData() {
                    return StyleableProperties.PROMPT_SCALE_Y;
                }
                @Override
                public Object getBean() { return TopPromptTextField.this; }
                @Override
                public String getName() { return "promptScaleY"; } //$NON-NLS-1$
            };
        }
        return promptScaleY;
    }
    private DoubleProperty promptScaleY;
    /**
     * Sets the prompt's ending Y-scale value.
     *
     * @param value - the prompt's new ending Y-scale value
     */
    public final void setPromptScaleY(double value) {
        promptScaleYProperty().setValue(value);
    }
    /**
     * Gets the prompt's ending X-scale value.
     *
     * @return the prompt's ending X-scale value
     */
    public final double getPromptScaleY() {
        return promptScaleY != null ? promptScaleY.getValue() : DEFAULT_PROMPT_SCALE_Y;
    }
    
    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new TopPromptTextFieldSkin(this)
        {
            @Override
            public ObjectProperty<Labeled> promptProperty() {
                return TopPromptTextField.this.promptProperty();
            }
            @Override
            public ObjectProperty<Labeled> defaultStubProperty() {
                return TopPromptTextField.this.defaultStubProperty();
            }
            @Override
            public ObjectProperty<Labeled> defaultValueProperty() {
                return TopPromptTextField.this.defaultValueProperty();
            }
            @Override
            public TextField getTextField() { return textField; }
        };
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/
    
    /** This control's CSS style class. */
    private static final String DEFAULT_STYLE_CLASS = "top-prompt-text-field"; //$NON-NLS-1$
    
    /** CSS ':settled' signals that the prompt is settled above the textfield */
    private static final PseudoClass SETTLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("settled"); //$NON-NLS-1$
    /** CSS ':not-editable' signals that the textfield is not editable */
    private static final PseudoClass NOT_EDITABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("not-editable"); //$NON-NLS-1$
    
    /** {@inheritDoc} */
    @Override
    public String getUserAgentStylesheet() {
        return TopPromptTextField.class.getResource("topprompttextfield.css").toExternalForm(); //$NON-NLS-1$
    }
    
    /** Instantiates all of the this control's CSS styleable properties */
    private static class StyleableProperties {
        
        /* --- Prompt Translate Duration --- */
        private static final String PROMPT_T_D = "-prompt-transition-duration"; //$NON-NLS-1$
        private static final CssMetaData<TopPromptTextField, Number> PROMPT_TRANSLATE_DURATION =
        new CssMetaData<TopPromptTextField, Number>(PROMPT_T_D, SizeConverter.getInstance(),
            DEFAULT_PROMPT_TRANSLATE_DURATION)
        {
            @Override
            public boolean isSettable(TopPromptTextField tptf) {
                return tptf.promptTransitionDuration == null || !tptf.promptTransitionDuration.isBound();
            }
            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Number> getStyleableProperty(TopPromptTextField tptf) {
                return (StyleableProperty<Number>) tptf.promptTransitionDurationProperty();
            }
        };
        
        /* --- Prompt Translate X --- */
        private static final String PROMPT_T_X = "-prompt-translate-x"; //$NON-NLS-1$
        private static final CssMetaData<TopPromptTextField, Number> PROMPT_TRANSLATE_X =
        new CssMetaData<TopPromptTextField, Number>(PROMPT_T_X, SizeConverter.getInstance(), DEFAULT_PROMPT_TRANSLATE_X)
        {
            @Override
            public boolean isSettable(TopPromptTextField tptf) {
                return tptf.promptTranslateX == null || !tptf.promptTranslateX.isBound();
            }
            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Number> getStyleableProperty(TopPromptTextField tptf) {
                return (StyleableProperty<Number>) tptf.promptTranslateXProperty();
            }
        };
        
        /* --- Prompt Translate Y --- */
        private static final String PROMPT_T_Y = "-prompt-translate-y"; //$NON-NLS-1$
        private static final CssMetaData<TopPromptTextField, Number> PROMPT_TRANSLATE_Y =
        new CssMetaData<TopPromptTextField, Number>(PROMPT_T_Y, SizeConverter.getInstance(), DEFAULT_PROMPT_TRANSLATE_Y)
        {
            @Override
            public boolean isSettable(TopPromptTextField tptf) {
                return tptf.promptTranslateY == null || !tptf.promptTranslateY.isBound();
            }
            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Number> getStyleableProperty(TopPromptTextField tptf) {
                return (StyleableProperty<Number>) tptf.promptTranslateYProperty();
            }
        };
        
        /* --- Prompt Scale X --- */
        private static final String PROMPT_S_X = "-prompt-scale-x"; //$NON-NLS-1$
        private static final CssMetaData<TopPromptTextField, Number> PROMPT_SCALE_X =
        new CssMetaData<TopPromptTextField, Number>(PROMPT_S_X, SizeConverter.getInstance(), DEFAULT_PROMPT_SCALE_X)
        {
            @Override
            public boolean isSettable(TopPromptTextField tptf) {
                return tptf.promptScaleX == null || !tptf.promptScaleX.isBound();
            }
            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Number> getStyleableProperty(TopPromptTextField tptf) {
                return (StyleableProperty<Number>) tptf.promptScaleXProperty();
            }
        };
        
        /* --- PROMPT_SCALE_Y --- */
        private static final String PROMPT_S_Y = "-prompt-scale-y"; //$NON-NLS-1$
        private static final CssMetaData<TopPromptTextField, Number> PROMPT_SCALE_Y =
        new CssMetaData<TopPromptTextField, Number>(PROMPT_S_Y, SizeConverter.getInstance(), DEFAULT_PROMPT_SCALE_Y)
        {
            @Override
            public boolean isSettable(TopPromptTextField tptf) {
                return tptf.promptScaleY == null || !tptf.promptScaleY.isBound();
            }
            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Number> getStyleableProperty(TopPromptTextField tptf) {
                return (StyleableProperty<Number>) tptf.promptScaleYProperty();
            }
        };
        
        
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
            new ArrayList<>(SkinBase.getClassCssMetaData());
            styleables.add(PROMPT_TRANSLATE_DURATION);
            styleables.add(PROMPT_TRANSLATE_X);
            styleables.add(PROMPT_TRANSLATE_Y);
            styleables.add(PROMPT_SCALE_X);
            styleables.add(PROMPT_SCALE_Y);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        
    }
    
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
     * Gets an unmodifiable list of this {@code TopPromptTextField} control's CSS
     * styleable properties.
     *
     * @return unmodifiable list of this control's CSS styleable properties
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() { return getClassCssMetaData(); }
    
}

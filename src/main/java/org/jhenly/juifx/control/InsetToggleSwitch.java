/**
 * Copyright (c) 2015, ControlsFX All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of ControlsFX, any associated
 * website, nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jhenly.juifx.control;

import org.jhenly.juifx.control.skin.InsetToggleSwitchSkin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;


/**
 * Much like a Toggle Button this control allows the user to toggle between one
 * of two states.
 * <p>
 * It has been popularized in touch based devices where its usage is
 * particularly useful because unlike a checkbox the finger touch of a user
 * doesn't obscure the control.
 * <p>
 * Shown below is a screenshot of the InsetToggleSwitch control in its on and
 * off state: <br>
 * <center>
 * <img src="InsetToggleSwitch.png" alt="Screenshot of InsetToggleSwitch">
 * </center>
 * <p>
 * <b>Note:</b> This class is heavily based, almost verbatim, on ControlFX's
 * <a href='https://github.com/controlsfx/controlsfx/blob/master/controlsfx/src/main/java/org/controlsfx/control/ToggleSwitch.java' target='_top'>{@code ToggleSwitch}</a>.
 * There are only minor differences, like the placement
 * of the {@code Label} and the thumb area color transition.
 * <p>
 *
 * @author ControlsFX (original {@code ToggleSwitch})
 * @author Jonathan Henly ({@code InsetToggleSwitch})
 */
public class InsetToggleSwitch extends Labeled {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Creates an {@code InsetToggleSwitch} instance with an empty string for
     * its label.
     */
    public InsetToggleSwitch() {
        initialize();
    }
    
    /**
     * Creates an {@code InsetToggleSwitch} instance  with the specified label.
     *
     * @param text - the label string of the control.
     */
    public InsetToggleSwitch(String text) {
        super(text);
        initialize();
    }
    
    /** All constructors use this method. */
    private void initialize() { getStyleClass().add(DEFAULT_STYLE_CLASS); }
    
    
    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
    
    // --- selected
    /** Indicates whether the {@code InsetToggleSwitch} is selected. */
    private BooleanProperty selected;

    public final BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new BooleanPropertyBase()
            {
                @Override
                protected void invalidated() {
                    final Boolean v = get();
                    pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, v);
                }
                @Override
                public Object getBean() { return InsetToggleSwitch.this; }
                @Override
                public String getName() { return "selected"; }
            };
        }
        return selected;
    }
    public final void setSelected(boolean value) { selectedProperty().set(value); }
    public final boolean isSelected() { return selected == null ? false : selected.get(); }


    /**************************************************************************
     *                                                                        *
     * Methods                                                                *
     *                                                                        *
     **************************************************************************/
    
    /**
     * Toggles the {@code selected} state of this {@code InsetToggleSwitch}
     * instance.
     * <p>
     * The {@code InsetToggleSwitch} will cycle through the selected and unselected
     * states.
     */
    public void fire() {
        if (!isDisabled()) {
            setSelected(!isSelected());
            fireEvent(new ActionEvent());
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected Skin<?> createDefaultSkin() { return new InsetToggleSwitchSkin(this); }
    
    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/
    
    private static final String DEFAULT_STYLE_CLASS = "inset-toggle-switch";
    
    /** The CSS ':selected' style indicator. */
    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    
    /** {@inheritDoc} */
    @Override
    public String getUserAgentStylesheet() {
        return InsetToggleSwitch.class.getResource("insettoggleswitch.css").toExternalForm();
    }
    
}

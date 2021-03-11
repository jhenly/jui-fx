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
package com.jhenly.juifx.control.skin;

import java.util.function.Consumer;

import com.jhenly.juifx.animation.JuiFillTransition;
import com.jhenly.juifx.animation.SingleFillTransition;
import com.jhenly.juifx.control.FillButton;
import com.jhenly.juifx.control.Fillable;

import impl.com.jhenly.juifx.fill.Fill;
import impl.com.jhenly.juifx.fill.FillApplier;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;


/**
 * Default skin implementation for the {@code FillButton} control.
 *
 * @param <C> - the type that extends {@code FillButton}
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * @see FillButton
 */
public class FillButtonSkin<C extends FillButton> extends SelectableButtonSkin<C> implements FillableSkin<C> {
    
    /***************************************************************************
     *                                                                         *
     * Private Members                                                         *
     *                                                                         *
     **************************************************************************/
    
    private JuiFillTransition jfTrans;
    
    
    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Creates a new {@code FillButtonSkin} instance.
     *
     * @param control - the control that this skin should be installed onto
     */
    public FillButtonSkin(final C control) {
        super(control);
        
        
        if (control.getFill() != null && control.getFill().hasFillSpans()) {
            createJuiFillTransition(control);
        }
        
        // register FillButton change listeners
        registerChangeListener(control.fillEnabledProperty(), o -> setFillEnabled(thisSkinnable().isFillEnabled()));
        registerChangeListener(control.fillProperty(), o -> updateJuiFillTransition(thisSkinnable()));
        
        // register SelectableButton change listeners
        registerChangeListener(control.selectedProperty(), o -> handleButtonSelected(thisSkinnable().isSelected()));
        
        // register Button change listeners
        registerChangeListener(control.armedProperty(), o -> handleButtonArmed(thisSkinnable().isArmed()));
        
        // register Node change listeners
        registerChangeListener(control.hoverProperty(), o -> handleButtonHover(thisSkinnable().isHover()));
        registerChangeListener(control.focusedProperty(), o -> handleButtonFocused(thisSkinnable().isFocused()));
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        if (thisSkinnable() == null) { return; }
        
        disposeOfJuiFillTransition();
        super.dispose();
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Private Implementation                                                  *
     *                                                                         *
     **************************************************************************/
    
    private void setFillEnabled(final boolean enabled) {
        if (!enabled) {
            if (thisSkinnable().isSelected()) { return; }
            
            // go back to start, set fill to fill-from
            gotoStartOfTransition();
        } else {
            // if fill transition is enabled while hover or focus then play fill
            if (thisSkinnable().isHover() || thisSkinnable().isFocused()) {
                playForward();
            }
        }
    }
    
    private void handleButtonSelected(boolean selected) {
        if (selected) {
            gotoEndOfTransition();
        } else {
            gotoStartOfTransition();
        }
    }
    
    private void handleButtonArmed(boolean armed) {
        if (thisSkinnable().isSelected()) { return; }
        
        if (armed) {
            // fillTrans.stop();
        } else {
            playBackward();
        }
        
    }
    
    private void handleButtonHover(boolean isHovering) {
        // don't alter the button if it's selected
        if (thisSkinnable().isSelected()) { return; }
        
        if (isHovering) {
            playForward();
        } else {
            
            if (thisSkinnable().isArmed() && thisSkinnable().isFocused()) {
                
            }
            if (thisSkinnable().isArmed()) {
                playBackward();
                return;
            }
            
            // don't un-fill if the button has focus and it's not armed
            if (thisSkinnable().isFocused()) { return; }
            
            playBackward();
        }
    }
    
    private void handleButtonFocused(boolean isFocused) {
        // don't alter the button if it's being hovered or it's selected
        if (thisSkinnable().isHover() || thisSkinnable().isSelected()) { return; }
        
        if (isFocused && !thisSkinnable().isPressed()) {
            playForward();
        } else {
            playBackward();
        }
        
    }
    
    /** Plays transition forward, on finish sets fill to control's fill-from. */
    private void playForward() {
        if (jfTrans == null || jfTrans.isAtEnd() || jfTrans.isPlayingForward()) { return; }
        
        // play forward if at start, rate is <= 0 or transition isn't playing
        jfTrans.setRate(1.0);
        jfTrans.play();
    }
    
    /** Plays transition forward, on finish sets fill to control's fill-to. */
    private void playBackward() {
        if (jfTrans == null || jfTrans.isAtStart() || jfTrans.isPlayingBackward()) { return; }
        
        // play backward if at end, rate is >= 0 or transition isn't playing
        jfTrans.setRate(-1.0);
        jfTrans.play();
    }
    
    /** Goes to end of transition, sets fill to control's fill-to. */
    private void gotoEndOfTransition() {
        if (jfTrans == null || jfTrans.isAtEnd()) { return; }
        jfTrans.stop();
        jfTrans.jumpTo(jfTrans.getTotalDuration());
        jfTrans.setRate(-1.0);
        
        thisSkinnable().getFillApplier().interpolateAndApply(1.0);
    }
    
    /** Goes to start of transition, resets control's background. */
    private void gotoStartOfTransition() {
        if (jfTrans == null || jfTrans.isAtStart()) { return; }
        jfTrans.stop();
        jfTrans.jumpTo(Duration.ZERO);
        jfTrans.setRate(1.0);
        
        thisSkinnable().getFillApplier().interpolateAndApply(0.0);
    }
    
    /** Helper that updates 'juiFillTrans' when fillable's fill changes. */
    private void updateJuiFillTransition(Fillable fillable) {
        final Fill fill = fillable.getFill();
        if (!fill.hasFillSpans()) {
            // we don't need a fill transition if we don't have fill spans
            disposeOfJuiFillTransition();
            return;
        }
        
        if (jfTrans == null) {
            createJuiFillTransition(fillable);
            return;
        }
    }
    
    /** Helper that creates a single or multi fill transition. */
    private void createJuiFillTransition(Fillable fillable) {
        jfTrans = new SingleFillTransition(fillable);
        jfTrans.setCycleCount(1);
    }
    
    /** Null-safe helper that stops, disposes and nulls out 'juiFillTrans'. */
    private void disposeOfJuiFillTransition() {
        if (jfTrans != null) {
            jfTrans.stop();
            jfTrans.dispose();
            jfTrans = null;
        }
    }
    
    
    @Override
    public FillApplier<?> createDefaultFillApplier() { // TODO Auto-generated method stub
        return null;
    }
    
    
    @Override
    public void addChangeListener(ObservableValue<?> property, Consumer<ObservableValue<?>> consumer) {
        
    }
    
}

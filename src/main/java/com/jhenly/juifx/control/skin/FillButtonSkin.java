/**
 * Copyright (c) 2021, JuiFX All rights reserved.
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
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jhenly.juifx.control.skin;

import com.jhenly.juifx.animation.JuiFillTransition;
import com.jhenly.juifx.animation.MultiFillTransition;
import com.jhenly.juifx.animation.SingleFillTransition;
import com.jhenly.juifx.control.FillButton;
import com.jhenly.juifx.control.Fillable;
import com.jhenly.juifx.layout.Fill;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Background;
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
public class FillButtonSkin<C extends FillButton> extends SelectableButtonSkin<FillButton> {
    
    /***************************************************************************
     *                                                                         *
     * Private Members                                                         *
     *                                                                         *
     **************************************************************************/
    
    private final Background originalBackground;
    // private final FillTransition fillTrans;
    private final EventHandler<ActionEvent> onFinishedHandler;
    
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
    public FillButtonSkin(final FillButton control) {
        super(control);
        
        // remember the control's original background
        originalBackground = control.getBackground();
        
        if (control.getFill() != null && control.getFill().hasFillSpans()) {
            createJuiFillTransition(control);
        }
        
        // ensures background color is correct on transition finish
        onFinishedHandler = action -> {
            /* if duration is greater than 0 then we've reached the end of the
             * transition otherwise we're at the start */
            if (jfTrans.getCurrentTime().greaterThan(Duration.ZERO)) {
                /* need the following to clear fill artifacts and ensure
                 * control's fill ends on fill-to color */
                // setShapeOrRectFill(thisSkinnable().getFillTo());
            } else {
                /* similarly, need the following to ensure control's fill ends
                 * on original color */
                System.out.println("ON FINISH HANDLER, DURATION !>= Duration.ZERO --- SETTING BACKGROUND");
                thisSkinnable().setBackground(originalBackground);
            }
        };
        
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
        System.out.println("SET_FILL_ENABLED: " + enabled);
        if (!enabled) {
            // go back to start, set fill to fill-from
            gotoStartOfTransition();
        } else {
            // if fill transition is enabled while hover or focus then play fill
            if (thisSkinnable().isHover() || thisSkinnable().isFocused()) {
                playForward();
            }
        }
    }
    
    private boolean HOVER = false;
    private boolean FOCUS = false;
    private boolean ARM = false;
    private boolean SELECT = false;
    
    private void log(String msg) {
        String out = "FillButtonSkin: " + stripToAt(this);
        out += toString();
        out += String.format("hov[%b] ", HOVER);
        out += String.format("foc[%b] ", FOCUS);
        out += String.format("arm[%b] ", ARM);
        out += String.format("sel[%b] ", SELECT);
        out += "\t" + msg;
        System.out.println(out);
    }
    
    private void a_log(String msg) {
        System.out.println("  " + stripToAt(this) + "\t\t" + msg);
    }
    
    private String stripToAt(Object obj) {
        if (obj == null) { return "null"; }
        
        String to = obj.toString();
        int idex = to.indexOf("@");
        if (idex == -1) { return to; }
        
        return to.substring(idex);
    }
    
    private void handleButtonSelected(boolean selected) {
        SELECT = selected;
        log("handleButtonSelected");
        if (selected) {
            a_log("Button Selected -- ");
            a_log("going to end of transition");
            gotoEndOfTransition();
        } else {
            a_log("Button Deselected -- ");
            
            a_log("going to start of transition");
            gotoStartOfTransition();
        }
    }
    
    private void handleButtonArmed(boolean armed) {
        ARM = armed;
        log("handleButtonArmed");
        if (thisSkinnable().isSelected()) {
            a_log("Skinnable is Selected -- ");
            a_log("returning");
            return;
        }
        
        if (armed) {
            a_log("Button Armed");
            // fillTrans.stop();
        } else {
            a_log("Button Disarmed -- ");
            if (!thisSkinnable().isSelected()) {
                a_log("skinnable not selected, playing transition backward");
                playBackward();
            }
            
        }
        
    }
    
    private void handleButtonHover(boolean isHovering) {
        HOVER = isHovering;
        log("handleButtonHover");
        a_log("    jfTrans Current TIME: " + jfTrans.getCurrentTime().toMillis());
        // don't alter the button if it's selected
        if (thisSkinnable().isSelected()) {
            a_log("Skinnable is Selected -- ");
            a_log("returning");
            return;
        }
        
        if (isHovering) {
            a_log("Button Hovering -- ");
            a_log("    jfTrans Current TIME: " + jfTrans.getCurrentTime().toMillis());
            a_log("playing forward");
            playForward();
            a_log("    jfTrans Current TIME: " + jfTrans.getCurrentTime().toMillis());
        } else {
            
            if (thisSkinnable().isArmed() && thisSkinnable().isFocused()) {
                
            }
            a_log("Button UnHovering -- ");
            if (thisSkinnable().isArmed()) {
                a_log("playing transition backward");
                playBackward();
                return;
            }
            
            // don't un-fill if the button has focus and it's not armed
            if (thisSkinnable().isFocused()) {
                a_log("skinnable is focused, returning");
                return;
            }
            
            a_log("skinnable is not focused, playing backward");
            playBackward();
        }
    }
    
    private void handleButtonFocused(boolean isFocused) {
        FOCUS = isFocused;
        log("handleButtonFocused");
        // don't alter the button if it's being hovered or it's selected
        if (thisSkinnable().isHover() || thisSkinnable().isSelected()) {
            a_log("Skinnable is Hovering or Skinnable is Selected -- ");
            a_log("returning");
            return;
        }
        
        if (isFocused && !thisSkinnable().isPressed()) {
            a_log("Button Focused && Skinnable Not Pressed-- ");
            a_log("playing forward");
            playForward();
        } else {
            a_log("Button Not Focused -- ");
            a_log("playing backward");
            playBackward();
        }
        
    }
    
    /** Plays transition forward, on finish sets fill to control's fill-from. */
    private void playForward() {
        System.out.println("current time: " + jfTrans.getCurrentTime() + " current rate: " + jfTrans.getRate()
            + " total_duration: " + jfTrans.getTotalDuration());
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
        jfTrans.setOnFinished(null);
        jfTrans.jumpTo(jfTrans.getTotalDuration());
        jfTrans.setRate(-1.0);
        
        jfTrans.setOnFinished(onFinishedHandler);
    }
    
    /** Goes to start of transition, resets control's background. */
    private void gotoStartOfTransition() {
        if (jfTrans == null || jfTrans.isAtStart()) { return; }
        
        jfTrans.stop();
        jfTrans.setOnFinished(null);
        jfTrans.jumpTo(Duration.ZERO);
        jfTrans.setRate(1.0);
        
        thisSkinnable().setBackground(originalBackground);
        
        jfTrans.setOnFinished(onFinishedHandler);
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
        
        // check if we should switch to different jui fill transition
        if (fill.getFillSpans().size() == 1) {
            // switch to multi fill transition if trans is single fill
            if (!(jfTrans instanceof SingleFillTransition)) {
                switchToMultiFillTransition(fillable);
            }
        } else {
            // switch to single fill transition if trans is multi fill
            if (!(jfTrans instanceof MultiFillTransition)) {
                switchToSingleFillTransition(fillable);
            }
        }
    }
    
    /** Helper that switches from a multi to a single fill transition. */
    private void switchToSingleFillTransition(Fillable fillable) {
        // juiFillTrans should be a MultiFillTransition at this point
        disposeOfJuiFillTransition();
        
        jfTrans = new SingleFillTransition(fillable);
    }
    
    /** Helper that switches from a single to a multi fill transition. */
    private void switchToMultiFillTransition(Fillable fillable) {
        // juiFillTrans should be a SingleFillTransition at this point
        disposeOfJuiFillTransition();
        
        jfTrans = new MultiFillTransition(fillable);
    }
    
    /** Helper that creates a single or multi fill transition. */
    private void createJuiFillTransition(Fillable fillable) {
        final Fill fill = fillable.getFill();
        
        if (fill.getFillSpans().size() == 1) {
            jfTrans = new SingleFillTransition(fillable);
        } else {
            jfTrans = new MultiFillTransition(fillable);
        }
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
    
}

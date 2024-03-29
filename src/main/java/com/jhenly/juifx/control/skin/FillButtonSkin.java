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

import com.jhenly.juifx.animation.JuiFillTransition;
import com.jhenly.juifx.control.FillButton;
import com.jhenly.juifx.control.applier.FillApplier;
import com.jhenly.juifx.control.applier.FillButtonApplier;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.input.MouseEvent;


/**
 * Default skin implementation for the {@code FillButton} control.
 *
 * @param <C> - the type that extends {@code FillButton}
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * @see FillButton
 */
public class FillButtonSkin<C extends FillButton> extends ButtonSkin implements FillableSkin<C> {
    
    /***************************************************************************
     *                                                                         *
     * Private Members                                                         *
     *                                                                         *
     **************************************************************************/
    
    private JuiFillTransition jfTrans;
    private BooleanBinding fillDisabled;
    private EventHandler<MouseEvent> clickedHandler;
    
    
    /***************************************************************************
     *                                                                         *
     * Listener(s)                                                             *
     *                                                                         *
     **************************************************************************/
    
    private ChangeListener<Boolean> focusChange = (obv, o, n) -> onFocused(n);
    private WeakChangeListener<Boolean> weakFocusChange = new WeakChangeListener<>(focusChange);
    
    
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
        
        // setFillApplier(createDefaultFillApplier());
        
        // create JuiFillTransition after fill applier has been set
        jfTrans = new JuiFillTransition(getFillApplier());
        jfTrans.durationProperty().bind(control.fillDurationProperty());
        
        fillDisabled = control.fillEnabledProperty().not();
        
        // register FillButton change listeners
        registerChangeListener(control.fillEnabledProperty(), o -> onFillEnabled(getFillable().isFillEnabled()));
        
        // register Node change listeners
        registerChangeListener(control.hoverProperty(), o -> onHover(getFillable().isHover()));
        
        clickedHandler = e -> onMouseClicked();
        control.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedHandler);
        
        // add focus change listener if fillOnFocus is true
        if (control.isFillOnFocus()) { control.focusedProperty().addListener(weakFocusChange); }
        
        // fill on focused listener
        registerChangeListener(control.fillOnFocusProperty(), o -> {
            final BooleanProperty fillOnFocus = getFillable().fillOnFocusProperty();
            if (fillOnFocus.get()) {
                getFillable().focusedProperty().addListener(weakFocusChange);
            } else {
                getFillable().focusedProperty().removeListener(weakFocusChange);
            }
        });
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API (from Skin)                                                  *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        if (getFillable() == null) { return; }
        
        getFillable().removeEventHandler(MouseEvent.MOUSE_CLICKED, clickedHandler);
        getFillable().focusedProperty().removeListener(weakFocusChange);
        
        if (jfTrans != null) {
            jfTrans.dispose();
            jfTrans = null;
        }
        
        if (getFillApplier() != null) {
            getFillApplier().dispose();
        }
        
        super.dispose();
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API (from FillableSkin)                                          *
     *                                                                         *
     **************************************************************************/
    
    @SuppressWarnings("unchecked")
    @Override
    public C getFillable() { return (C) getSkinnable(); }
    
    @Override
    public FillApplier<C> createDefaultFillApplier() { return new FillButtonApplier<C>(getFillable()); }
    
    
    @Override
    public final ReadOnlyObjectProperty<FillApplier<C>> fillApplierProperty() {
        return fillApplier.getReadOnlyProperty();
    }
    protected void setFillApplier(FillApplier<C> value) { fillApplier.set(value); }
    private ReadOnlyObjectWrapper<FillApplier<C>> fillApplier =
    new ReadOnlyObjectWrapper<FillApplier<C>>(this, "fillApplier", createDefaultFillApplier());
    
    
    /***************************************************************************
     *                                                                         *
     * Private Implementation                                                  *
     *                                                                         *
     **************************************************************************/
    
    private void onFillEnabled(final boolean enabled) {
        if (!enabled) {
            // go back to start, reset fillable to its pre-fill state
            jfTrans.jumpToStart();
            getFillApplier().resetFillable();
        } else {
            // if fill transition is enabled while hover or focus then play fill
            if (getFillable().isHover() || getFillable().isFocused()) {
                jfTrans.playForward();
            }
        }
    }
    
    private void onMouseClicked() {
        if (fillDisabled.get()) { return; }
        
        jfTrans.jumpToStart();
        if (getFillable().isHover()) {
            jfTrans.playForward();
        }
    }
    
    private void onHover(boolean isHovering) {
        // don't alter the button if fill is disabled or it's selected
        if (fillDisabled.get()) { return; }
        
        if (isHovering) {
            jfTrans.playForward();
        } else {
            final C fable = getFillable();
            
            // don't un-fill if the button has focus and it's not armed
            if (fable.isFillOnFocus() && fable.isFocused()) { return; }
            
            jfTrans.playBackward();
        }
    }
    
    private void onFocused(boolean isFocused) {
        // don't alter the button if it's being hovered or it's selected
        if (fillDisabled.get() || getFillable().isHover()) { return; }
        
        if (isFocused && !getFillable().isPressed()) {
            jfTrans.playForward();
        } else {
            jfTrans.playBackward();
        }
        
    }
    
    
}

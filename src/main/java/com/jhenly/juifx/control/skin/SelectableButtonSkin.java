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

import com.jhenly.juifx.control.SelectableButton;

import javafx.beans.InvalidationListener;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.control.skin.LabeledSkinBase;


/**
 * Default skin implementation for the {@code SelectableButton} control.
 *
 * @param <C> - the type that extends {@code SelectableButton}
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * @see SelectableButton
 * @see #thisSkinnable()
 * 
 * @apiNote
 * This class could just extend {@link LabeledSkinBase
 * LabeledSkinBase&lt;SelectableButton&gt;}, but we need {@link ButtonSkin}'s
 * {@code ButtonBehavior} in order to function as a button.
 */
public class SelectableButtonSkin<C extends SelectableButton> extends ButtonSkin {
    
    InvalidationListener disabled;
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Creates a {@code SelectableButton} skin.
     * <p>
     * This constructor simply calls
     * {@link ButtonSkin#ButtonSkin(javafx.scene.control.Button)
     * super(control)}.
     * 
     * @param control - the control that this skin should be installed onto
     * 
     * @see ButtonSkin#ButtonSkin(javafx.scene.control.Button) ButtonSkin(Button)
     */
    public SelectableButtonSkin(C control) {
        super(control);
        
        disabled = obv -> {
            if (thisSkinnable().isDisable()) {
                thisSkinnable().deselect();
            }
        };
        
        control.disabledProperty().addListener(disabled);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    @Override
    public void dispose() {
        if (thisSkinnable() == null) { return; }
        
        thisSkinnable().disabledProperty().removeListener(disabled);
        
        super.dispose();
    }
    
    /**
     * Adapter method that simply invokes the following:<pre>
     * return (C) getSkinnable();</pre>
     * 
     * @return the skinnable cast to {@code C}, where {@code C} extends
     *         {@code SelectableButton}       
     * @see #getSkinnable()
     * 
     * @apiNote
     * This method is necessary to get the skinnable without having to cast the
     * result of {@code getSkinnable()} every time. This problem is caused by
     * {@link ButtonSkin} extending {@link LabeledSkinBase
     * LabeledSkinBase&lt;Button&gt;} and not including a {@code <C extends
     * Button>} type parameter itself. This class could just extend {@code
     * LabeledSkinBase<SelectableButton>}, but we need {@code ButtonSkin}'s {@code
     * ButtonBehavior} in order to function as a button.
     */
    @SuppressWarnings("unchecked")
    public final C thisSkinnable() { return (C) getSkinnable(); }
    
}

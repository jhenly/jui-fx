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
package com.jhenly.juifx.control;

import com.jhenly.juifx.control.event.SelectionEvent;
import com.jhenly.juifx.layout.SelectHBox;
import com.jhenly.juifx.layout.SelectVBox;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;

/**
 * The selectable interface.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * @see SelectableButton
 * @see SelectVBox
 * @see SelectHBox
 * @see #selectedProperty()
 * @see #onSelectedProperty()
 * @see #onDeselectedProperty()
 * @see #PSEUDO_CLASS_SELECTED
 */
public interface Selectable extends EventTarget, Styleable {
    
    /**************************************************************************
     *                                                                        *
     * Properties                                                             *
     *                                                                        *
     *************************************************************************/
    
    /* --- Selected --- */
    /**
     * The control's selected property, which indicates whether the control is
     * in the selected state or not.
     * <p>
     * A control may be in the selected state for multiple reasons,
     * the user may have clicked on the control with the mouse, or a touch
     * event or key press could have triggered the control's selected state, or
     * if a developer programmatically invokes the {@link #select()} method.
     * 
     * @return the property that represents a control's selected state
     */
    ReadOnlyBooleanProperty selectedProperty();
    /**
     * Gets whether or not this control is in the selected state.
     * @return this control's selected state
     */
    boolean isSelected();
    
    
    /* --- On Selected --- */
    /**
     * The control's selected action, which is invoked whenever the control is
     * selected.
     * @return the property that represents the control's selected action,
     *         which is invoked whenever the control is selected
     */
    ObjectProperty<EventHandler<? super SelectionEvent>> onSelectedProperty();
    /**
     * Sets the control's on selected action event handler.
     * @param value - the control's new on selected action event handler
     */
    void setOnSelected(EventHandler<? super SelectionEvent> value);
    /**
     * Gets the control's on selected action event handler.
     * @return the control's on selected action event handler
     */
    EventHandler<? super SelectionEvent> getOnSelected();
    
    
    /* --- On Deselected --- */
    /**
     * The control's deselected action, which is invoked whenever the control
     * is deselected.
     * @return the property that represents the control's deselected action,
     *         which is invoked whenever the control is deselected
     */
    ObjectProperty<EventHandler<? super SelectionEvent>> onDeselectedProperty();
    /**
     * Sets the control's on deselected action event handler.
     * @param value - the control's new on deselected action event handler
     */
    void setOnDeselected(EventHandler<? super SelectionEvent> value);
    /**
     * Gets the control's on deselected action event handler.
     * @return the control's on deselected action event handler
     */
    EventHandler<? super SelectionEvent> getOnDeselected();
    
    
    /**************************************************************************
     *                                                                        *
     * Methods                                                                *
     *                                                                        *
     *************************************************************************/
    
    /** 
     * Puts this control into the selected state.
     * <p>
     * In most cases this method will set the {@code Selectable} instance to
     * selected and then fire a {@link SelectionEvent#SELECTED} event via,
     * <blockquote><pre>fireEvent(new SelectionEvent(this, null, SelectionEvent.SELECTED))</pre></blockquote>
     * but implementations are not required to do so.
     * <p>
     * 
     * @see #selectedProperty()
     * @see #onSelectedProperty()
     * @see javafx.scene.Node#fireEvent(javafx.event.Event) fireEvent(Event)
     */
    void select();
    
    /**
     * Takes this control out of the selected state.
     * <p>
     * In most cases this method will set the {@code Selectable} instance to
     * not selected and then fire a {@link SelectionEvent#DESELECTED} event
     * via,
     * <blockquote><pre>fireEvent(new SelectionEvent(this, null, SelectionEvent.DESELECTED))</pre></blockquote>
     * but implementations are not required to do so.
     * <p>
     * 
     * @see #selectedProperty()
     * @see #onDeselectedProperty()
     * @see javafx.scene.Node#fireEvent(javafx.event.Event) fireEvent(Event)
     */
    void deselect();
    
    /** 
     * Gets this {@code Selectable} as a {@link Node}.
     * @return this {@code Selectable} as a {@code Node}
     */
    Node getNode();
    
    
    /**************************************************************************
     *                                                                        *
     * CSS                                                                    *
     *                                                                        *
     *************************************************************************/
    
    /** The CSS {@code :selected} pseudo class. */
    static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    
    
}

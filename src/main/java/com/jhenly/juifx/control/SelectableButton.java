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

import com.jhenly.juifx.control.event.SelectionEvent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.event.EventHandler;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;


/**
 * A simple button control with a selected state.
 * <p>
 * This class extends {@code Button} and does not alter any of its public API.
 * 
 * @author Jonathan Henly
 * @see Selectable
 * @see Button
 * @since JuiFX 1.0
 */
public class SelectableButton extends Button implements Selectable {
    
    /***************************************************************************
     *                                                                         *
     * Constructor(s)                                                          *
     *                                                                         *
     **************************************************************************/
//                                                                                
    /**
     * Creates a {@code SelectableButton} instance, with an empty string for
     * its text, in the non-selected state.
     */
    public SelectableButton() {
        this(false);
    }
    
    /**
     * Creates a {@code SelectableButton} instance, with an empty string for
     * its text, in specified selected state.
     * @param selected - the button's selected state
     */
    public SelectableButton(boolean selected) {
        super();
        initialize(selected);
    }
    
    /**
     * Creates a {@code SelectableButton} instance with the specified text, in
     * the non-selected state.
     * @param text - the button's text
     */
    public SelectableButton(String text) {
        this(text, false);
    }
    
    /**
     * Creates a {@code SelectableButton} instance with the specified text, in
     * the specified selected state.
     * @param text - the button's text
     * @param selected - the button's selected state
     */
    public SelectableButton(String text, boolean selected) {
        super(text);
        initialize(selected);
    }
    
    /**
     * Creates a {SelectableButton} instance with the specified text and
     *  graphic, in the non-selected state.
     * @param text - the button's text
     * @param graphic - the button's graphic
     */
    public SelectableButton(String text, Node graphic) {
        this(text, graphic, false);
    }
    
    /**
     * Creates a {SelectableButton} instance with the specified text, graphic,
     * and selected state.
     * @param text - the button's text
     * @param graphic - the button's graphic
     * @param selected - the button's selected state
     */
    public SelectableButton(String text, Node graphic, boolean selected) {
        super(text, graphic);
        initialize(selected);
    }
    
    /**
     * Method used by all constructors.
     * <p>
     * Adds the default style class and sets selected.
     */
    private void initialize(boolean selected) {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        
        if (selected) { select(); }
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Properties                                                             *
     *                                                                        *
     **************************************************************************/
    
    /* --- Selected --- */
    /** {@inheritDoc} */
    @Override
    public final ReadOnlyBooleanProperty selectedProperty() { return selected.getReadOnlyProperty(); }
    private void setSelected(boolean value) { selected.set(value); }
    @Override
    public boolean isSelected() { return selected == null ? false : selected.get(); }
    private ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper(SelectableButton.this, "selected", false)
    {
        @Override
        protected void invalidated() { pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, get()); }
    };
    
    
    /* --- On Selected --- */
    /** {@inheritDoc} */
    @Override
    public final ObjectProperty<EventHandler<? super SelectionEvent>> onSelectedProperty() {
        // lazy instantiation
        if (onSelected == null) {
            onSelected = new ObjectPropertyBase<EventHandler<? super SelectionEvent>>()
            {
                @Override
                protected void invalidated() { setEventHandler(SelectionEvent.SELECTED, get()); }
                
                @Override
                public Object getBean() { return SelectableButton.this; }
                
                @Override
                public String getName() { return "onSelected"; }
            };
        }
        return onSelected;
    }
    @Override
    public EventHandler<? super SelectionEvent> getOnSelected() {
        return onSelected == null ? null : onSelectedProperty().get();
    }
    private ObjectProperty<EventHandler<? super SelectionEvent>> onSelected;
    
    
    /* --- On Deselected --- */
    /** {@inheritDoc} */
    @Override
    public final ObjectProperty<EventHandler<? super SelectionEvent>> onDeselectedProperty() {
        // lazy instantiation
        if (onDeselected == null) {
            onDeselected = new ObjectPropertyBase<EventHandler<? super SelectionEvent>>()
            {
                @Override
                protected void invalidated() { setEventHandler(SelectionEvent.DESELECTED, get()); }
                @Override
                public Object getBean() { return SelectableButton.this; }
                @Override
                public String getName() { return "onDeselected"; }
            };
        }
        return onDeselected;
    }
    @Override
    public EventHandler<? super SelectionEvent> getOnDeselected() {
        return onDeselected == null ? null : onDeselectedProperty().get();
    }
    private ObjectProperty<EventHandler<? super SelectionEvent>> onDeselected;
    
    
    /**************************************************************************
     *                                                                        *
     * Methods                                                                *
     *                                                                        *
     **************************************************************************/
    
    /**
     * Puts this button in the selected state if it's not disabled and it's not
     * already in the selected state.
     * <p>
     * If this button is not disabled or already selected, then this method
     * sets this button to selected and fires a {@link SelectionEvent} with
     * this button as the source and {@link SelectionEvent#SELECTED} as the
     * event type.
     */
    @Override
    public void select() {
        if (selectedOrDisabled()) { return; }
        
        setSelected(true);
        setFocusTraversable(false);
        fireEvent(new SelectionEvent(this, null, SelectionEvent.SELECTED));
    }
    
    /**
     * Takes this button out of the selected state if it's in the selected
     * state.
     * <p>
     * If this button is selected, then this method sets this button to not
     * selected and fires a {@link SelectionEvent} with this button as the
     * source and {@link SelectionEvent#DESELECTED} as the event type.
     */
    @Override
    public void deselect() {
        if (!isSelected()) { return; }
        
        setSelected(false);
        if (!isDisabled()) { setFocusTraversable(true); }
        fireEvent(new SelectionEvent(this, null, SelectionEvent.DESELECTED));
    }
    
    
    /**
     * If this button is not selected or disabled, then this method invokes
     * this button's {@link #select()} method and then calls
     * {@link Button#fire() super.fire()}.
     * @see #select()
     */
    @Override
    public void fire() {
        if (selectedOrDisabled()) { return; }
        
        select();
        super.fire();
    }
    
    /**
     * If this button is not selected or disabled, then this method invokes
     * {@link Button#arm() super.arm()}.
     */
    @Override
    public void arm() {
        if (selectedOrDisabled()) { return; }
        
        super.arm();
    }
    
    /**
     * If this button is not selected or disabled, then this method invokes
     * {@link Button#disarm() super.disarm()}.
     */
    @Override
    public void disarm() {
        if (selectedOrDisabled()) { return; }
        
        super.disarm();
    }
    
    /** Helper method which returns true if this button is selected or disabled. */
    private boolean selectedOrDisabled() {
        return isSelected() || isDisabled();
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/
    
    /** The default CSS identifier. */
    private static final String DEFAULT_STYLE_CLASS = "selectable-button";
    
    /** {@inheritDoc} */
    @Override
    public String getUserAgentStylesheet() {
        return SelectableButton.class.getResource("selectablebutton.css").toExternalForm();
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Accessibility handling                                                  *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        switch (attribute) {
            case SELECTED:
                return isSelected();
            default:
                return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
    
}

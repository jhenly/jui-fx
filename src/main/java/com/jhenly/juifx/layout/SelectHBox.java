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
package com.jhenly.juifx.layout;

import com.jhenly.juifx.control.Selectable;
import com.jhenly.juifx.control.event.SelectionEvent;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 * A simple {@link HBox} wrapper that handles the selected states of any {@link
 * Selectable} children.
 * <p>
 * This wrapper simply sits atop {@code HBox}, it does not alter any of {@code
 * HBox}'s public API or underlying functionality. As such any {@link Node} is
 * allowed to be contained by a {@code SelectHBox} instance, not just those
 * implementing {@link Selectable}. Though it is recommended that {@link HBox}
 * be used over this class, if this class' {@link Selectable} functionality
 * will not be used.
 * <p>
 * This class has one event handler which listens for a {@linkplain
 * SelectionEvent#SELECTED selected event} fired from a {@link Selectable}
 * child contained within this {@code SelectHBox}. Upon receiving a selected
 * event, the handler then calls {@link Selectable#deselect() deselect()} on
 * this {@code SelectHBox} instance's currently selected child {@code
 * Selectable}, if any. Lastly, the handler then sets this {@code SelectHBox}
 * instance's currently selected child to the event's {@code Selectable}
 * target.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * @see HBox
 * @see Selectable
 */
public class SelectHBox extends HBox {
    
    /**************************************************************************
     *                                                                        *
     * Property Defaults                                                      *
     *                                                                        *
     *************************************************************************/
    
    private static boolean DEFAULT_CONSUME_DE_SELECTED = true;
    
    
    /**************************************************************************
     *                                                                        *
     * Private Members                                                        *
     *                                                                        *
     *************************************************************************/
    
    private Selectable selected;
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    
    /** 
     * Creates a {@code SelectHBox} layout with no horizontal spacing between
     * children.
     */
    public SelectHBox() {
        super();
        initialize();
    }
    
    /**
     * Creates a {@code SelectHBox} layout with the specified horizontal
     * spacing between children.
     * @param spacing - the amount of horizontal space to separate children by
     */
    public SelectHBox(double spacing) {
        super(spacing);
        initialize();
    }
    
    /**
     * Creates a {@code SelectHBox} layout with no horizontal spacing between
     * children.
     * @param children - the initial set of children for this pane
     */
    public SelectHBox(Node... children) {
        this();
        getChildren().addAll(children);
    }
    
    /**
     * Creates a {@code SelectHBox} layout with the specified horizontal
     * spacing between children.
     * @param spacing - the amount of horizontal space to separate children by
     * @param children - the initial set of children for this pane
     */
    public SelectHBox(double spacing, Node... children) {
        this(spacing);
        getChildren().addAll(children);
    }
    
    /** 
     * Initialize method called by each constructor.
     * <p>
     * Adds the {@link SelectionEvent.SELECTED} and the {@link
     * SelectionEvent.DESELECTED} event handlers to this {@link SelectHBox}
     * instance.
     */
    private void initialize() {
        addEventHandler(SelectionEvent.DESELECTED, event -> {
            if (getConsumeDeselected()) { event.consume(); }
            
            if (event.getTarget() == selected) { selected = null; }
            
        });
        
        addEventHandler(SelectionEvent.SELECTED, event -> {
            if (getConsumeSelected()) { event.consume(); }
            
            Selectable oldSelected = selected;
            selected = (Selectable) event.getTarget();
            
            if (oldSelected != null) { oldSelected.deselect(); }
        });
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Properties                                                             *
     *                                                                        *
     *************************************************************************/
    
    /* --- Consume Selected --- */
    /**
     * Property indicating whether or not the container consumes {@link
     * SelectionEvent#SELECTED} events.
     * <p>
     * This property's value is {@code true} by default.
     * 
     * @return the property indicating whether or not the container should
     *         consume selected events
     * @see Selectable
     */
    public final BooleanProperty consumeSelectedProperty() {
        if (consumeSelected == null) {
            consumeSelected
                = new SimpleBooleanProperty(SelectHBox.this, "consumeSelected", DEFAULT_CONSUME_DE_SELECTED);
        }
        return consumeSelected;
    }
    /**
     * Sets whether or not the container should consume {@link
     * SelectionEvent#SELECTED} events.
     * 
     * @param value - whether or not the container should consume selected
     *        events
     */
    public final void setConsumeSelected(boolean value) { consumeSelectedProperty().set(value); }
    /**
     * Gets whether or not the container consumes {@link
     * SelectionEvent#SELECTED} events.
     * 
     * @return whether or not the container consumes selected events
     */
    public final boolean getConsumeSelected() {
        return consumeSelected == null ? DEFAULT_CONSUME_DE_SELECTED : consumeSelected.get();
    }
    private BooleanProperty consumeSelected;
    
    
    /* --- Consume Deselected --- */
    /**
     * Property indicating whether or not the container consumes {@link
     * SelectionEvent#DESELECTED} events.
     * <p>
     * This property's value is {@code true} by default.
     * 
     * @return the property indicating whether or not the container should
     *         consume deselected events
     * @see Selectable
     */
    public final BooleanProperty consumeDeselectedProperty() {
        if (consumeDeselected == null) {
            consumeDeselected
                = new SimpleBooleanProperty(SelectHBox.this, "consumeDeselected", DEFAULT_CONSUME_DE_SELECTED);
        }
        return consumeDeselected;
    }
    /**
     * Sets whether or not the container should consume {@link
     * SelectionEvent#DESELECTED} events.
     * 
     * @param value - whether or not the container should consume deselected
     *        events
     */
    public final void setConsumeDeselected(boolean value) { consumeDeselectedProperty().set(value); }
    /**
     * Gets whether or not the container consumes {@link
     * SelectionEvent#DESELECTED} events.
     * 
     * @return whether or not the container consumes deselected events
     */
    public final boolean getConsumeDeselected() {
        return consumeDeselected == null ? DEFAULT_CONSUME_DE_SELECTED : consumeDeselected.get();
    }
    private BooleanProperty consumeDeselected;
    
    
    /**************************************************************************
     *                                                                        *
     * Methods                                                                *
     *                                                                        *
     *************************************************************************/
    
    /** Deselects any currently managed selected {@link Selectable}. */
    public void clearSelected() {
        if (selected != null) { selected.deselect(); }
    }
    
    
}

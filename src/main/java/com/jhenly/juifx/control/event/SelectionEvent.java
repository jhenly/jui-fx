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
package com.jhenly.juifx.control.event;

import com.jhenly.juifx.control.Selectable;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * An {@link Event} representing a selection event. This event type
 * is used to represent a {@link com.jhenly.juifx.control.Selectable
 * Selectable} control being selected or deselected. Such as when a
 * {@link com.jhenly.juifx.control.SelectableButton SelectableButton} has been
 * selected or deselected.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * @see #SELECTED
 * @see #DESELECTED
 * @see Selectable
 */
public class SelectionEvent extends Event {
    private static final long serialVersionUID = 8018365731634919905L;
    
    /**************************************************************************
     *                                                                        *
     * Public Static API                                                      *
     *                                                                        *
     *************************************************************************/
    
    /** Common supertype for all selection event types. */
    public static final EventType<SelectionEvent> SELECTION_ANY = new EventType<SelectionEvent>(Event.ANY, "SELECTION");
    
    /** 
     * This event occurs when a {@link com.jhenly.juifx.control.Selectable
     * Selectable} control is
     * {@link com.jhenly.juifx.control.Selectable#select() selected}.
     */
    public static final EventType<SelectionEvent> SELECTED = new EventType<SelectionEvent>(SELECTION_ANY, "SELECTED");
    
    /**
     * This event occurs when a {@link com.jhenly.juifx.control.Selectable
     * Selectable} control is
     * {@link com.jhenly.juifx.control.Selectable#deselect() deselected}.
     */
    public static final EventType<SelectionEvent> DESELECTED
        = new EventType<SelectionEvent>(SELECTION_ANY, "DESELECTED");
    
    
    /**************************************************************************
     *                                                                        *
     * Constructors                                                           *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Constructs a new {@code SelectionEvent} with the specified event type.
     * <p>
     * The source and target of the event are set to
     * {@code NULL_SOURCE_TARGET}.
     * @param eventType - the type of {@code SelectionEvent}
     */
    public SelectionEvent(EventType<? extends SelectionEvent> eventType) { super(eventType); }
    
    /**
     * Constructs a new {@code SelectionEvent} with the specified event source,
     * target and event type.
     * <p>
     * If the specified source or target are set to {@code null}, then they are
     * replaced by the {@code NULL_SOURCE_TARGET} value.
     *
     * @param source - the source which sent the event
     * @param target - the target to associate with the event
     * @param eventType - the type of {@code SelectionEvent}
     */
    public SelectionEvent(Object source, EventTarget target, EventType<? extends SelectionEvent> eventType) {
        super(source, target, eventType);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public SelectionEvent copyFor(Object newSource, EventTarget newTarget) {
        return (SelectionEvent) super.copyFor(newSource, newTarget);
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public EventType<? extends SelectionEvent> getEventType() {
        return (EventType<? extends SelectionEvent>) super.getEventType();
    }
    
    /**
     * Gets a string representation of this {@code SelectionEvent}.
     * <p>
     * An example of the returned string representation follows:<pre>
     * "SelectionEvent [source = <i>&lt;Object&gt;</i>,  target = <i>&lt;EventTarget&gt;</i>, eventType = <i>&lt;EventType&lt;? extends SelectionEvent&gt;&gt;</i>, consumed = <i>&lt;boolean&gt;</i>]"
     * </pre>
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SelectionEvent [");
        
        sb.append("source = ").append(getSource());
        sb.append(", target = ").append(getTarget());
        sb.append(", eventType = ").append(getEventType());
        sb.append(", consumed = ").append(isConsumed());
        sb.append("]");
        
        return sb.toString();
    }
    
    
}

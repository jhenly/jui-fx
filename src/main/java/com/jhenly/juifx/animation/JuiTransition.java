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
package com.jhenly.juifx.animation;

import java.util.function.Consumer;

import com.jhenly.juifx.control.LambdaMultiplePropertyChangeListenerHandler;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;

/**
 * An abstract base class that contains the basic functionalities required by
 * all JuiFX transitions.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see JuiFillTransition
 * @see Transition
 */
public abstract class JuiTransition extends Transition {
    
    private static final Duration DEFAULT_DURATION = Duration.millis(200);
    
    private static final double EPSILON = 1e-12;
    
    /**************************************************************************
     *                                                                        *
     * Properties                                                             *
     *                                                                        *
     *************************************************************************/
    
    /**
     * The duration of this {@code JuiTransition}.
     * <p>
     * It is not possible to change the {@code duration} of a running
     * {@code JuiTransition}. If the value of {@code duration} is changed for a
     * running {@code JuiTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     * <p>
     * Note: While the unit of {@code duration} is a millisecond, the
     * granularity depends on the underlying operating system and will in
     * general be larger. For example animations on desktop systems usually run
     * with a maximum of 60fps which gives a granularity of ~17 ms.
     *
     * Setting duration to value lower than {@link Duration#ZERO} will result
     * in {@link IllegalArgumentException}.
     *
     * @defaultValue 200ms
     * 
     * @return this {@code JuiTransition} instance's duration property
     */
    public final ObjectProperty<Duration> durationProperty() {
        if (duration == null) {
            duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION)
            {
                @Override
                public void invalidated() {
                    try {
                        setCycleDuration(getDuration());
                    } catch (IllegalArgumentException e) {
                        if (isBound()) {
                            unbind();
                        }
                        set(getCycleDuration());
                        throw e;
                    }
                }
                @Override
                public Object getBean() { return JuiTransition.this; }
                @Override
                public String getName() { return "duration"; }
            };
        }
        return duration;
    }
    /**
     * Sets the duration of this {@code JuiTransition}.
     * @param value - the duration to set
     */
    public final void setDuration(Duration value) {
        if ((duration != null) || (!DEFAULT_DURATION.equals(value))) {
            durationProperty().set(value);
        }
    }
    /**
     * Gets the duration of this {@code JuiTransition} instance.
     * @return the duration of this {@code JuiTransition}
     */
    public final Duration getDuration() { return (duration == null) ? DEFAULT_DURATION : duration.get(); }
    private ObjectProperty<Duration> duration;
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /**
     * This method <strong>must</strong> be called on a {@link JuiTransition}
     * when it is no longer needed.
     * <p>
     * This method allows a {@code JuiTransition} to implement any logic
     * necessary to clean up itself after the {@code JuiTransition} is no
     * longer needed. It may be used to release native resources.
     * <p>
     * Though calling dispose twice has no effect, any other methods called
     * after dispose will exhibit undefined behavior.
     */
    public void dispose() {
        if (duration != null) { duration.unbind(); }
        
        // unhook listeners
        if (lambdaChangeListenerHandler != null) { lambdaChangeListenerHandler.dispose(); }
    }
    
    /**
     * Gets whether or not this {@link JuiTransition} is currently playing.
     * @return {@code true} if this transition is playing, otherwise
     *         {@code false}
     */
    public final boolean isPlaying() { return getStatus() == Animation.Status.RUNNING; }
    
    /**
     * Gets whether or not this {@link JuiTransition} is currently playing in
     * the forward direction, that is
     * {@code isPlaying() == true && (getRate() > 0.0) == true}.
     * @return {@code true} if this transition is playing in the forward
     *         direction, otherwise {@code false}
     */
    public final boolean isPlayingForward() { return isPlaying() && getRate() > 0.0; }
    
    /**
     * Gets whether or not this {@link JuiTransition} is currently playing in
     * the backward direction, that is
     * {@code isPlaying() == true && (getRate() < 0.0) == true}.
     * @return {@code true} if this transition is playing in the backward
     *         direction, otherwise {@code false}
     */
    public final boolean isPlayingBackward() { return isPlaying() && getRate() < 0.0; }
    
    /**
     * Gets whether or not this {@link JuiTransition} is currently stopped.
     * @return {@code true} if this transition is stopped, otherwise
     *         {@code false}
     */
    public final boolean isStopped() { return getStatus() == Animation.Status.STOPPED; }
    
    /**
     * Gets whether or not this {@link JuiTransition} is currently paused.
     * @return {@code true} if this transition is paused, otherwise
     *         {@code false}
     */
    public final boolean isPaused() { return getStatus() == Animation.Status.PAUSED; }
    
    /**
     * Gets whether or not this {@link JuiTransition} is at the start of the
     * transition.
     * @return {@code true} if this transition is at the start, otherwise
     *         {@code false}
     */
    public final boolean isAtStart() { return isStopped() && (getCurrentTime().equals(Duration.ZERO)); }
    
    /**
     * Gets whether or not this {@link JuiTransition} is at the end of the
     * transition.
     * @return {@code true} if this transition is at the end, otherwise
     *         {@code false}
     */
    public final boolean isAtEnd() { return isStopped() && (getCurrentTime().equals(getTotalDuration())); }
    
    
    /**************************************************************************
     *                                                                        *
     * Register Change Listeners Section                                      *
     *                                                                        *
     *************************************************************************/
    
    // used by register change listener
    private LambdaMultiplePropertyChangeListenerHandler lambdaChangeListenerHandler;
    
    /**
     * Subclasses can invoke this method to register that they want to listen
     * to property change events for the given property.
     * <p>
     * Registered {@link Consumer} instances will be executed in the order in
     * which they are registered.
     * <p>
     * <i>Method documentation and implementation copied from
     * {@code javafx.scene.control.SkinBase#registerChangeListener}.</i>
     * 
     * @param property - the property
     * @param consumer - the consumer
     */
    protected final void registerChangeListener(ObservableValue<?> property, Consumer<ObservableValue<?>> consumer) {
        if (lambdaChangeListenerHandler == null) {
            lambdaChangeListenerHandler = new LambdaMultiplePropertyChangeListenerHandler();
        }
        lambdaChangeListenerHandler.registerChangeListener(property, consumer);
    }
    
    /**
     * Unregisters all change listeners that have been registered using
     * {@link #registerChangeListener(ObservableValue, Consumer)} for the given
     * property.
     * <p>
     * The end result is that the given property is no longer
     * observed by any of the change listeners, but it may still have
     * additional listeners registered on it through means outside of
     * {@link #registerChangeListener(ObservableValue, Consumer)}.
     * <p>
     * If no consumers have been registered on this property, {@code null} will
     * be returned.
     * <p>
     * <i>Method documentation and implementation copied from
     * {@code javafx.scene.control.SkinBase#unregisterChangeListeners}.</i>
     * 
     * @param property - the property for which all listeners should be removed
     * @return a single chained {@link Consumer} consisting of all
     *         {@link Consumer consumers} registered through
     *         {@link #registerChangeListener(ObservableValue, Consumer)}
     */
    protected final Consumer<ObservableValue<?>> unregisterChangeListeners(ObservableValue<?> property) {
        if (lambdaChangeListenerHandler == null) { return null; }
        return lambdaChangeListenerHandler.unregisterChangeListeners(property);
    }
    
}

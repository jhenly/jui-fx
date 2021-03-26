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

import com.jhenly.juifx.control.Fillable;
import com.jhenly.juifx.control.applier.FillApplier;
import com.jhenly.juifx.control.skin.FillableSkin;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.util.Duration;


/**
 * An animated transition class containing the basic functionalities required
 * by {@link FillableSkin} instances.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public class JuiFillTransition extends Transition {
    
    private static final Duration DEFAULT_DURATION = Duration.millis(200);
    
    /**************************************************************************
     *                                                                        *
     * Private Members                                                        *
     *                                                                        *
     *************************************************************************/
    
    private FillApplier<?> cachedFillApplier;
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Creates a {@code JuiFillTransition} that acts on the specified target
     * {@code Fillable} instance's {@code FillApplier}.
     * @param applier - the target {@code Fillable} instance's
     *        {@code FillApplier}
     */
    public JuiFillTransition(FillApplier<?> applier) {
        if (applier == null) { throw new IllegalArgumentException("the 'applier' parameter cannot be null"); }
        
        cachedFillApplier = applier;
        
        // if playing backwards then reset fillable to pre-fill state on finish
        setOnFinished(e -> {
            if (getRate() < 0.0) { cachedFillApplier.resetFillable(); }
        });
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Properties                                                             *
     *                                                                        *
     *************************************************************************/
//    
    /**
     * The duration of this {@code JuiFillTransition}.
     * <p>
     * It is not possible to change the {@code duration} of a running
     * {@code JuiFillTransition}. If the value of {@code duration} is changed
     * for a running {@code JuiFillTransition}, the animation has to be stopped
     * and started again to pick up the new value.
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
     * @return this {@code JuiFillTransition} instance's duration property
     */
    public final ObjectProperty<Duration> durationProperty() { return duration; }
    /**
     * Sets the duration of this {@code JuiFillTransition}.
     * @param value - the duration to set
     */
    public final void setDuration(Duration value) { durationProperty().set(value); }
    /**
     * Gets the duration of this {@code JuiFillTransition} instance.
     * @return the duration of this {@code JuiFillTransition}
     */
    public final Duration getDuration() { return duration.get(); }
    
    private final ObjectProperty<Duration> duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION)
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
        public Object getBean() { return JuiFillTransition.this; }
        @Override
        public String getName() { return "duration"; }
    };
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /**
     * This method checks if the {@link Fillable} is able to be filled and if
     * so, calls {@link FillApplier#interpolateAndApply(double)}, otherwise it
     * simply returns.
     * <p>
     * The {@code Fillable} is able to be filled if the following conditions
     * hold true.
     * <ul>
     * <li>{@code fillable.isFillEnabled() == true}</li>
     * <li>{@code (fillable.getFill() != null) == true}</li>
     * </ul>
     * <p>
     * <b>Note:</b> this method must not be called by implementing classes
     * directly.
     * <p>
     * <b>Documentation from</b>
     * {@linkplain Transition#interpolate(double) interpolate(...)}
     * <b>in</b> {@linkplain Transition} <b>follows:</b>
     * <p>
     * {@inheritDoc}
     * 
     * @param frac - the current position in the animation
     */
    @Override
    protected void interpolate(double frac) { cachedFillApplier.interpolateAndApply(frac); }
    
    /**
     * This method allows a {@code JuiFillTransition} to implement any logic
     * necessary to clean up itself after the {@code JuiFillTransition} is no
     * longer needed, it may be used to release native resources.
     * <p>
     * Though calling dispose twice has no effect, any other methods called
     * after dispose will exhibit undefined behavior.
     */
    public void dispose() {
        if (cachedFillApplier == null) { return; }
        
        stop();
        if (duration.isBound()) { duration.unbind(); }
        setOnFinished(null);
        
        cachedFillApplier = null;
    }
    
    /**
     * Gets whether or not this {@link JuiFillTransition} is currently playing.
     * @return {@code true} if this transition is playing, otherwise
     *         {@code false}
     */
    public final boolean isPlaying() { return getStatus() == Animation.Status.RUNNING; }
    
    /**
     * Gets whether or not this {@link JuiFillTransition} is currently playing in
     * the forward direction, that is
     * {@code isPlaying() == true && (getRate() > 0.0) == true}.
     * @return {@code true} if this transition is playing in the forward
     *         direction, otherwise {@code false}
     */
    public final boolean isPlayingForward() { return isPlaying() && getRate() > 0.0; }
    
    /** Plays the transition forward at a rate of {@code 1.0}. */
    public final void playForward() {
        if (isAtEnd() || isPlayingForward()) { return; }
        
        // play forward if at start, rate is <= 0 or transition isn't playing
        setRate(1.0);
        play();
    }
    
    /**
     * Gets whether or not this {@link JuiFillTransition} is currently playing in
     * the backward direction, that is
     * {@code isPlaying() == true && (getRate() < 0.0) == true}.
     * @return {@code true} if this transition is playing in the backward
     *         direction, otherwise {@code false}
     */
    public final boolean isPlayingBackward() { return isPlaying() && getRate() < 0.0; }
    
    /** Plays the transition backward at a rate of {@code -1.0}. */
    public final void playBackward() {
        if (isAtStart() || isPlayingBackward()) { return; }
        
        // play backward if at end, rate is >= 0 or transition isn't playing
        setRate(-1.0);
        play();
    }
    
    /**
     * Gets whether or not this {@link JuiFillTransition} is currently stopped.
     * @return {@code true} if this transition is stopped, otherwise
     *         {@code false}
     */
    public final boolean isStopped() { return getStatus() == Animation.Status.STOPPED; }
    
    /**
     * Gets whether or not this {@link JuiFillTransition} is currently paused.
     * @return {@code true} if this transition is paused, otherwise
     *         {@code false}
     */
    public final boolean isPaused() { return getStatus() == Animation.Status.PAUSED; }
    
    /**
     * Gets whether or not this {@link JuiFillTransition} is at the start of the
     * transition.
     * @return {@code true} if this transition is at the start, otherwise
     *         {@code false}
     */
    public final boolean isAtStart() { return isStopped() && (getCurrentTime().equals(Duration.ZERO)); }
    
    /**
     * Gets whether or not this {@link JuiFillTransition} is at the end of the
     * transition.
     * @return {@code true} if this transition is at the end, otherwise
     *         {@code false}
     */
    public final boolean isAtEnd() { return isStopped() && (getCurrentTime().equals(getTotalDuration())); }
    
    /**
     * Goes to the end of this fill transition, sets the {@code Fillable}
     * instance's fill to its fill-to.
     */
    public final void jumpToEnd() {
        if (isAtEnd()) { return; }
        
        stop();
        jumpTo(getTotalDuration());
        setRate(-1.0);
        
        cachedFillApplier.interpolateAndApply(1.0);
    }
    
    /**
     * Goes to the start of this fill transition, resets the {@code Fillable}
     * instance to its pre-fill state.
     */
    public final void jumpToStart() {
        if (isAtStart()) { return; }
        
        stop();
        jumpTo(Duration.ZERO);
        setRate(1.0);
        
        cachedFillApplier.resetFillable();
    }
    
    
}

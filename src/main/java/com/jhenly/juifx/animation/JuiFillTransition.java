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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.jhenly.juifx.control.Fillable;
import com.jhenly.juifx.fill.Fill;

import javafx.animation.Transition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

/**
 * An abstract class containing the basic functionalities required by all JuiFX
 * fill transitions, such as {@link SingleFillTransition} and
 * {@link MultiFillTransition}. 
 * <p>
 * <h3 id='bg-fill-cache'>BackgroundFill Cache</h3>
 * This class keeps a static cache of {@link BackgroundFill} instances to
 * improve transition performance. The cache is enabled by default and is used
 * by all subclasses. The size of the cache can be queried via 
 * {@link #getBgFillCacheSize()}. If for some reason the cache is found to be
 * hurting performance, then the cache can be disabled via
 * {@link #disableBgFillCache()} and/or cleared via
 * {@link #clearBgFillCache()}. The enabled state of the cache can be queried
 * via {@link #isBgFillCacheEnabled()}. If the cache is disabled it can be
 * re-enabled via {@link #enableBgFillCache()}.
 * <p>
 * If it is known that no instances of {@link JuiFillTransition} are in use and
 * no more are needed, then clearing the cache would be beneficial.
 * <p>
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see SingleFillTransition
 * @see MultiFillTransition
 * @see JuiTransition
 *
 */
public abstract class JuiFillTransition extends JuiTransition {
    
    /**************************************************************************
     *                                                                        *
     * Private Members                                                        *
     *                                                                        *
     *************************************************************************/
    
    protected final ObservableBooleanValue isFillable;
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Sole constructor used by implementing classes.
     * @param fillable - the target of this fill transition
     */
    protected JuiFillTransition(Fillable fillable) {
        Objects.requireNonNull(fillable, "fillable cannot be null");
        
        setFillable(fillable);
        
        isFillable = new BooleanBinding()
        {
            {
                bind(fillable.fillEnabledProperty(), fillable.fillProperty());
            }
            
            @Override
            protected boolean computeValue() {
                if (!fillable.isFillEnabled()) { return false; }
                
                final Fill fill = fillable.getFill();
                if (fill == null || !fill.hasFillSpans()) { return false; }
                
                return true;
            }
        };
        
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Properties                                                             *
     *                                                                        *
     *************************************************************************/
    
    /**
     * The fillable property which represents the {@link Fillable} that this
     * {@link JuiFillTransition} is acting on.
     * <p>
     * This property is guaranteed to not hold a {@code null} value, prior to
     * any calls to {@link #dispose()}.
     * 
     * @return the fillable property of this {@link JuiFillTransition}
     */
    public final ReadOnlyObjectProperty<Fillable> fillableProperty() {
        return fillable;
    }
    private void setFillable(Fillable value) { fillable.set(value); }
    /**
     * Gets the {@link Fillable} that this {@link JuiFillTransition} is
     * acting on.
     * <p>
     * This method is guaranteed not to return {@code null} if called prior to
     * any calls to {@link #dispose()}.
     * 
     * @return the fillable associated with this fill transition
     */
    public final Fillable getFillable() { return fillable.get(); }
    private ObjectProperty<Fillable> fillable = new SimpleObjectProperty<Fillable>(JuiFillTransition.this, "fillable")
    {
        @Override
        protected void invalidated() {
            Fillable value = get();
            if (value != null) {
                // remove duration binding on any old fillable
                durationProperty().unbind();
                // bind this transition's duration with the new fillable's
                durationProperty().bind(value.fillDurationProperty());
            }
        }
    };
    
    
    /**************************************************************************
     *                                                                        *
     * Abstract API                                                           *
     *                                                                        *
     *************************************************************************/
    
    /**
     * This method checks if the {@link Fillable} is able to be filled and if
     * so, calls {@link #interpolateSpans(double, List)}, otherwise it simply
     * returns.
     * <p>
     * The {@code Fillable} is able to be filled if the following conditions
     * hold true.
     * <ul>
     * <li>{@code fillable.isFillEnabled() == true}</li>
     * <li>{@code (fillable.getFill() != null) == true}</li>
     * <li>{@code (fillable.getFill().getFillSpans() != null) == true}</li>
     * <li>{@code fillable.getFill().getFillSpans().isEmpty() == false}</li>
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
    protected final void interpolate(double frac) {
        if (isFillable.get()) { interpolateFill(frac); }
    }
    
    /**
     * Fills the {@link Fillable} from interpolating a fill span with a
     * specified current position in the animation.
     * <p>
     * <b>Note:</b> this method must not be called by implementing classes
     * directly.
     * 
     * @param frac - the current position in the animation
     */
    protected abstract void interpolateFill(final double frac);
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /** {@inheritDoc}
     * <p>
     * <b>Note:</b>
     * <ul>
     * <li>this method is a no-op if the fillable is not fillable.</li>
     * </ul>
     */
    @Override
    public void play() {
        if (isFillable.get()) { super.play(); }
    }
    
    /** 
     * {@inheritDoc}
     * <p>
     * <b>Note:</b>
     * <ul>
     * <li>this method is a no-op if the fillable is not fillable.</li>
     * </ul>
     */
    @Override
    public void jumpTo(Duration time) {
        if (isFillable.get()) { super.jumpTo(time); }
    }
    
    /**
     * This method <strong>must</strong> be called on a
     * {@code JuiFillTransition} when it is no longer needed.
     * <p>
     * This method allows a {@code JuiFillTransition} to implement any logic
     * necessary to clean up itself after the {@code JuiFillTransition} is no
     * longer needed. It may be used to release native resources.
     * <p>
     * Though calling dispose twice has no effect, any other methods called
     * after dispose will exhibit undefined behavior.
     */
    @Override
    public void dispose() {
        super.dispose();
        
        if (fillable != null) {
            fillable.unbind();
            fillable.set(null);
            fillable = null;
        }
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Public Static API                                                      *
     *                                                                        *
     *************************************************************************/
    
    
    /**************************************************************************
     *                                                                        *
     * BackgroundFill Cache Implementation                                    *
     *                                                                        *
     *************************************************************************/
    
    /** Keeps track of the disabled status of the cache. */
    private static boolean cacheIsDisabled = false;
    
    /**
     * Disables the caching of {@link BackgroundFill} instances.
     * <p>
     * Calling this method when caching is already disabled has no effect.
     * 
     * @see JuiFillTransition BackgroundFill Cache
     */
    public static final void disableBgFillCache() { cacheIsDisabled = true; }
    
    /**
     * Enables the caching of {@link BackgroundFill} instances.
     * <p>
     * Calling this method when caching is already enabled has no effect.
     * 
     * @see JuiFillTransition BackgroundFill Cache
     */
    public static final void enableBgFillCache() { cacheIsDisabled = false; }
    
    /**
     * Gets whether or not the cache of {@link BackgroundFill} instances is
     * enabled or not.
     * 
     * @return {@code true} if the cache is enabled, otherwise {@code false}
     * @see JuiFillTransition BackgroundFill Cache
     */
    public static final boolean isBgFillCacheEnabled() { return !cacheIsDisabled; }
    
    /**
     * Gets the number of {@link BackgroundFill} instances in the cache.
     * 
     * @return the number of background fills in the cache
     * @see JuiFillTransition BackgroundFill Cache
     */
    public static final int getBgFillCacheSize() { return BackgroundFillCache.getInstance().size(); }
    
    /**
     * Clears the cache of {@link BackgroundFill} instances.
     * @see JuiFillTransition BackgroundFill Cache
     */
    public static final void clearBgFillCache() {
        BackgroundFillCache.getInstance().clear();
    }
    
    /**
     * Gets a {@link BackgroundFill} associated with the specified fill,
     * radii and insets, from the cache of background fills.
     * <p>
     * If the cache does not contain an associated {@code BackgroundFill} then
     * one will be created, stored in the cache and then returned. If the cache
     * is disabled then this method simply creates and returns a new
     * {@code BackgroundFill} from the specified parameters.
     * 
     * @param fill - the background fill's fill
     * @param radii - the background fill's radii
     * @param insets - the background fill's insets
     * 
     * @return a {@code BackgroundFill} from the cache, or a newly created
     *         one
     *         
     * @see JuiFillTransition JuiFillTransition - BackgroundFill Cache
     * @see BackgroundFillCache
     */
    protected static final BackgroundFill getBgFillFromCache(Paint fill, CornerRadii radii, Insets insets) {
        // if cache is disabled then just create a new bg fill
        if (cacheIsDisabled) { return new BackgroundFill(fill, radii, insets); }
        
        // if cache is enabled then try to get a bg fill from it
        return BackgroundFillCache.getInstance().get(fill, radii, insets);
    }
    
    
    /** 
     * Used to cache {@link BackgroundFill} instances requested from
     * interpolate methods.
     * 
     * @author Jonathan Henly
     * @since JuiFX 1.0
     * 
     * @see JuiFillTransition BackgroundFill Cache
     * @see JuiFillTransition#getBgFillFromCache(Paint, CornerRadii, Insets)
     */
    protected static final class BackgroundFillCache {
        
        // lazy, thread-safe instantiation
        private static class Holder {
            static final BackgroundFillCache INSTANCE = new BackgroundFillCache();
        }
        private static BackgroundFillCache getInstance() { return Holder.INSTANCE; }
        
        // the background fill cache
        private static Map<Integer, BackgroundFill> cache;
        
        /** Called once by getInstance() when it returns Holder.INSTANCE. */
        private BackgroundFillCache() {
            cache = new HashMap<Integer, BackgroundFill>();
        }
        
        /**
         * Gets a {@link BackgroundFill} associated with the specified fill,
         * radii and insets, from this cache.
         * <p>
         * If this cache does not contain an associated {@code BackgroundFill}
         * then this method creates a new one and stores it in this cache.
         * 
         * @param fill - the background fill's fill
         * @param radii - the background fill's radii
         * @param insets - the background fill's insets
         * 
         * @return a {@code BackgroundFill} in this cache, or a newly created
         *         one
         */
        protected BackgroundFill get(Paint fill, CornerRadii radii, Insets insets) {
            Integer hash = 31 * (31 * (31 * fill.hashCode() + radii.hashCode()) + insets.hashCode());
            
            BackgroundFill bgFill = cache.get(hash);
            if (bgFill == null) {
                bgFill = new BackgroundFill(fill, radii, insets);
                cache.put(hash, bgFill);
            }
            
            return bgFill;
        }
        
        /**
         * Gets the size of the cache.
         * @return the size of the cache
         */
        protected int size() { return cache.size(); }
        
        /** Clears the cache of {@link BackgroundFill} instances. */
        protected void clear() {
            if (cache != null) { cache.clear(); }
        }
        
    } // class BackgroundFillCache
    
}

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
package com.jhenly.juifx.animation;

import java.util.ArrayList;
import java.util.List;

import com.jhenly.juifx.control.Fillable;

import impl.com.jhenly.juifx.fill.FillSpan;
import javafx.animation.FillTransition;
import javafx.scene.layout.BackgroundFill;


/**
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see JuiTransition
 * @see MultiFillTransition
 * @see FillTransition
 */
public class SingleFillTransition extends JuiFillTransition {
    
    /**************************************************************************
     *                                                                        *
     * Private Members                                                        *
     *                                                                        *
     *************************************************************************/
    
    private List<BackgroundFill> cachedFills;
    private FillSpan cachedSpan;
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Constructs a {@code SingleFillTransition} on a specified
     * {@link Fillable}.
     * 
     * @param control - the {@code Fillable} to fill
     */
    public SingleFillTransition(Fillable control) {
        super(control);
        
//        final Background background = control.getBackground();
//        if (background != null && background.getFills() != null) {
//            cachedFills = background.getFills();
//        }
//        
//        final Fill fill = control.getFill();
//        if (fill != null && fill.hasFillSpans()) {
//            // single fill transition, so we just want the first span
//            cachedSpan = fill.getFillSpan(0);
//        }
//        
//        setFillListener(control);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        stop();
        removeFillListener(getFillable());
        cachedSpan = null;
        
        super.dispose();
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Abstract Implementation                                                *
     *                                                                        *
     *************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    protected void interpolateFill(final double frac) {
        getFillable().getFillApplier().interpolateAndApply(frac);
//        final Fillable fable = getFillable();
//        final Background bg = fable.getBackground();
//        if (bg == null || cachedFills == null) { return; }
//        
//        // interpolate fill span and replace innermost bg fill with fill span
//        final List<BackgroundFill> interpFills = interpolateFillSpanAndReplace(frac, cachedFills);
//        
//        // no reason to create a new background if bg fills didn't change
//        // if (bgFills == interpFills) { return; }
//        
//        // update fillable's background with the interpolated fill
//        fable.setBackground(new Background(interpFills, bg.getImages()));
    }
    
    /**
     * Interpolates the passed in span and replaces the innermost background
     * fill with interpolated fill span.
     * 
     * @param frac - the current position in the animation
     * @param span - the single fill span to interpolate
     * @param bgFills - the fillable's list of background fills
     * @return a list containing the interpolated background fills
     */
    private List<BackgroundFill> interpolateFillSpanAndReplace(final double frac, final List<BackgroundFill> bgFills) {
        
        // bgFills list is unmodifiable, we need a modifiable list
        final List<BackgroundFill> tmpFills = new ArrayList<>(bgFills);
        
        // get the innermost background fill
        final BackgroundFill inner = tmpFills.get(tmpFills.size() - 1);
        
        // get existing or new bg fill from cache, replace fill, keep radii and
        // insets
        final BackgroundFill newBgFill =
        getBgFillFromCache(cachedSpan.interpolate(frac), inner.getRadii(), inner.getInsets());
        
        // replace innermost bg fill with the interpolated fill
        tmpFills.set(tmpFills.size() - 1, newBgFill);
        
        return tmpFills;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Private Implementation                                                 *
     *                                                                        *
     *************************************************************************/
    
    /** Helper that removes the fillable's fill listener. */
    private void removeFillListener(Fillable fillable) {
//        unregisterChangeListeners(fillable.fillProperty());
    }
    
    /** Helper that sets the fillable's fill listener. */
    private void setFillListener(Fillable fillable) {
        
//        registerChangeListener(fillable.fillProperty(), o -> {
//            boolean wasPlaying = isPlaying();
//            stop();
//            
//            final Fill fill = fillable.getFill();
//            // we just we want the first fill
//            cachedSpan = (fill != null && fill.hasFillSpans()) ? fill.getFillSpan(0) : null;
//            
//            if (wasPlaying && cachedSpan != null) { play(); }
//        });
    
    }
    
}

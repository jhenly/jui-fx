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
import javafx.scene.layout.BackgroundFill;


/**
 * Handles multiple fill transitions on a {@link Fillable}.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see SingleFillTransition
 * @see JuiTransition
 */
public class MultiFillTransition extends JuiFillTransition {
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    private List<BackgroundFill> cachedFills;
    private List<FillSpan> cachedSpans;
    
    /**
     * Constructs a {@code MultiFillTransition} on a specified {@link Fillable}
     * control.
     * 
     * @param control - the {@code Fillable} control to multi fill
     */
    public MultiFillTransition(Fillable control) {
        super(control);
        
//        final Background background = control.getBackground();
//        if (background != null && background.getFills() != null) {
//            cachedFills = background.getFills();
//        }
//        
//        final Fill fill = control.getFill();
//        if (fill != null && fill.hasFillSpans()) {
//            cachedSpans = fill.getFillSpans();
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
        cachedSpans = null;
        
        super.dispose();
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Abstract Implementation                                               *
     *                                                                        *
     *************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    protected void interpolateFill(double frac) {
        getFillable().getFillApplier().interpolateAndApply(frac);
//        final Fillable fable = getFillable();
//        final Background bg = fable.getBackground();
//        if (bg == null || cachedFills == null) { return; }
//        
//        // interpolate fill spans and replace innermost bg fills with spans
//        final List<BackgroundFill> interpFills = interpolateFillSpansAndReplace(frac, cachedFills);
//        
//        // no reason to create a new background if bg fills didn't change
//        // if (bg.getFills() == interpFills) { return; }
//        
//        // update fillable's background with the interpolated fill
//        fable.setBackground(new Background(interpFills, bg.getImages()));
    }
    
    /**
     * Interpolates the passed in fill spans and replaces the innermost
     * background fills with the interpolated fill spans.
     * 
     * @param frac - the current position in the animation
     * @param spans - the fill spans to interpolate
     * @param bgFills - the fillable's list of background fills
     * @return a list containing the interpolated background fills
     */
    private List<BackgroundFill> interpolateFillSpansAndReplace(final double frac, final List<BackgroundFill> bgFills) {
        final List<BackgroundFill> newBgFills = new ArrayList<>(bgFills);
        
        final int bgSize = newBgFills.size();
        final int csSize = cachedSpans.size();
        final int minSize = Math.min(bgSize, csSize);
        
        for (int i = 0; i < minSize; i++) {
            final BackgroundFill bgFill = newBgFills.get((bgSize - 1) - i);
            final FillSpan span = cachedSpans.get((csSize - 1) - i);
            
            // get new bg fill from cache, replace fill, keep radii and insets
            final BackgroundFill newBgFill =
            getBgFillFromCache(span.interpolate(frac), bgFill.getRadii(), bgFill.getInsets());
            
            newBgFills.set((bgSize - 1) - i, newBgFill);
        }
        
        return newBgFills;
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
//            cachedSpans = (fill != null && fill.hasFillSpans()) ? fill.getFillSpans() : null;
//            
//            if (wasPlaying && cachedSpans != null) { play(); }
//        });
//        
    }
    
}

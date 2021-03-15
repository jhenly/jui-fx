package impl.com.jhenly.juifx.fill;

import static javafx.animation.Interpolator.EASE_BOTH;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;


/**
 * A {@code GradientFillSpan} is a specialized {@code FillSpan} where either
 * <i>fill-from</i> or <i>fill-to</i>, or both, are instances of
 * {@link LinearGradient} or {@link RadialGradient}.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * 
 * @see FillSpan
 * @see LinearGradient
 * @see RadialGradient
 */
abstract class GradientFillSpan<T extends Paint, E extends Paint> extends FillSpan {
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
//                                                                               
    /**
     * Creates a {@code FillSpan} with the specified fill-from and fill-to
     * {@code Paint} instances.
     * <p>
     * GradientFillSpan instances are only created via
     * {@link FillSpan#of(Paint, Paint)}. We don't have to worry about the
     * possibility of a special identifier because a {@code GradientFillSpan}
     * is created when a {@code FillSpan} replacer replaces the special
     * identifier(s) in a {@code FillSpan}.
     * 
     * @param from - the fill-from paint
     * @param to - the fill-to paint
     */
    GradientFillSpan(Paint from, Paint to) { super(from, to); }
    
    /**
     * Creates a {@code GradientFillSpan} with fill-from and fill-to set to the
     * same specified {@code Paint} instance.
     * <p>
     * GradientFillSpan instances are only created via
     * {@link FillSpan#of(Paint, Paint)}. We don't have to worry about the
     * possibility of a special identifier because a {@code GradientFillSpan}
     * is created when a {@code FillSpan} replacer replaces the special
     * identifier(s) in a {@code FillSpan}.
     * 
     * @param same - the fill-from and fill-to paint
     */
    GradientFillSpan(Paint same) { super(same); }
    
    
    /**************************************************************************
     *                                                                        *
     * Public API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public T from() { return (T) from; }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public E to() { return (E) to; }
    
    /** {@inheritDoc} */
    @Override
    boolean equals(FillSpan that) {
        if (!(that instanceof GradientFillSpan)) { return false; }
        
        if (from.getClass() != that.from.getClass()) { return false; }
        if (to.getClass() != that.to.getClass()) { return false; }
        
        return from.equals(that.from) && to.equals(that.to);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Static of* Methods                                                     *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a {@code FillSpan} with the specified fill-from and fill-to
     * {@code Paint} instances.
     * <p>
     * GradientFillSpan instances are only created via
     * {@link FillSpan#of(Paint, Paint)}. We don't have to worry about the
     * possibility of a special identifier because a {@code GradientFillSpan}
     * is created when a {@code FillSpan} replacer replaces the special
     * identifier(s) in a {@code FillSpan}.
     * 
     * @param from - the fill-from paint
     * @param to - the fill-to paint
     * @param fromIsGradient - whether or not fill-from is a gradient
     * @param toIsGradient - whether or not fill-to is a gradient
     * 
     * @return a {@code FillSpan} instance with the specified fill-from and
     *         fill-to {@code Paint} instances
     */
    static final FillSpan of(Paint from, Paint to, boolean fromIsGradient, boolean toIsGradient) {
        
        /* Caution - smelly 'if-getClass()' code follows. */
        
        if (fromIsGradient && toIsGradient) {
            
            if (from.getClass() == LinearGradient.class) {
                
                if (to.getClass() == LinearGradient.class) {
                    return FillSpan
                        .getFromCache(LinearToLinearFillSpan.ofLinear((LinearGradient) from, (LinearGradient) to));
                    
                } else {
                    // 'to' is a RadialGradient, use RadialToRadialFillSpan since linear to radial
                    // is not possible
                    RadialGradient convertedFrom =
                    newRadialGradient((RadialGradient) to, ((LinearGradient) from).getStops());
                    
                    return FillSpan.getFromCache(RadialToRadialFillSpan.ofRadial(convertedFrom, (RadialGradient) to));
                }
                
            } else {
                
                // 'from' is a RadialGradient
                if (to.getClass() == LinearGradient.class) {
                    // use LinearToLinearFillSpan since radial to linear is not possible
                    LinearGradient convertedFrom =
                    newLinearGradient((LinearGradient) to, ((RadialGradient) from).getStops());
                    
                    return FillSpan.getFromCache(LinearToLinearFillSpan.ofLinear(convertedFrom, (LinearGradient) to));
                    
                } else {
                    
                    // 'to' is a RadialGradient
                    return FillSpan
                        .getFromCache(RadialToRadialFillSpan.ofRadial((RadialGradient) from, (RadialGradient) to));
                }
                
            }
            
        } else if (fromIsGradient) {
            // 'from' is a gradient and 'to' is a color
            if (from.getClass() == LinearGradient.class) {
                return FillSpan.getFromCache(new LinearToColorFillSpan(from, to));
            } else {
                // 'from' is a RadialGradient
                return FillSpan.getFromCache(new RadialToColorFillSpan(from, to));
            }
            
        } else {
            // 'from' is a color and 'to' is a gradient
            if (to.getClass() == LinearGradient.class) {
                return FillSpan.getFromCache(new ColorToLinearFillSpan(from, to));
            } else {
                // 'to' is a RadialGradient
                return FillSpan.getFromCache(new ColorToRadialFillSpan(from, to));
            }
        }
        
        
    } // static FillSpan ofGradient(...)
    
    /**
     * Gets a {@code FillSpan} with fill-from and fill-to set to the
     * same specified {@code Paint} instance.
     * <p>
     * GradientFillSpan instances are only created via
     * {@link FillSpan#of(Paint, Paint)}. We don't have to worry about the
     * possibility of a special identifier because a {@code GradientFillSpan}
     * is created when a {@code FillSpan} replacer replaces the special
     * identifier(s) in a {@code FillSpan}.
     * 
     * @param same - the fill-from and fill-to paint
     * @param spec - the from-to color-gradient specifier
     */
    static final FillSpan of(Paint same) {
        // don't need to worry about same.getClass() == Color.class,
        // FillSpan.of(...) takes care of that
        
        if (same.getClass() == LinearGradient.class) {
            return FillSpan.getFromCache(new LinearToLinearFillSpan(same));
        } else {
            return FillSpan.getFromCache(new RadialToRadialFillSpan(same));
        }
    }
    
    
    /**************************************************************************
     *                                                                        *
     * New Gradient Helpers                                                   *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Convenience method that creates a new LinearGradient from an old 
     * LinearGradient with new stops.
     */
    private static LinearGradient newLinearGradient(LinearGradient old, List<Stop> stops) {
        return new LinearGradient(old.getStartX(), old.getStartY(), old.getEndX(), old.getEndY(), old.isProportional(),
            old.getCycleMethod(), stops);
    }
    
    /**
     * Convenience method that creates a new RadialGradient from an old 
     * RadialGradient with new stops.
     */
    private static RadialGradient newRadialGradient(RadialGradient old, List<Stop> stops) {
        return new RadialGradient(old.getFocusAngle(), old.getFocusDistance(), old.getCenterX(), old.getCenterY(),
            old.getRadius(), old.isProportional(), old.getCycleMethod(), stops);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Stop Interpolate Methods                                               *
     *                                                                        *
     *************************************************************************/
    
    /** Convenience method that interpolates a double. */
    private static double interpolateDouble(double start, double end, double frac) {
        return start == end ? start : EASE_BOTH.interpolate(start, end, frac);
    }
    
    private static Stop interpolateStop(Color from, Stop to, double frac) {
        return new Stop(to.getOffset(), from.interpolate(to.getColor(), frac));
    }
    
    private static Stop interpolateStop(Stop from, Color to, double frac) {
        return new Stop(from.getOffset(), from.getColor().interpolate(to, frac));
    }
    
    private static Stop interpolateStop(Stop from, Stop to, double frac) {
        final Color color = from.getColor().interpolate(to.getColor(), frac);
        final double offset = interpolateDouble(from.getOffset(), to.getOffset(), frac);
        
        return new Stop(offset, color);
    }
    
    private static List<Stop> interpolateStops(Color from, List<Stop> to, double frac) {
        return IntStream.range(0, to.size()).mapToObj(i -> interpolateStop(from, to.get(i), frac))
            .collect(Collectors.toList());
    }
    
    private static List<Stop> interpolateStops(List<Stop> from, Color to, double frac) {
        return IntStream.range(0, from.size()).mapToObj(i -> interpolateStop(from.get(i), to, frac))
            .collect(Collectors.toList());
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Color To Linear Gradient                                               *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Indicates that {@code from} is a {@link Color} and {@code to} is a
     * {@link LinearGradient}.
     */
    private static final class ColorToLinearFillSpan extends GradientFillSpan<Color, LinearGradient> {
        private ColorToLinearFillSpan(Paint from, Paint to) {
            super(from, to);
            
            hash = 31 * hash + 1;
        }
        
        @Override
        public Paint interpolateImpl(double frac) {
            return newLinearGradient(to(), interpolateStops(from(), to().getStops(), frac));
        }
    } // class ColorToLinearFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Linear To Color Gradient                                               *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Indicates that {@code from} is a {@link LinearGradient} and
     * {@code to} is a {@link Color}.
     */
    private static final class LinearToColorFillSpan extends GradientFillSpan<LinearGradient, Color> {
        private LinearToColorFillSpan(Paint from, Paint to) {
            super(from, to);
            
            hash = 31 * hash + 3;
        }
        
        @Override
        public Paint interpolateImpl(double frac) {
            return newLinearGradient(from(), interpolateStops(from().getStops(), to(), frac));
        }
    } // class LinearToColorFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Color To Radial Gradient                                               *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Indicates that {@code from} is a {@link Color} and {@code to} is a
     * {@link RadialGradient}.
     */
    private static final class ColorToRadialFillSpan extends GradientFillSpan<Color, RadialGradient> {
        private ColorToRadialFillSpan(Paint from, Paint to) {
            super(from, to);
            
            hash = 31 * hash + 2;
        }
        
        @Override
        public Paint interpolateImpl(double frac) {
            return newRadialGradient(to(), interpolateStops(from(), to().getStops(), frac));
        }
    } // class ColorToRadialFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Radial To Color Gradient                                               *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Indicates that {@code from} is a {@link RadialGradient} and {@code to}
     * is a {@link Color}.
     */
    private static final class RadialToColorFillSpan extends GradientFillSpan<RadialGradient, Color> {
        private RadialToColorFillSpan(Paint from, Paint to) {
            super(from, to);
            
            hash = 31 * hash + 6;
        }
        
        @Override
        public Paint interpolateImpl(double frac) {
            return newRadialGradient(from(), interpolateStops(from().getStops(), to(), frac));
        }
    } // class RadialToColorFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Linear To Linear Gradient                                              *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Indicates that {@code from} is a {@link LinearGradient} and
     * {@code to} is a {@link LinearGradient}.
     */
    private static class LinearToLinearFillSpan extends GradientFillSpan<LinearGradient, LinearGradient> {
        private final boolean uniform;
        
        private LinearToLinearFillSpan(LinearGradient from, LinearGradient to) {
            super(from, to);
            // 'hash' is preset in FillSpan
            hash = 31 * hash + 4;
            uniform = calculateUniform(from, to);
        }
        
        private LinearToLinearFillSpan(Paint same) {
            super(same);
            // 'hash' is preset in FillSpan
            hash = 31 * hash + 4;
            // 'from' == 'to' so this is uniform
            uniform = true;
        }
        
        /** 
         * A LinearToLinearFillSpan is uniform if 'from' and 'to' are the same
         * except for their 'stops'.
         */
        private static boolean calculateUniform(LinearGradient from, LinearGradient to) {
            return from.getStartX() == to.getStartX() && from.getEndX() == to.getEndX()
                   && from.getStartY() == to.getStartY() && from.getEndY() == to.getEndY()
                   && from.isProportional() == to.isProportional()
                   // CycleMethod is an enum so '==' is valid
                   && from.getCycleMethod() == to.getCycleMethod();
        }
        
        @Override
        protected Paint interpolateImpl(double frac) {
            final List<Stop> stops = interpolateStops(from().getStops(), to().getStops(), frac);
            
            return uniform ? newLinearGradient(from(), stops) : interpolateNonUniform(stops, frac);
        }
        
        protected List<Stop> interpolateStops(List<Stop> from, List<Stop> to, double frac) {
            // from and to are same size so just interpolate their stops
            return IntStream.range(0, from.size()).mapToObj(i -> interpolateStop(from.get(i), to.get(i), frac))
                .collect(Collectors.toList());
        }
        
        protected Paint interpolateNonUniform(List<Stop> stops, double frac) {
            final LinearGradient f = from(), t = to();
            
            // start X
            final double startX = interpolateDouble(f.getStartX(), t.getStartX(), frac);
            // start Y
            final double startY = interpolateDouble(f.getStartY(), t.getStartY(), frac);
            // end X
            final double endX = interpolateDouble(f.getEndX(), t.getEndX(), frac);
            // end Y
            final double endY = interpolateDouble(f.getEndY(), t.getEndY(), frac);
            
            return new LinearGradient(startX, startY, endX, endY, to().isProportional(), to().getCycleMethod(), stops);
        }
        
        /**
         * If the sizes of 'from' and 'to' are equal then this method returns
         * a regular LinearToLinearFillSpan, otherwise this method returns a
         * LinearToLinearDisjunctFillSpan.
         */
        private static FillSpan ofLinear(LinearGradient from, LinearGradient to) {
            return from.getStops().size() == to.getStops().size() ? new LinearToLinearFillSpan(from, to)
                : new LinearToLinearDisjunctFillSpan(from, to);
        }
        
    } // class LinearToLinearFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Linear To Linear Disjunct Gradient                                     *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Indicates that {@code from} is a {@link LinearGradient} and
     * {@code to} is a {@link LinearGradient}, and that {@code from} and
     * {@code to}'s lists of {@link Stop} instances differ in size.
     */
    private static final class LinearToLinearDisjunctFillSpan extends LinearToLinearFillSpan {
        private final List<Double> offsets;
        private long fromToBits;
        
        private LinearToLinearDisjunctFillSpan(LinearGradient from, LinearGradient to) {
            super(from, to);
            
            offsets = marryStopOffsets(from.getStops(), to.getStops());
        }
        
        /**
         * 
         * @param from
         * @param to
         * @return
         */
        private List<Double> marryStopOffsets(List<Stop> from, List<Stop> to) {
            // a gradient's list of stops contains at least two stops, so we start with
            // from's and to's first stop's offset
            double fOff = from.get(0).getOffset();
            double tOff = to.get(0).getOffset();
            
            int fIdx = 0, tIdx = 0;
            int count = 0;
            
            ArrayList<Double> ret = new ArrayList<>(Math.max(from.size(), to.size()));
            while (fIdx < from.size() - 1 || tIdx < to.size() - 1) {
                
                if (fOff == tOff) {
                    ret.add(fOff);
                    
                    // mark that both from and to have a color change at this offset
                    setFromBit(count);
                    setToBit(count);
                    
                    // get from's and to's next stop's offset
                    fOff = from.get(++fIdx).getOffset();
                    tOff = to.get(++tIdx).getOffset();
                    
                } else if (fOff > tOff) {
                    ret.add(tOff);
                    
                    // mark that to has a color change at this offset
                    setToBit(count);
                    
                    // get to's next stop's offset
                    tOff = to.get(++tIdx).getOffset();
                    
                } else if (fOff < tOff) {
                    ret.add(fOff);
                    
                    // mark that from has a color change at this offset
                    setFromBit(count);
                    
                    // get from's next stop's offset
                    fOff = from.get(++fIdx).getOffset();
                }
                
                // keep track of current offset count so we can mark bits
                count += 1;
            }
            
            // add the last offset which is '1.0'
            ret.add(fOff);
            
            // we won't be adding any more offsets, so save some space
            ret.trimToSize();
            
            return ret;
        }
        
        @Override
        protected List<Stop> interpolateStops(List<Stop> fStops, List<Stop> tStops, double frac) {
            boolean fIsNew = true, tIsNew = true;
            int fIdx = 0, tIdx = 0;
            
            Color fColor = fStops.get(0).getColor();
            Color tColor = tStops.get(0).getColor();
            
            final List<Stop> ret = new ArrayList<Stop>(offsets.size());
            for (int i = 0, n = offsets.size(); i < n; i++) {
                fColor = fIsNew ? fStops.get(++fIdx).getColor() : fColor;
                tColor = tIsNew ? tStops.get(++tIdx).getColor() : tColor;
                
                ret.add(new Stop(offsets.get(i), fColor.interpolate(tColor, frac)));
                
                fIsNew = fromBitIsSet(i);
                tIsNew = toBitIsSet(i);
                
            }
            
            return ret;
        }
        
        /**
         * Sets a specified 'from' bit in {@code fromToBits} to {@code 1}.
         */
        private void setFromBit(int which) { fromToBits |= (1 << which); }
        
        /** 
         * Gets whether or not a specified 'from' bit in {@code fromToBits} is
         *  {@code 1}.
         */
        private boolean fromBitIsSet(int which) { return (fromToBits & (1 << which)) != 0; }
        
        /**
         * Sets a specified 'to' bit in {@code fromToBits} to {@code 1}.
         */
        private void setToBit(int which) { fromToBits |= (1 << (32 + which)); }
        
        /**
         * Gets whether or not a specified 'to' bit in {@code fromToBits} is {@code 1}.
         */
        private boolean toBitIsSet(int which) { return (fromToBits & (1 << (32 + which))) != 0; }
        
        
    } // class LinearToLinearDisjunctFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Radial To Radial Gradient                                              *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Indicates that {@code from} is a {@link RadialGradient} and
     * {@code to} is a {@link RadialGradient}.
     */
    private static class RadialToRadialFillSpan extends GradientFillSpan<RadialGradient, RadialGradient> {
        private final boolean uniform;
        
        private RadialToRadialFillSpan(RadialGradient from, RadialGradient to) {
            super(from, to);
            hash = 31 * hash + 8;
            uniform = calculateUniform(from, to);
        }
        
        private RadialToRadialFillSpan(Paint same) {
            super(same);
            hash = 31 * hash + 8;
            uniform = true;
        }
        
        /** 
         * A RadialToRadialFillSpan is uniform if 'from' and 'to' are the same
         * except for their 'stops'.
         */
        private static boolean calculateUniform(RadialGradient from, RadialGradient to) {
            return from.getFocusAngle() == to.getFocusAngle() && from.getFocusDistance() == to.getFocusDistance()
                   && from.getCenterX() == to.getCenterX() && from.getCenterY() == to.getCenterY()
                   && from.getRadius() == to.getRadius() && from.isProportional() == to.isProportional()
                   // CycleMethod is an enum so '==' is valid
                   && from.getCycleMethod() == to.getCycleMethod();
        }
        
        @Override
        protected Paint interpolateImpl(double frac) {
            final List<Stop> stops = interpolateStops(from().getStops(), to().getStops(), frac);
            
            return uniform ? newRadialGradient(from(), stops) : interpolateNonUniform(stops, frac);
        }
        
        protected List<Stop> interpolateStops(List<Stop> from, List<Stop> to, double frac) {
            // from and to are same size so just interpolate their stops
            return IntStream.range(0, from.size()).mapToObj(i -> interpolateStop(from.get(i), to.get(i), frac))
                .collect(Collectors.toList());
        }
        
        private Paint interpolateNonUniform(List<Stop> stops, double frac) {
            final RadialGradient f = from(), t = to();
            
            // focus angle
            final double angle = interpolateDouble(f.getFocusAngle(), t.getFocusAngle(), frac);
            // focus distance
            final double distance = interpolateDouble(f.getFocusDistance(), t.getFocusDistance(), frac);
            // center X
            final double centerX = interpolateDouble(f.getCenterX(), t.getCenterX(), frac);
            // center Y
            final double centerY = interpolateDouble(f.getCenterY(), t.getCenterY(), frac);
            // radius
            final double radius = interpolateDouble(f.getRadius(), t.getRadius(), frac);
            
            return new RadialGradient(angle, distance, centerX, centerY, radius, to().isProportional(),
                to().getCycleMethod(), stops);
        }
        
        /**
         * If the sizes of 'from' and 'to' are equal then this method returns
         * a regular RadialToRadialFillSpan, otherwise ths method returns a
         * RadialToRadialDisjunctFillSpan.
         */
        private static FillSpan ofRadial(RadialGradient from, RadialGradient to) {
            return from.getStops().size() == to.getStops().size() ? new RadialToRadialFillSpan(from, to)
                : new RadialToRadialDisjunctFillSpan(from, to);
        }
        
    } // class RadialToRadialFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Radial To Radial Disjunct Gradient                                     *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Indicates that {@code from} is a {@link RadialGradient} and
     * {@code to} is a {@link RadialGradient}, and that {@code from} and
     * {@code to}'s lists of {@link Stop} instances differ in size.
     */
    private static class RadialToRadialDisjunctFillSpan extends RadialToRadialFillSpan {
        private final List<Double> offsets;
        private long fromToBits;
        
        
        RadialToRadialDisjunctFillSpan(RadialGradient from, RadialGradient to) {
            super(from, to);
            
            offsets = marryStopOffsets(from.getStops(), to.getStops());
        }
        
        /**
         * 
         * @param from
         * @param to
         * @return
         */
        private List<Double> marryStopOffsets(List<Stop> from, List<Stop> to) {
            // a gradient's list of stops contains at least two stops, so we start with
            // from's and to's first stop's offset
            double fOff = from.get(0).getOffset();
            double tOff = to.get(0).getOffset();
            
            int fIdx = 0, tIdx = 0;
            int count = 0;
            
            ArrayList<Double> ret = new ArrayList<>(Math.max(from.size(), to.size()));
            while (fIdx < from.size() - 1 || tIdx < to.size() - 1) {
                
                if (fOff == tOff) {
                    ret.add(fOff);
                    
                    // mark that both from and to have a color change at this offset
                    setFromBit(count);
                    setToBit(count);
                    
                    // get from's and to's next stop's offset
                    fOff = from.get(++fIdx).getOffset();
                    tOff = to.get(++tIdx).getOffset();
                    
                } else if (fOff > tOff) {
                    ret.add(tOff);
                    
                    // mark that to has a color change at this offset
                    setToBit(count);
                    
                    // get to's next stop's offset
                    tOff = to.get(++tIdx).getOffset();
                    
                } else if (fOff < tOff) {
                    ret.add(fOff);
                    
                    // mark that from has a color change at this offset
                    setFromBit(count);
                    
                    // get from's next stop's offset
                    fOff = from.get(++fIdx).getOffset();
                }
                
                // keep track of current offset count so we can mark bits
                count += 1;
            }
            
            // add the last offset which is '1.0'
            ret.add(fOff);
            
            // we won't be adding any more offsets, so save some space
            ret.trimToSize();
            
            return ret;
        }
        
        @Override
        protected List<Stop> interpolateStops(List<Stop> fStops, List<Stop> tStops, double frac) {
            boolean fIsNew = true, tIsNew = true;
            int fIdx = 0, tIdx = 0;
            
            Color fColor = fStops.get(0).getColor();
            Color tColor = tStops.get(0).getColor();
            
            final List<Stop> ret = new ArrayList<Stop>(offsets.size());
            for (int i = 0, n = offsets.size(); i < n; i++) {
                fColor = fIsNew ? fStops.get(++fIdx).getColor() : fColor;
                tColor = tIsNew ? tStops.get(++tIdx).getColor() : tColor;
                
                ret.add(new Stop(offsets.get(i), fColor.interpolate(tColor, frac)));
                
                fIsNew = fromBitIsSet(i);
                tIsNew = toBitIsSet(i);
                
            }
            
            return ret;
        }
        
        /**
         * Sets a specified 'from' bit in {@code fromToBits} to {@code 1}.
         */
        private void setFromBit(int which) { fromToBits |= (1 << which); }
        
        /** 
         * Gets whether or not a specified 'from' bit in {@code fromToBits} is
         *  {@code 1}.
         */
        private boolean fromBitIsSet(int which) { return (fromToBits & (1 << which)) != 0; }
        
        /**
         * Sets a specified 'to' bit in {@code fromToBits} to {@code 1}.
         */
        private void setToBit(int which) { fromToBits |= (1 << (32 + which)); }
        
        /**
         * Gets whether or not a specified 'to' bit in {@code fromToBits} is {@code 1}.
         */
        private boolean toBitIsSet(int which) { return (fromToBits & (1 << (32 + which))) != 0; }
        
        
    } // class RadialToRadialDisjunctFillSpan
    
    
} // class GradientFillSpan

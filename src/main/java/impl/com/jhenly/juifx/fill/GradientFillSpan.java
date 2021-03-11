package impl.com.jhenly.juifx.fill;

import static javafx.animation.Interpolator.EASE_BOTH;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.animation.Interpolatable;
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
     * From-To Specifiers                                                     *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Enum that indicates {@code from}'s type and {@code to}'s type.
     * 
     * @see #COLOR_TO_LINEAR
     * @see #COLOR_TO_RADIAL
     * @see #LINEAR_TO_COLOR
     * @see #LINEAR_TO_LINEAR
     * @see #LINEAR_TO_RADIAL
     * @see #RADIAL_TO_COLOR
     * @see #RADIAL_TO_LINEAR
     * @see #RADIAL_TO_RADIAL
     */
    enum FromToSpecifier {
        /**
         * Indicates that {@code from} is a {@link Color} and {@code to} is a
         * {@link LinearGradient}.
         */
        COLOR_TO_LINEAR,
        /**
         * Indicates that {@code from} is a {@link Color} and {@code to} is a
         * {@link RadialGradient}.
         */
        COLOR_TO_RADIAL,
        /**
         * Indicates that {@code from} is a {@link LinearGradient} and
         * {@code to} is a {@link Color}.
         */
        LINEAR_TO_COLOR,
        /**
         * Indicates that {@code from} is a {@link LinearGradient} and
         * {@code to} is a {@link LinearGradient}.
         */
        LINEAR_TO_LINEAR,
        /**
         * Indicates that {@code from} is a {@link LinearGradient} and
         * {@code to} is a {@link RadialGradient}.
         */
        LINEAR_TO_RADIAL,
        /**
         * Indicates that {@code from} is a {@link RadialGradient} and
         * {@code to} is a {@link Color}.
         */
        RADIAL_TO_COLOR,
        /**
         * Indicates that {@code from} is a {@link RadialGradient} and
         * {@code to} is a {@link LinearGradient}.
         */
        RADIAL_TO_LINEAR,
        /**
         * Indicates that {@code from} is a {@link RadialGradient} and
         * {@code to} is a {@link RadialGradient}.
         */
        RADIAL_TO_RADIAL;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Private Members                                                        *
     *                                                                        *
     *************************************************************************/
    
    
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
    static final FillSpan ofGradient(Paint from, Paint to, boolean fromIsGradient, boolean toIsGradient) {
        
        /* Caution - smelly 'if-getClass()' code follows. */
        
        if (fromIsGradient && toIsGradient) {
            if (from.getClass() == LinearGradient.class) {
                if (to.getClass() == LinearGradient.class) {
                    return FillSpan
                        .getFromCache(LinearToLinearFillSpan.ofLinear((LinearGradient) from, (LinearGradient) to));
                } else {
                    // 'to' is a RadialGradient
                    return FillSpan.getFromCache(new LinearToRadialFillSpan(from, to));
                }
            } else {
                // 'from' is a RadialGradient
                if (to.getClass() == LinearGradient.class) {
                    return FillSpan.getFromCache(new RadialToLinearFillSpan(from, to));
                } else {
                    // 'to' is a RadialGradient
                    return FillSpan
                        .getFromCache(new RadialToRadialFillSpan((RadialGradient) from, (RadialGradient) to));
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
        
    }
    
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
    static final FillSpan ofGradient(Paint same) {
        if (same.getClass() == LinearGradient.class) {
            return FillSpan.getFromCache(new LinearToLinearFillSpan(same));
        } else {
            return FillSpan.getFromCache(new RadialToRadialFillSpan(same));
        }
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Stop Interpolate Methods                                               *
     *                                                                        *
     *************************************************************************/
    
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
     * Linear And Radial Gradient Interpolate Methods                         *
     *                                                                        *
     *************************************************************************/
    
    private static double interpolateDouble(double start, double end, double frac) {
        return start == end ? start : EASE_BOTH.interpolate(start, end, frac);
    }
    
    private static LinearGradient newLinearGradient(LinearGradient old, List<Stop> stops) {
        return new LinearGradient(old.getStartX(), old.getStartY(), old.getEndX(), old.getEndY(), old.isProportional(),
            old.getCycleMethod(), stops);
    }
    
    private static LinearGradient interpolateLinearGradient(LinearGradient from, LinearGradient to, double frac) {
        // startX
        final double fStartX = from.getStartX(), tStartX = to.getStartX();
        final double startX = (fStartX == tStartX) ? fStartX : interpolateDouble(fStartX, tStartX, frac);
        // startY
        final double fStartY = from.getStartY(), tStartY = to.getStartY();
        final double startY = (fStartY == tStartY) ? fStartY : interpolateDouble(fStartY, tStartY, frac);
        // endX
        final double fEndX = from.getEndX(), tEndX = to.getEndX();
        final double endX = (fEndX == tEndX) ? fEndX : interpolateDouble(fEndX, tEndX, frac);
        // endY
        final double fEndY = from.getEndY(), tEndY = to.getEndY();
        final double endY = (fEndY == tEndY) ? fEndY : interpolateDouble(fEndY, tEndY, frac);
        // stops
        final List<Stop> stops = interpolateStops(from.getStops(), to.getStops(), frac);
        
        return new LinearGradient(startX, startY, endX, endY, to.isProportional(), to.getCycleMethod(), stops);
    }
    
    private static RadialGradient interpolateRadialGradient(RadialGradient from, RadialGradient to, double frac) {
        // focus angle
        final double fAngle = from.getFocusAngle(), tAngle = to.getFocusAngle();
        final double angle = (fAngle == tAngle) ? fAngle : interpolateDouble(fAngle, tAngle, frac);
        // focus distance
        final double fDistance = from.getFocusDistance(), tDistance = to.getFocusDistance();
        final double distance = (fDistance == tDistance) ? fDistance : interpolateDouble(fDistance, tDistance, frac);
        // center X
        final double fCenterX = from.getCenterX(), tCenterX = to.getCenterX();
        final double centerX = (fCenterX == tCenterX) ? fCenterX : interpolateDouble(fCenterX, tCenterX, frac);
        // center Y
        final double fCenterY = from.getCenterY(), tCenterY = to.getCenterY();
        final double centerY = (fCenterY == tCenterY) ? fCenterY : interpolateDouble(fCenterY, tCenterY, frac);
        // radius
        final double fRadius = from.getRadius(), tRadius = to.getRadius();
        final double radius = (fRadius == tRadius) ? fRadius : interpolateDouble(fRadius, tRadius, frac);
        // stops
        final List<Stop> stops = interpolateStops(from.getStops(), to.getStops(), frac);
        
        return new RadialGradient(angle, distance, centerX, centerY, radius, to.isProportional(), to.getCycleMethod(),
            stops);
    }
    
    private static RadialGradient newRadialGradient(RadialGradient old, List<Stop> stops) {
        return new RadialGradient(old.getFocusAngle(), old.getFocusDistance(), old.getCenterX(), old.getCenterY(),
            old.getRadius(), old.isProportional(), old.getCycleMethod(), stops);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Color To Linear Gradient                                               *
     *                                                                        *
     *************************************************************************/
    
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
            final double startX = interpolateDouble(from().getStartX(), to().getStartX(), frac);
            final double startY = interpolateDouble(from().getStartY(), to().getStartY(), frac);
            final double endX = interpolateDouble(from().getEndX(), to().getEndX(), frac);
            final double endY = interpolateDouble(from().getEndY(), to().getEndY(), frac);
            
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
    
    private static final class LinearToLinearDisjunctFillSpan extends LinearToLinearFillSpan {
        private final List<Double> married;
        private int fromBits;
        private int toBits;
        
        private LinearToLinearDisjunctFillSpan(LinearGradient from, LinearGradient to) {
            super(from, to);
            
            married = calculateMarried(from.getStops(), to.getStops());
        }
        
        private List<Double> calculateMarried(List<Stop> from, List<Stop> to) {
            final List<Double> ret = new LinkedList<Double>();
            
            
            return ret;
        }
        
        @Override
        protected Paint interpolateImpl(double frac) {
            final List<Stop> fStops = from().getStops();
            final List<Stop> tStops = to().getStops();
            final List<Stop> ret = new ArrayList<Stop>(married.size());
            
            for (int i = 0, n = married.size(); i < n; i++) {
                
            }
            
            return ret;
        }
        
    } // class LinearToLinearDisjunctFillSpan
    
    /**************************************************************************
     *                                                                        *
     * Linear To Radial Gradient                                              *
     *                                                                        *
     *************************************************************************/
    
    private static final class LinearToRadialFillSpan extends GradientFillSpan<LinearGradient, RadialGradient> {
        private LinearToRadialFillSpan(Paint from, Paint to) {
            super(from, to);
            
            hash = 31 * hash + 5;
        }
    } // class LinearToRadialFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Radial To Linear Gradient                                               *
     *                                                                        *
     *************************************************************************/
    
    private static final class RadialToLinearFillSpan extends GradientFillSpan<RadialGradient, LinearGradient> {
        private RadialToLinearFillSpan(Paint from, Paint to) {
            super(from, to);
            
            hash = 31 * hash + 7;
        }
    } // class RadialToLinearFillSpan
    
    
    /**************************************************************************
     *                                                                        *
     * Radial To Radial Gradient                                              *
     *                                                                        *
     *************************************************************************/
    
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
        
        private Paint interpolateNonUniform(List<Stop> stops, double frac) {
            // focus angle
            final double fAngle = from().getFocusAngle(), tAngle = to().getFocusAngle();
            final double angle = (fAngle == tAngle) ? fAngle : interpolateDouble(fAngle, tAngle, frac);
            // focus distance
            final double fDistance = from().getFocusDistance(), tDistance = to().getFocusDistance();
            final double distance =
            (fDistance == tDistance) ? fDistance : interpolateDouble(fDistance, tDistance, frac);
            // center X
            final double fCenterX = from().getCenterX(), tCenterX = to().getCenterX();
            final double centerX = (fCenterX == tCenterX) ? fCenterX : interpolateDouble(fCenterX, tCenterX, frac);
            // center Y
            final double fCenterY = from().getCenterY(), tCenterY = to().getCenterY();
            final double centerY = (fCenterY == tCenterY) ? fCenterY : interpolateDouble(fCenterY, tCenterY, frac);
            // radius
            final double fRadius = from().getRadius(), tRadius = to().getRadius();
            final double radius = (fRadius == tRadius) ? fRadius : interpolateDouble(fRadius, tRadius, frac);
            
            return new RadialGradient(angle, distance, centerX, centerY, radius, to().isProportional(),
                to().getCycleMethod(), stops);
        }
        
        /**
         * If the sizes of 'from' and 'to' are equal then this method returns
         * a regular RadialToRadialFillSpan, otherwise ths method returns a
         * RadialToRadialDisjunctFillSpan.
         */
        private static FillSpan ofLinear(RadialGradient from, RadialGradient to) {
            return from.getStops().size() == to.getStops().size() ? new RadialToRadialFillSpan(from, to)
                : new RadialToRadialDisjunctFillSpan(from, to);
        }
        
    } // class RadialToRadialFillSpan
    
    
    private static class RadialToRadialDisjunctFillSpan extends RadialToRadialFillSpan {
        
        RadialToRadialDisjunctFillSpan(RadialGradient from, RadialGradient to) {
            super(from, to);
        }
    }
    
    private static final class MarriedStops implements Interpolatable<List<Stop>> {
        
        
        private MarriedStops(List<Stop> from, List<Stop> to) {
            
        }
        
        @Override
        public List<Stop> interpolate(List<Stop> fodder, double frac) { return interpolate(frac); }
        
        public List<Stop> interpolate(double frac) {
            return null;
        }
        
    }
} // class GradientFillSpan

package com.jhenly.juifx.fill;


import javafx.scene.paint.Color;


/**
 * Class used to indicate that either {@link FillSpan#from()} or
 * {@link FillSpan#to()} is a special identifier.
 * 
 * @see FillSpan#INHERIT
 * @see FillSpan#USE_BG
 * @see FillSpan#USE_TEXT
 * @see FillSpan#USE_SHAPE
 * @see FillSpan#USE_STROKE
 */
final class SpecialFillSpan extends FillSpan {
    
    /**************************************************************************
     *                                                                        *
     * Special Identifiers                                                    *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Special indicator that indicates to use an inherited fill color for
     * either {@code from} or {@code to}.
     */
    static final Color INHERIT = new Color(0.01, 0.0, 0.0, 0.0);
    /**
     * Special indicator that indicates to use a background fill color for
     * either {@code from} or {@code to}.
     * @see SpecialFillSpan
     */
    static final Color USE_BG = new Color(0.02, 0.0, 0.0, 0.0);
    /**
     * Special indicator that indicates to use a text fill color for either
     * {@code from} or {@code to}.
     */
    static final Color USE_TEXT = new Color(0.03, 0.0, 0.0, 0.0);
    /**
     * Special indicator that indicates to use a shape fill color for either
     * {@code from} or {@code to}.
     */
    static final Color USE_SHAPE = new Color(0.04, 0.0, 0.0, 0.0);
    /**
     * Special indicator that indicates to use a stroke fill color for either
     * {@code from} or {@code to}.
     */
    static final Color USE_STROKE = new Color(0.05, 0.0, 0.0, 0.0);
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Uses the regular {@code FillSpan} constructor.
     * @param from - the color to set {@code from} to
     * @param to - the color to set {@code to} to
     */
    private SpecialFillSpan(Color from, Color to) { super(from, to); }
    
    /**
     * Uses the {@code FillSpan} constructor that doesn't check for
     * equality.
     * @param same - the color to set {@code from} and {@code to} to
     */
    private SpecialFillSpan(Color same) { super(same); }
    
    
    /**************************************************************************
     *                                                                        *
     * Methods                                                                *
     *                                                                        *
     *************************************************************************/
    
    /* --- from --- */
    
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #from()}
     * is the {@link #INHERIT} identifier.
     * @return {@code true} if {@code from} is the {@code INHERIT} identifier,
     *         otherwise {@code false}
     */
    final boolean fromIsInherit() { return from() == INHERIT; }
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #from()}
     * is the {@link #USE_BG} identifier.
     * @return {@code true} if {@code from} is the {@code USE_BG} identifier,
     *         otherwise {@code false}
     */
    final boolean fromIsUseBg() { return from() == USE_BG; }
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #from()}
     * is the {@link #USE_TEXT} identifier.
     * @return {@code true} if {@code from} is the {@code USE_TEXT} identifier,
     *         otherwise {@code false}
     */
    final boolean fromIsUseText() { return from() == USE_TEXT; }
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #from()}
     * is the {@link #USE_SHAPE} identifier.
     * @return {@code true} if {@code from} is the {@code USE_SHAPE}
     *         identifier, otherwise {@code false}
     */
    final boolean fromIsUseShape() { return from() == USE_SHAPE; }
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #from()}
     * is the {@link #USE_STROKE} identifier.
     * @return {@code true} if {@code from} is the {@code USE_STROKE}
     *         identifier otherwise {@code false}
     */
    final boolean fromIsUseStroke() { return from() == USE_STROKE; }
    
    /* --- to --- */
    
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #to()}
     * is the {@link #INHERIT} identifier.
     * @return {@code true} if {@code to} is the {@code INHERIT} identifier
     *         otherwise {@code false}
     */
    final boolean toIsInherit() { return to() == INHERIT; }
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #to()}
     * is the {@link #USE_BG} identifier.
     * @return {@code true} if {@code to} is the {@code USE_BG} identifier,
     *         otherwise {@code false}
     */
    final boolean toIsUseBg() { return to() == USE_BG; }
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #to()}
     * is the {@link #USE_TEXT} identifier.
     * @return {@code true} if {@code to} is the {@code USE_TEXT} identifier,
     *         otherwise {@code false}
     */
    final boolean toIsUseText() { return to() == USE_TEXT; }
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #to()}
     * is the {@link #USE_SHAPE} identifier.
     * @return {@code true} if {@code to} is the {@code USE_SHAPE}
     *         identifier, otherwise {@code false}
     */
    final boolean toIsUseShape() { return to() == USE_SHAPE; }
    /**
     * Gets whether this {@code SpecialFillSpan} instance's {@link #to()}
     * is the {@link #USE_STROKE} identifier.
     * @return {@code true} if {@code to} is the {@code USE_STROKE}
     *         identifier otherwise {@code false}
     */
    final boolean toIsUseStroke() { return to() == USE_STROKE; }
    
    
    /**************************************************************************
     *                                                                        *
     * Static API                                                             *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * <p>
     * If either of the specified colors is a special identifier, then a
     * {@code FillSpan} that is an instance of {@link SpecialFillSpan} will be
     * returned.
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    static final FillSpan ofSpecial(Color from, Color to) {
        final boolean fromIsNull = (from == null);
        final boolean toIsNull = (to == null);
        
        if (fromIsNull && toIsNull) {
            // both are null so return null args instance
            return Holder.NULL_ARGS_INSTANCE;
        } else if (!fromIsNull && toIsNull) {
            to = from;
        } else if (fromIsNull && !toIsNull) {
            from = to;
        }
        
        // if either are spec. then get/create a spec. span from the cache, if
        // not then get/create a regular fill span from the cache
        return checkAndGetSpecial(from, to);
    }
    
    /** Helper method for {@link #ofSpecial(Color, Color)}. */
    private static final FillSpan checkAndGetSpecial(final Color from, final Color to) {
        // if either are spec. identifiers then create and return a spec. span
        if (FillSpanHelper.colorIsSpecialIdentifier(from) || FillSpanHelper.colorIsSpecialIdentifier(to)) {
            if (from == to) {
                // use constructor that doesn't check for equality
                return getSpecialFromCache(from);
            }
            return getSpecialFromCache(from, to);
        }
        
        // if neither are special identifiers then just return a regular span
        
        if (from == to) {
            // use FillSpan constructor that doesn't check equality
            return getFromCache(from);
        }
        return getFromCache(from, to);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Fill Span Cache                                                        *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a {@code FillSpan} with the specified colors from the cache if the
     * cache contains it, otherwise a new {@code SpecialFillSpan} is created
     * with the specified colors, then added to the cache and returned.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @return a new {@code FillSpan} created from the specified colors, or
     *         a previously cached {@code FillSpan} with the specified
     *         colors
     */
    private static final FillSpan getSpecialFromCache(Color from, Color to) {
        return FillSpanCache.get(new SpecialFillSpan(from, to));
    }
    
    /**
     * Gets a {@code FillSpan} that has the same fill-from and fill-to color
     * from the cache if the cache contains it, otherwise a new
     * {@code SpecialFillSpan} is created with the specified color, added to
     * the cache and returned.
     * 
     * @param same - the fill-from and fill-to color
     * @return a new {@code FillSpan} created from the specified color, or
     *         a previously cached {@code FillSpan} with the specified
     *         color
     */
    private static final FillSpan getSpecialFromCache(Color same) {
        return FillSpanCache.get(new SpecialFillSpan(same));
    }
    
    
}

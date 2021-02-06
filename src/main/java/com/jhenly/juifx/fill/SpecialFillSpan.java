package com.jhenly.juifx.fill;


import javafx.scene.paint.Color;


/**
 * Class used to indicate that either {@link FillSpan#from()} or
 * {@link FillSpan#to()} is a special identifier, and to contain the special
 * attributes associated with any given special identifier.
 * 
 * @see FillSpan#USE_BG
 * @see FillSpan#USE_BORDER
 * @see FillSpan#USE_TEXT
 * @see FillSpan#USE_SHAPE
 * @see FillSpan#USE_STROKE
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
final class SpecialFillSpan extends FillSpan {
    
    /**************************************************************************
     *                                                                        *
     * Private Members                                                        *
     *                                                                        *
     *************************************************************************/
    
    private final int specAtts; // holds all special attributes
    private final int hash;
    
    
    /**************************************************************************
     *                                                                        *
     * Constructor(s)                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Uses the regular {@code FillSpan} constructor.
     * @param from - the color to set {@code from} to
     * @param to - the color to set {@code to} to
     * @param fSpec - whether or not fill-from is a special identifier
     * @param tSpec - whether or not fill-to is a special identifier
     */
    private SpecialFillSpan(Color from, Color to, boolean fSpec, boolean tSpec) {
        super(from, to);
        
        specAtts = bitPackSpecialAttributes(fSpec, tSpec);
        hash = precomputeHash(this, super.hashCode());
    }
    
    /**
     * Uses the {@code FillSpan} constructor that doesn't check for
     * equality.
     * @param same - the color to set {@code from} and {@code to} to
     */
    private SpecialFillSpan(Color same) {
        super(same);
        
        // both 'from' and 'to' have to be special identifiers at this point
        specAtts = bitPackSpecialAttributes();
        hash = precomputeHash(this, super.hashCode());
    }
    
    /**
     * Uses the regular {@code FillSpan} constructor.
     * @param from - the color to set {@code from} to
     * @param to - the color to set {@code to} to
     * @param fSpec - whether or not fill-from is a special identifier
     * @param tSpec - whether or not fill-to is a special identifier
     * @param fIndex - the index of the fill to replace the fill-from with
     * @param tIndex - the index of the fill to replace the fill-to with
     */
    private SpecialFillSpan(Color from, Color to, boolean fSpec, boolean tSpec, int fIndex, int tIndex) {
        super(from, to);
        
        specAtts = bitPackSpecialAttributes(fSpec, tSpec, fIndex, tIndex);
        hash = precomputeHash(this, super.hashCode());
    }
    
    /**
     * Uses the {@code FillSpan} constructor that doesn't check for
     * equality.
     * @param same - the color to set {@code from} and {@code to} to
     * @param index - the index of the fill to replace fill-from and fill-to
     *        with
     */
    private SpecialFillSpan(Color same, int index) {
        super(same);
        
        // both 'from' and 'to' have to be special identifiers at this point
        specAtts = bitPackSpecialAttributes(index);
        hash = precomputeHash(this, super.hashCode());
    }
    
    /**
     * Uses the regular {@code FillSpan} constructor.
     * @param from - the color to set {@code from} to
     * @param to - the color to set {@code to} to
     * @param fSpec - whether or not fill-from is a special identifier
     * @param tSpec - whether or not fill-to is a special identifier
     * @param fIndex - the index of the fill to replace the fill-from with
     * @param tIndex - the index of the fill to replace the fill-to with
     * @param fBsPos - the from border stroke position
     * @param tBsPos - the to border stroke position
     */
    private SpecialFillSpan(Color from, Color to, boolean fSpec, boolean tSpec, int fIndex, int tIndex, int fBsPos,
        int tBsPos) {
        super(from, to);
        
        specAtts = bitPackSpecialAttributes(fSpec, tSpec, fIndex, tIndex, fBsPos, tBsPos);
        hash = precomputeHash(this, super.hashCode());
    }
    
    /**
     * Uses the {@code FillSpan} constructor that doesn't check for
     * equality.
     * @param same - the color to set {@code from} and {@code to} to
     * @param index - the index of the fill to replace fill-from and fill-to
     *        with
     * @param bsPos - the border stroke position for from and to
     */
    private SpecialFillSpan(Color same, int index, int bsPos) {
        super(same);
        
        specAtts = bitPackSpecialAttributes(true, index, bsPos);
        hash = precomputeHash(this, super.hashCode());
    }
    
    /** Helper used by constructors to precompute hash. */
    private static int precomputeHash(SpecialFillSpan span, int superHash) {
        return 31 * superHash + span.specAtts;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Methods                                                                *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets whether or not fill-from is a special identifier.
     * @return {@code true} if fill-from is a special identifier, otherwise
     *         {@code false}
     */
    boolean fromIsSpecial() { return fromSpecBitIsSet(specAtts); }
    
    /**
     * Gets whether or not fill-to is a special identifier.
     * @return {@code true} if fill-to is a special identifier, otherwise
     *         {@code false}
     */
    boolean toIsSpecial() { return toSpecBitIsSet(specAtts); }
    
    /**
     * Gets the border stroke position to use for the fill-from.
     * <p>
     * This method returns the top border stroke position by default.
     * @return the fill-from border stroke position
     */
    int fromBsPos() { return getFromBsPos(specAtts); }
    
    /**
     * Gets the border stroke position to use for the fill-to.
     * <p>
     * This method returns the top border stroke position by default.
     * @return the fill-to border stroke position
     */
    int toBsPos() { return getToBsPos(specAtts); }
    
    /**
     * Gets the index of the fill to replace the fill-from with.
     * @return the index of the fill to replace the fill-from with or
     *         {@code -1} if not set
     *         
     */
    int fromIndex() {
        int index = getFromIndex(specAtts);
        return index != 255 ? index : -1;
    }
    
    /**
     * Gets the index of the fill to replace the fill-to with.
     * @return the index of the fill to replace the fill-to with, or {@code -1}
     *         if not set
     */
    int toIndex() {
        int index = getToIndex(specAtts);
        return index != 255 ? index : -1;
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() { return hash; }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj == null || !(obj instanceof SpecialFillSpan)) { return false; }
        
        SpecialFillSpan that = (SpecialFillSpan) obj;
        
        
        if (this.hash != that.hash) { return false; }
        if (this.specAtts != that.specAtts) { return false; }
        
        return super.equals(that);
        
//        return (this.hash == that.hash)
//            && (this.specAtts == that.specAtts)
//            && super.equals(that);
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Static 'of' Methods                                                    *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @param fromIsSpecial - whether or not fill-from is a special identifier
     * @param toIsSpecial - whether or not fill-to is a special identifier
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    static final FillSpan of(Color from, Color to, boolean fromIsSpecial, boolean toIsSpecial) {
        return getFromCache(new SpecialFillSpan(from, to, fromIsSpecial, toIsSpecial));
    }
    
    /**
     * 
     * @param same - the color to set {@code from} and {@code to} to
     * @param index - the index of the fill to replace fill-from and fill-to
     *        with
     */
    static final FillSpan of(Color same) {
        return getFromCache(new SpecialFillSpan(same));
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @param fromIsSpecial - whether or not fill-from is a special identifier
     * @param toIsSpecial - whether or not fill-to is a special identifier
     * @param fIndex - the index of the fill to replace the fill-from with
     * @param tIndex - the index of the fill to replace the fill-to with
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    static final FillSpan of(Color from, Color to, boolean fromIsSpecial, boolean toIsSpecial, int fIndex, int tIndex) {
        return getFromCache(new SpecialFillSpan(from, to, fromIsSpecial, toIsSpecial, fIndex, tIndex));
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * @param same - the color to set {@code from} and {@code to} to
     * @param index - the index of the fill to replace fill-from and fill-to
     *        with
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         color
     */
    static final FillSpan of(Color same, int index) {
        return getFromCache(new SpecialFillSpan(same, index));
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @param fromIsSpecial - whether or not fill-from is a special identifier
     * @param toIsSpecial - whether or not fill-to is a special identifier
     * @param fIndex - the index of the fill to replace the fill-from with
     * @param tIndex - the index of the fill to replace the fill-to with
     * @param fBsPos - the from border stroke position
     * @param tBsPos - the to border stroke position
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    static final FillSpan of(Color from, Color to, boolean fSpec, boolean tSpec, int fIndex, int tIndex, int fBsPos,
        int tBsPos) {
        return getFromCache(new SpecialFillSpan(from, to, fSpec, tSpec, fIndex, tIndex, fBsPos, tBsPos));
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * 
     * @param from - the fill-from color
     * @param to - the fill-to color
     * @param fromIsSpecial - whether or not fill-from is a special identifier
     * @param toIsSpecial - whether or not fill-to is a special identifier
     * @param fIndex - the index of the fill to replace the fill-from with
     * @param tIndex - the index of the fill to replace the fill-to with
     * @param fBsPos - the from border stroke position
     * @param tBsPos - the to border stroke position
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         colors
     */
    static final FillSpan of(Color same, int index, int bsPos) {
        return getFromCache(new SpecialFillSpan(same, index, bsPos));
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Special Attribute Bits                                                 *
     *                                                                        *
     *************************************************************************/
    
    /** 
     * Just calls {@link #bitPackSpecialAttributes(boolean, boolean, int, int, int, int)
     * bitPackSpecialAttributes(true, true, -1, -1, 0, 0)}.
     */
    private static int bitPackSpecialAttributes() {
        return bitPackSpecialAttributes(true, true);
    }
    
    /** 
     * Just calls {@link #bitPackSpecialAttributes(boolean, boolean, int, int, int, int)
     * bitPackSpecialAttributes(fSpec, tSpec, fIndex, tIndex, 0, 0)}.
     * @param fSpec - if from is special
     * @param tSpec - if to is special
     */
    private static int bitPackSpecialAttributes(boolean fSpec, boolean tSpec) {
        return bitPackSpecialAttributes(fSpec, tSpec, -1, -1);
    }
    
    /** 
     * Just calls {@link #bitPackSpecialAttributes(boolean, boolean, int, int, int, int)
     * bitPackSpecialAttributes(true, true, index, index, 0, 0)}.
     * @param index - the index of the fill to use for from and to
     * @return a 32-bit integer with all the attributes packed in it
     */
    private static int bitPackSpecialAttributes(int index) {
        return bitPackSpecialAttributes(true, true, index, index);
    }
    
    /** 
     * Just calls {@link #bitPackSpecialAttributes(boolean, boolean, int, int, int, int)
     * bitPackSpecialAttributes(fSpec, tSpec, fIndex, tIndex, 0, 0)}.
     * @param fSpec - if from is special
     * @param tSpec - if to is special
     * @param fIndex - the index of the fill to use for from
     * @param tIndex - the index of the fill to use for to
     * @return a 32-bit integer with all the attributes packed in it
     */
    private static int bitPackSpecialAttributes(boolean fSpec, boolean tSpec, int fIndex, int tIndex) {
        return bitPackSpecialAttributes(fSpec, tSpec, fIndex, tIndex, 0, 0);
    }
    
    /** 
     * Just calls {@link #bitPackSpecialAttributes(boolean, boolean, int, int, int, int)
     * bitPackSpecialAttributes(spec, spec, index, index, bsPos, bsPos)}.
     * @param spec - if from and to are special
     * @param index - the index of the fill to use for from and to
     * @param bsPos - the border position's fill to use for from and to
     * @return a 32-bit integer with all the attributes packed in it
     */
    private static int bitPackSpecialAttributes(boolean spec, int index, int bsPos) {
        return bitPackSpecialAttributes(spec, spec, index, index, bsPos, bsPos);
    }
    
    /**
     * Packs all of the relevant special identifier attributes into a 32-bit
     * integer.
     * <p>
     * Packed bits layout:<pre>
     * 
     *                     15         7 5  321
     * 0000 0000 0000 0000 0000 0000 0000 0000
     *            |         |         | |  |||
     *                   to index     
     *                                | |  ||| 
     *                           from index         
     *                                  |  |||    
     *                              to Bs Pos
     *                                     |||         
     *                               from Bs Pos       
     *                                      ||   
     *                                to is special
     *                                       |
     *                                 from is special
     * </pre>                                
     *                                 
     * @param fSpec - if from is special
     * @param tSpec - if to is special
     * @param fIndex - the index of the fill to use for from
     * @param tIndex - the index of the fill to use for to
     * @param fBsPos - the border position's fill to use for from
     * @param tBsPos - the border position's fill to use for to
     * @return a 32-bit integer with all the attributes packed in it
     */
    private static int bitPackSpecialAttributes(boolean fSpec, boolean tSpec, int fIndex, int tIndex, int fBsPos,
        int tBsPos) {
        int preSpecAtts = 0;
        preSpecAtts = setFromSpecBit(preSpecAtts, fSpec);
        preSpecAtts = setToSpecBit(preSpecAtts, tSpec);
        preSpecAtts = setFromBsPos(preSpecAtts, fBsPos);
        preSpecAtts = setToBsPos(preSpecAtts, tBsPos);
        preSpecAtts = setFromIndex(preSpecAtts, fIndex);
        preSpecAtts = setToIndex(preSpecAtts, tIndex);
        return preSpecAtts;
    }
    
    private static class IsSpecialHolder {
        static final int FROM_SPEC_BIT = 0x01b;
        static final int TO_SPEC_BIT = 0x10b;
    }
    
    private static int setFromSpecBit(int specAtts, boolean fromIsSpec) {
        return specAtts | (fromIsSpec ? IsSpecialHolder.FROM_SPEC_BIT : 0x0b);
    }
    private static int setToSpecBit(int specAtts, boolean toIsSpec) {
        return specAtts | (toIsSpec ? IsSpecialHolder.TO_SPEC_BIT : 0x0b);
    }
    private static boolean fromSpecBitIsSet(int specAtts) { return (specAtts & IsSpecialHolder.FROM_SPEC_BIT) != 0; }
    private static boolean toSpecBitIsSet(int specAtts) { return (specAtts & IsSpecialHolder.TO_SPEC_BIT) != 0; }
    
    private static class BorderStrokePosHolder {
        static final int BS_POS_BIT_SIZE = 2;
        static final int BS_POS_BIT_MASK = 3;
        static final int BS_POS_FROM_SHIFT = 2;
        static final int BS_POS_FROM_MASK = BS_POS_BIT_MASK << BS_POS_FROM_SHIFT;
        static final int BS_POS_TO_SHIFT = BS_POS_FROM_SHIFT + BS_POS_BIT_SIZE;
        static final int BS_POS_TO_MASK = BS_POS_BIT_MASK << BS_POS_TO_SHIFT;
    }
    
    private static int setFromBsPos(int specAtts, int pos) {
        return specAtts | ((pos & BorderStrokePosHolder.BS_POS_BIT_MASK) << BorderStrokePosHolder.BS_POS_FROM_SHIFT);
    }
    private static int setToBsPos(int specAtts, int pos) {
        return specAtts | ((pos & BorderStrokePosHolder.BS_POS_BIT_MASK) << BorderStrokePosHolder.BS_POS_TO_SHIFT);
    }
    
    private static int getFromBsPos(int specAtts) {
        return (specAtts & BorderStrokePosHolder.BS_POS_FROM_MASK) >>> BorderStrokePosHolder.BS_POS_FROM_SHIFT;
    }
    private static int getToBsPos(int specAtts) {
        return (specAtts & BorderStrokePosHolder.BS_POS_TO_MASK) >>> BorderStrokePosHolder.BS_POS_TO_SHIFT;
    }
    
    private static class IndexHolder {
        static final int INDEX_BIT_SIZE = 8;
        static final int INDEX_BIT_MASK = 255;
        static final int FROM_INDEX_SHIFT = 6;
        static final int FROM_INDEX_MASK = INDEX_BIT_MASK << FROM_INDEX_SHIFT;
        static final int TO_INDEX_SHIFT = INDEX_BIT_SIZE + FROM_INDEX_SHIFT;
        static final int TO_INDEX_MASK = INDEX_BIT_MASK << TO_INDEX_SHIFT;
    }
    
    private static int setFromIndex(int specAtts, int index) {
        return specAtts | ((index & IndexHolder.INDEX_BIT_MASK) << IndexHolder.FROM_INDEX_SHIFT);
    }
    private static int setToIndex(int specAtts, int index) {
        return specAtts | ((index & IndexHolder.INDEX_BIT_MASK) << IndexHolder.TO_INDEX_SHIFT);
    }
    private static int getFromIndex(int specAtts) {
        return (specAtts & IndexHolder.FROM_INDEX_MASK) >>> IndexHolder.FROM_INDEX_SHIFT;
    }
    private static int getToIndex(int specAtts) {
        return (specAtts & IndexHolder.TO_INDEX_MASK) >>> IndexHolder.TO_INDEX_SHIFT;
    }
    
}

package impl.com.jhenly.juifx.fill;

import javafx.scene.paint.Paint;


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
     * @param fIndex - the index of the fill to replace the fill-from with
     * @param tIndex - the index of the fill to replace the fill-to with
     * @param fBsPos - the from border stroke position
     * @param tBsPos - the to border stroke position
     */
    private SpecialFillSpan(Paint from, Paint to, boolean fSpec, boolean tSpec, int fIndex, int tIndex,
                            BorderStrokePosition fBsPos, BorderStrokePosition tBsPos)
    {
        super(from, to);
        
        specAtts = bitPackSpecialAttributes(fSpec, tSpec, fIndex, tIndex, fBsPos, tBsPos);
        // 'hash' is set in 'super(from, to)'
        hash = 31 * hash + specAtts;
    }
    
    /**
     * Uses the {@code FillSpan} constructor that doesn't check for
     * equality.
     * @param same - the color to set {@code from} and {@code to} to
     * @param index - the index of the fill to replace fill-from and fill-to
     *        with
     * @param bsPos - the border stroke position for from and to
     */
    private SpecialFillSpan(Paint same, int index, BorderStrokePosition bsPos) {
        super(same);
        
        specAtts = bitPackSpecialAttributes(true, index, bsPos);
        // 'hash' is set in 'super(same)'
        hash = 31 * hash + specAtts;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Methods                                                                *
     *                                                                        *
     *************************************************************************/
    
    /**
     * {@inheritDoc}
     * @return {@code true}
     */
    @Override
    boolean isSpecial() { return true; }
    
    /**
     * Gets whether or not fill-from is a special identifier.
     * @return {@code true} if fill-from is a special identifier, otherwise
     *         {@code false}
     */
    boolean fromIsSpecial() {
        return fromSpecBitIsSet(specAtts);
    }
    
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
        
        return (hash == that.hash) && (specAtts == that.specAtts) && super.equals(that);
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
    static FillSpan of(Paint from, Paint to, boolean fromIsSpecial, boolean toIsSpecial) {
        return getFromCache(new SpecialFillSpan(from, to, fromIsSpecial, toIsSpecial, -1, -1, null, null));
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * 
     * @param same - the color to set {@code from} and {@code to} to
     * @param index - the index of the fill to replace fill-from and fill-to
     *        with
     */
    static FillSpan of(Paint same) {
        return getFromCache(new SpecialFillSpan(same, -1, null));
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
    static FillSpan of(Paint from, Paint to, boolean fromIsSpecial, boolean toIsSpecial, int fIndex, int tIndex) {
        return getFromCache(new SpecialFillSpan(from, to, fromIsSpecial, toIsSpecial, fIndex, tIndex, null, null));
    }
    
    /**
     * Gets a {@link FillSpan} with the specified fill-from and fill-to colors.
     * 
     * @param same - the color to set {@code from} and {@code to} to
     * @param index - the index of the fill to replace fill-from and fill-to
     *        with
     * @return a {@code FillSpan} with the specified fill-from and fill-to
     *         color
     */
    static FillSpan of(Paint same, int index) {
        return getFromCache(new SpecialFillSpan(same, index, null));
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
    static FillSpan of(Paint from, Paint to, boolean fSpec, boolean tSpec, int fIndex, int tIndex,
                       BorderStrokePosition fBsPos, BorderStrokePosition tBsPos)
    {
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
    static FillSpan of(Paint same, int index, BorderStrokePosition bsPos) {
        return getFromCache(new SpecialFillSpan(same, index, bsPos));
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Special Attribute Bit Packing Section - PROCEED WITH CAUTION           *
     *                                                                        *
     *************************************************************************/
    
    /** 
     * Just calls {@link #bitPackSpecialAttributes(boolean, boolean, int, int, int, int)
     * bitPackSpecialAttributes(spec, spec, index, index, bsPos, bsPos)}.
     * @param spec - if from and to are special
     * @param index - the index of the fill to use for from and to
     * @param bsPos - the border position's fill to use for from and to
     * @return a 32-bit integer with all the attributes packed in it
     */
    private static int bitPackSpecialAttributes(boolean spec, int index, BorderStrokePosition bsPos) {
        return bitPackSpecialAttributes(spec, spec, index, index, bsPos, bsPos);
    }
    
    /**
     * Packs all of the relevant special identifier attributes into a 32-bit
     * integer.
     * <p>
     * Packed bits layout:<pre>
     * 
     *           23        15         7 5  321
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
    private static int bitPackSpecialAttributes(boolean fSpec, boolean tSpec, int fIndex, int tIndex,
                                                BorderStrokePosition fBsPos, BorderStrokePosition tBsPos)
    {
        int preSpecAtts = 0;
        preSpecAtts = setFromSpecBit(preSpecAtts, fSpec);
        preSpecAtts = setToSpecBit(preSpecAtts, tSpec);
        
        final int fBsPosOrd = (fBsPos == null) ? 0 : fBsPos.ordinal();
        final int tBsPosOrd = (tBsPos == null) ? 0 : tBsPos.ordinal();
        
        preSpecAtts = setFromBsPos(preSpecAtts, fBsPosOrd);
        preSpecAtts = setToBsPos(preSpecAtts, tBsPosOrd);
        
        preSpecAtts = setFromIndex(preSpecAtts, fIndex);
        preSpecAtts = setToIndex(preSpecAtts, tIndex);
        return preSpecAtts;
    }
    
    
    /**************************************************************************
     *                                                                        *
     * Special Attribute Bit Fiddling Section - PROCEED WITH CAUTION          *
     *                                                                        *
     *************************************************************************/
    
    private static class IsSpecialHolder {
        static final int FROM_SPEC_BIT = 0b01;
        static final int TO_SPEC_BIT = 0b10;
    }
    
    private static int setFromSpecBit(int specAtts, boolean fromIsSpec) {
        return specAtts | (fromIsSpec ? IsSpecialHolder.FROM_SPEC_BIT : 0);
    }
    private static int setToSpecBit(int specAtts, boolean toIsSpec) {
        return specAtts | (toIsSpec ? IsSpecialHolder.TO_SPEC_BIT : 0);
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

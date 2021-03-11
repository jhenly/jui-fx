package impl.com.jhenly.juifx.fill;

import com.jhenly.juifx.control.Fillable;

import javafx.css.CssMetaData;
import javafx.css.StyleableObjectProperty;


/**
 * This class extends {@code StyleableObjectProperty} and provides a partial
 * implementation of a {@code FillProperty}.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public abstract class FillProperty extends StyleableObjectProperty<Fill> {
    protected Fill special;
    
    /**
     * The constructor of the {@code FillProperty}.
     */
    public FillProperty() { super(); }
    
    /**
     * The constructor of the {@code FillProperty}.
     * 
     * @param initialValue - the initial value of the wrapped object
     * @param fable - the {@code Fillable} associated with this property
     */
    public FillProperty(Fill initialValue, Fillable fable) {
        super(replaceSpecial(initialValue, fable));
        special = (initialValue != null && initialValue.hasSpecial()) ? initialValue : null;
    }
    
    /** Helper method used by constructor during call to 'super(...)'. */
    private static Fill replaceSpecial(Fill fill, Fillable fable) {
        if (fill == null || !fill.hasSpecial()) { return fill; }
        
        return Fill.replaceSpecialsInFill(fill, fable);
    }
    
    /**
     * 
     */
    protected void fireValueChanged() {
        getFillable().getFillApplier().updateCaches();
        
        if (special != null) {
            Fill newRepl = Fill.replaceSpecialsInFill(special, getFillable());
            if (!newRepl.equals(get())) {
                super.set(newRepl);
            }
        }
    }
    
    @Override
    public void set(Fill val) {
        if (val == null) {
            // super.set may throw, null out 'special' afterward
            super.set(val);
            special = null;
            return;
        }
        
        // don't do anything if we weren't given a new fill
        if (val.equals(special) || val.equals(get())) { return; }
        
        final boolean hasSpecial = val.hasSpecial();
        
        // replace special fill spans if fill contains any
        if (hasSpecial) { val = Fill.replaceSpecialsInFill(val, getFillable()); }
        
        // super.set may throw if this is bound, so set 'special' afterward
        super.set(val);
        
        // store fill if it contains special fill spans
        special = hasSpecial ? val : null;
    }
    
    /**
     * Gets the {@link Fillable} instance that this property is associated
     * with.
     * @return the {@code Fillable} instance
     */
    protected abstract Fillable getFillable();
    
    @Override
    public String getName() { return "fill"; } // $NON-NLS-1$
    
    @Override
    public CssMetaData<Fillable, Fill> getCssMetaData() { return Fillable.StyleableProperties.FILL; }
    
}

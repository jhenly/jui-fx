package com.jhenly.juifx.control.skin;

import com.jhenly.juifx.animation.JuiFillTransition;
import com.jhenly.juifx.control.FillButton;
import com.jhenly.juifx.control.SelectableFillButton;
import com.jhenly.juifx.control.applier.FillApplier;
import com.jhenly.juifx.control.applier.SelectableFillButtonApplier;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;


/**
 * Default skin implementation for the {@code FillButton} control.
 *
 * @param <C> - the type that extends {@code FillButton}
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 * @see FillButton
 */
public class SelectableFillButtonSkin<C extends SelectableFillButton> extends SelectableButtonSkin<C>
implements FillableSkin<C>
{
    
    /***************************************************************************
     *                                                                         *
     * Private Members                                                         *
     *                                                                         *
     **************************************************************************/
    
    private JuiFillTransition jfTrans;
    private BooleanBinding fillDisabledOrSelected;
    
    
    /***************************************************************************
     *                                                                         *
     * Listener(s)                                                             *
     *                                                                         *
     **************************************************************************/
    
    private ChangeListener<Boolean> focusChange = (obv, o, n) -> onFocused(n);
    private WeakChangeListener<Boolean> weakFocusChange = new WeakChangeListener<>(focusChange);
    
    
    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
    
    /**
         * Creates a new {@code FillButtonSkin} instance.
         *
         * @param control - the control that this skin should be installed onto
         */
    public SelectableFillButtonSkin(final C control) {
        super(control);
        
        // setFillApplier(createDefaultFillApplier());
        
        // create JuiFillTransition after fill applier has been set
        jfTrans = new JuiFillTransition(getFillApplier());
        jfTrans.durationProperty().bind(control.fillDurationProperty());
        
        fillDisabledOrSelected = Bindings.or(control.selectedProperty(), control.fillEnabledProperty().not());
        
        // register FillButton change listeners
        registerChangeListener(control.fillEnabledProperty(), o -> onFillEnabled(getFillable().isFillEnabled()));
        
        // register SelectableButton change listeners
        registerChangeListener(control.selectedProperty(), o -> onSelected(getFillable().isSelected()));
        
        // register Button change listeners
        registerChangeListener(control.armedProperty(), o -> onArmed(getFillable().isArmed()));
        
        // register Node change listeners
        registerChangeListener(control.hoverProperty(), o -> onHover(getFillable().isHover()));
        
        // focus change listener
        focusChange = (obv, o, n) -> onFocused(n);
        
        // add focus change listener if fillOnFocus is true
        if (control.isFillOnFocus()) { control.focusedProperty().addListener(weakFocusChange); }
        
        // fill on focused listener
        registerChangeListener(control.fillOnFocusProperty(), o -> {
            final BooleanProperty fillOnFocus = getFillable().fillOnFocusProperty();
            if (fillOnFocus.get()) {
                getFillable().focusedProperty().addListener(weakFocusChange);
            } else {
                getFillable().focusedProperty().removeListener(weakFocusChange);
            }
        });
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API (from Skin)                                                  *
     *                                                                         *
     **************************************************************************/
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        if (getFillable() == null) { return; }
        
        getFillable().focusedProperty().removeListener(weakFocusChange);
        
        if (jfTrans != null) {
            jfTrans.dispose();
            jfTrans = null;
        }
        
        if (getFillApplier() != null) {
            getFillApplier().dispose();
        }
        
        super.dispose();
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API (from FillableSkin)                                          *
     *                                                                         *
     **************************************************************************/
    
    @Override
    public C getFillable() { return thisSkinnable(); }
    
    @Override
    public FillApplier<C> createDefaultFillApplier() { return new SelectableFillButtonApplier<C>(getFillable()); }
    
    @Override
    public final ReadOnlyObjectProperty<FillApplier<C>> fillApplierProperty() {
        return fillApplier.getReadOnlyProperty();
    }
    protected void setFillApplier(FillApplier<C> value) { fillApplier.set(value); }
    private ReadOnlyObjectWrapper<FillApplier<C>> fillApplier =
    new ReadOnlyObjectWrapper<FillApplier<C>>(this, "fillApplier", createDefaultFillApplier());
    
    
    /***************************************************************************
     *                                                                         *
     * Private Implementation                                                  *
     *                                                                         *
     **************************************************************************/
    
    private void onFillEnabled(final boolean enabled) {
        if (!enabled) {
            // don't alter fillable's look if its selected
            if (getFillable().isSelected()) { return; }
            
            // go back to start, reset fillable to its pre-fill state
            jfTrans.jumpToStart();
            getFillApplier().resetFillable();
        } else {
            // if fill transition is enabled while hover or focus then play
            // fill
            if (getFillable().isHover() || getFillable().isFocused()) {
                jfTrans.playForward();
            }
        }
    }
    
    private void onSelected(boolean selected) {
        if (selected) {
            jfTrans.jumpToEnd();
        } else {
            jfTrans.jumpToStart();
        }
    }
    
    private void onArmed(boolean armed) {
        // don't alter the button if fill is disabled or it's selected
        if (fillDisabledOrSelected.get()) { return; }
        
        if (!armed) {
            jfTrans.playBackward();
        }
        
    }
    
    private void onHover(boolean isHovering) {
        // don't alter the button if fill is disabled or it's selected
        if (fillDisabledOrSelected.get()) { return; }
        
        if (isHovering) {
            jfTrans.playForward();
        } else {
            final C fable = getFillable();
            if (fable.isArmed()) {
                jfTrans.playBackward();
                return;
            }
            
            // don't un-fill if the button has focus and it's not armed
            if (fable.isFillOnFocus() && fable.isFocused()) { return; }
            
            jfTrans.playBackward();
        }
    }
    
    private void onFocused(boolean isFocused) {
        // don't alter the button if it's being hovered or it's selected
        if (fillDisabledOrSelected.get() || getFillable().isHover()) { return; }
        
        if (isFocused && !getFillable().isPressed()) {
            jfTrans.playForward();
        } else {
            jfTrans.playBackward();
        }
        
    }
    
}

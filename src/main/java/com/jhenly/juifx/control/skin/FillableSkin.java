package com.jhenly.juifx.control.skin;

import java.util.function.Consumer;

import com.jhenly.juifx.control.Fillable;

import impl.com.jhenly.juifx.fill.FillApplier;
import javafx.beans.value.ObservableValue;


/**
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public interface FillableSkin<F extends Fillable> {
    
    /**
     * 
     * @return
     */
    FillApplier<?> createDefaultFillApplier();
    
    FillApplier<?> getFillApplier();
    
    void addChangeListener(ObservableValue<?> property, Consumer<ObservableValue<?>> consumer);
    
    Consumer<ObservableValue<?>> removeChangeListeners(ObservableValue<?> property);
    
}

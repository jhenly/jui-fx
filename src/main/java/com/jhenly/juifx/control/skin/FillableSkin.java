package com.jhenly.juifx.control.skin;

import com.jhenly.juifx.control.Fillable;
import com.jhenly.juifx.control.applier.FillApplier;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;


/**
 * The {@code FillableSkin} interface is implemented by the {@link Fillable}
 * interface, and therefore is implemented by all {@code Fillable}
 * implementations.
 *
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public interface FillableSkin<F extends Fillable> {
    
    /**
     * Gets the {@code Fillable} to which this {@code FillableSkin} is assigned.
     * <p>
     * {@link Control}
     * A {@code FillableSkin} must be created for one and only one
     * {@code Fillable}. This value will only ever go from a non-{@code null}
     * to {@code null} value when the {@code FillableSkin} is removed from the
     * {@code Fillable}, and only as a consequence of a call to
     * {@link #dispose()}.
     * <p>
     * The caller who constructs a {@code Fillable} must also construct a
     * {@code FillableSkin} and properly establish the relationship between the
     * {@code Fillable} and its {@code FillableSkin}.
     *
     * @return a non-{@code null} {@code Fillable}, or {@code null} if disposed
     */
    F getFillable();
    
    /**
     * Creates a new instance of the default {@code FillApplier} for this
     * {@code FillableSkin}.
     * <p>
     * This method should not be invoked by non-implementing classes. It is
     * meant to be invoked and/or overridden by implementing classes. This
     * method should also never return {@code null}, doing so will result in
     * undefined behavior.
     * 
     * @return  a new, non-{@code null} instance of the default
     *          {@code FillApplier} for this {@code FillableSkin}
     */
    FillApplier<F> createDefaultFillApplier();
    
    /**
    * This {@code FillableSkin} instances {@link FillApplier} property..
    * 
    * @defaultValue a non-{@code null} concrete {@link FillApplier} instance
    */
    ReadOnlyObjectProperty<FillApplier<F>> fillApplierProperty();
    /**
     * Gets the {@link FillApplier} to which this {@code FillableSkin}'s
     * {@code Fillable} is assigned.
     * <p>
     * A {@code FillApplier} must be created for one and only one
     * {@code FillableSkin}. This value will only ever go from a
     * non-{@code null} to {@code null} value when the {@code FillableSkin} is
     * removed from the {@code Fillable}, and only as a consequence of a call
     * to {@link Skin#dispose()}.
     * <p>
     * The caller who constructs a {@code FillableSkin} must also construct a
     * {@code FillApplier} and properly establish the relationship between the
     * {@code FillableSkin} and its {@code FillApplier}.
     *
     * @return a non-{@code null} {@code FillApplier}, or {@code null} if
     *         disposed
     */
    default FillApplier<F> getFillApplier() { return fillApplierProperty().get(); }
}

package com.jhenly.juifx.control.applier;

import java.util.Collection;

import com.jhenly.juifx.control.Fillable;

import impl.com.jhenly.juifx.fill.Fill;
import javafx.scene.control.Control;


/**
 * Interface for defining the application of a {@link Fill} on a
 * {@link Fillable} control.
 * <p>
 * A user interface control is abstracted behind the {@link Fillable} interface.
 *
 * @param <F> A subtype of {@code Fillable} that the {@code FillApplier} acts
 *        on. This allows for a {@code FillApplier} implementation to access 
 *        the {@link Fillable} implementation, which is usually a
 *        {@link Control} implementation.
 *        
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public interface FillApplier<F extends Fillable> {
    
    /**
     * Gets the {@link Fillable} to which this {@code FillApplier} is assigned.
     * 
     * A {@code FillApplier} must be created for one and only one
     * {@code Fillable}. This value will only ever go from a non-null to null
     * value when the {@code FillApplier} is removed from the {@code Fillable},
     * and only as a consequence of a call to {@link #dispose()}.
     * <p>
     * The caller who constructs a {@code Fillable} must also construct a
     * {@code FillApplier} and properly establish the relationship between the
     * {@code Fillable} and its {@code FillApplier}.
     *
     * @return a non-null {@code Fillable}, or {@code null} if disposed
     */
    F getFillable();
    
    /**
     * Gets whether or not this {@code FillApplier} instance is currently
     * applying the {@code Fillable} instance's {@code Fill}.
     * 
     * @return {@code true} if this fill applier is currently applying the
     *         fillable's fill, otherwise {@code false}
     */
    boolean isApplying();
    
    /***
     * Allows a {@code FillApplier} to implement any logic necessary to clean
     * itself up after the {@code FillApplier} is no longer needed.
     * <p>
     * The method {@link #getFillable()} should return {@code null} following a
     * call to dispose. Calling dispose twice has no effect.
     */
    void dispose();
    
    /**
     * Attaches a specified {@code FillApplier} to this {@code FillApplier}.
     * <p>
     * Invoking {@link #interpolateAndApply(double)} on a {@code FillApplier}
     * will invoke {@code interpolateAndApply(double)} on any attached
     * {@code FillApplier} instances, as well as any {@code FillApplier}
     * instances attached to them.
     * <p>
     * Due to this, attaching a {@code FillApplier} that creates a circular
     * reference will result in this method being a no-op and returning
     * {@code false}.
     * 
     * @param applier - the {@code FillApplier} to attach to this
     *        {@code FillApplier}
     * @return {@code true} if the specified {@code FillApplier} was attached
     *         to this {@code FillApplier}, otherwise {@code false}
     */
    boolean attach(FillApplier<?> applier);
    
    /**
     * Attaches all of the specified {@code FillApplier} instances to this
     * {@code FillApplier}.
     * <p>
     * Invoking {@link #interpolateAndApply(double)} on a {@code FillApplier}
     * will invoke {@code interpolateAndApply(double)} on any attached
     * {@code FillApplier} instances, as well as any {@code FillApplier}
     * instances attached to them.
     * <p>
     * Due to this, if attaching a {@code FillApplier} were to create a
     * circular reference then it will not be attached.
     * 
     * @param appliers - the collection of {@code FillApplier} instances to
     *        attach to this {@code FillApplier}
     * @return {@code true} if any of the specified {@code FillApplier}
     *         instances were attached to this {@code FillApplier}, otherwise
     *         {@code false}
     */
    boolean attachAll(Collection<FillApplier<?>> appliers);
    
    /**
     * Detaches a specified attached {@code FillApplier} from this
     * {@code FillApplier}, if one is attached.
     * 
     * @param applier - the {@code FillApplier} to detach from this
     *        {@code FillApplier}
     * @return {@code true} if the specified {@code FillApplier} was detached
     *         from this {@code FillApplier}, otherwise {@code false}
     */
    boolean detach(FillApplier<?> applier);
    
    /**
     * Detaches all of the specified attached {@code FillApplier} instances
     * from this {@code FillApplier}, if any are attached.
     * 
     * @param applier - the collection of {@code FillApplier} instances to
     *        detach from this {@code FillApplier}
     * @return {@code true} if any of the specified {@code FillApplier}
     *         instances were detached from this {@code FillApplier}, otherwise
     *         {@code false}
     */
    boolean detachAll(Collection<FillApplier<?>> appliers);
    
    /**
     * Detaches all of the attached {@code FillApplier} instances from this
     * {@code FillApplier}, if any are attached.
     */
    void detachAll();
    
    /**
     * Applies the {@code Fillable} instance's text, shape, stroke, background
     * and border {@code FillSpan} and {@code BorderFillSpan} instances to the
     * {@code Fillable}.
     * <p>
     * Invoking {@link #interpolateAndApply(double)} on a {@code FillApplier}
     * will invoke {@code interpolateAndApply(double)} on any attached
     * {@code FillApplier} instances, as well as any {@code FillApplier}
     * instances attached to them.
     * 
     * @param frac - the interpolate fraction
     */
    void interpolateAndApply(double frac);
    
    /**
     * Resets the {@code Fillable} instance's properties to their original
     * state.
     */
    void resetFillable();
    
}

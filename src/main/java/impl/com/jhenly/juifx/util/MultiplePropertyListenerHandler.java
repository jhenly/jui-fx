package impl.com.jhenly.juifx.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;


/**
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public final class MultiplePropertyListenerHandler {
    
    private static final Consumer<ObservableValue<?>> EMPTY_CHANGE_CONSUMER = e -> {};
    private static final Consumer<Observable> EMPTY_INVALID_CONSUMER = e -> {};
    
    private final Map<ObservableValue<?>, Consumer<ObservableValue<?>>> changeMap;
    private final ChangeListener<Object> propertyChangedListener;
    private final WeakChangeListener<Object> weakPropertyChangedListener;
    
    private final Map<Observable, Consumer<Observable>> invalidMap;
    private final InvalidationListener propertyInvalidatedListener;
    private final WeakInvalidationListener weakPropertyInvalidatedListener;
    
    /** Constructs a new {@code MultiplePropertyListenerHandler}. */
    public MultiplePropertyListenerHandler() {
        changeMap = new HashMap<>(0);
        propertyChangedListener = (observable, oldValue, newValue) -> {
            // because all consumers are chained, this calls each consumer for
            // the given property in turn.
            changeMap.getOrDefault(observable, EMPTY_CHANGE_CONSUMER).accept(observable);
        };
        weakPropertyChangedListener = new WeakChangeListener<>(propertyChangedListener);
        
        invalidMap = new HashMap<>(0);
        propertyInvalidatedListener = observable -> {
            // because all consumers are chained, this calls each consumer for
            // the given property in turn.
            invalidMap.getOrDefault(observable, EMPTY_INVALID_CONSUMER).accept(observable);
        };
        weakPropertyInvalidatedListener = new WeakInvalidationListener(propertyInvalidatedListener);
    }
    
    /**
     * Register to listen to property change events for a specified property.
     * <p>
     * Registered {@link Consumer} instances will be executed in the order in
     * which they are registered. If the specified consumer is {@code null}
     * then this method is a no-op.
     *
     * @param property - the property to listen to change events for
     * @param consumer - the consumer to invoke upon a change event, if
     *        {@code null} then this method is a no-op
     */
    public void registerChangeListener(ObservableValue<?> property, Consumer<ObservableValue<?>> consumer) {
        if (consumer == null) { return; }
        
        // we only add a listener if the propertyReferenceMap does not contain
        // the property (that is, we've added a consumer to this specific
        // property for the first time).
        if (!changeMap.containsKey(property)) {
            property.addListener(weakPropertyChangedListener);
        }
        
        changeMap.merge(property, consumer, Consumer::andThen);
    }
    
    /**
     * Register to listen to property invalidation events for a specified
     * property.
     * <p>
     * Registered {@link Consumer} instances will be executed in the order in
     * which they are registered. If the specified consumer is {@code null}
     * then this method is a no-op.
     * 
     * @param property - the property to listen to invalidation events for
     * @param consumer - the consumer to invoke upon an invalidation event, if
     *        {@code null} then this method is a no-op
     */
    public void registerInvalidationListener(Observable property, Consumer<Observable> consumer) {
        if (consumer == null) { return; }
        
        // we only add a listener if the propertyReferenceMap does not contain
        // the property (that is, we've added a consumer to this specific
        // property for the first time).
        if (!invalidMap.containsKey(property)) {
            property.addListener(weakPropertyInvalidatedListener);
        }
        
        invalidMap.merge(property, consumer, Consumer::andThen);
    }
    
    /** 
     * Removes all registered change listeners on the specified property.
     * 
     * @param property - the property to remove all registered change listeners
     *        from
     * @return the chain of removed change listeners, or {@code null} if no
     *         change listeners were removed
     */
    public Consumer<ObservableValue<?>> unregisterChangeListeners(ObservableValue<?> property) {
        property.removeListener(weakPropertyChangedListener);
        return changeMap.remove(property);
    }
    
    /** 
     * Removes all registered invalidation listeners on the specified property.
     * 
     * @param property - the property to remove all registered invalidation
     *        listeners from
     * @return the chain of removed invalidation listeners, or {@code null} if
     *         no invalidation listeners were removed
     */
    public Consumer<Observable> unregisterInvalidationListeners(Observable property) {
        property.removeListener(weakPropertyInvalidatedListener);
        return invalidMap.remove(property);
    }
    
    /** 
     * Removes all registered listeners on the specified property.
     * 
     * @param property - the property to remove all registered listeners from
     * @return the chain of removed invalidation listeners, or {@code null} if
     *         no invalidation listeners were removed
     */
    public void unregisterListeners(ObservableValue<?> property) {
        unregisterChangeListeners(property);
        unregisterInvalidationListeners(property);
    }
    
    /** Removes all registered listeners on all registered properties. */
    public void dispose() {
        // unhook change listeners and clear the map of change listeners
        changeMap.keySet().forEach(this::unregisterChangeListeners);
        changeMap.clear();
        
        // unhook invalidation listeners and clear the map of invalidation listeners
        invalidMap.keySet().forEach(this::unregisterInvalidationListeners);
        invalidMap.clear();
    }
    
}

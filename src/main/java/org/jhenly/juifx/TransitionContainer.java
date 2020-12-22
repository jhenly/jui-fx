package org.jhenly.juifx;

import java.util.Objects;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;


/**
 * Transition container class with the ability to create and manage multiple
 * transitions. This class also contains convenience methods for manipulating a
 * single contained transition, a few contained transitions or all of the
 * contained transitions.
 *
 * @author Jonathan Henly
 */
public abstract class TransitionContainer {
    /**
     * Contains enums representing the different types of transitions supported by
     * {@link TransitionContainer}.
     */
    public enum Type {
        /**
         * The {@code TransitionType} associated with the {@code TranslateTransition}
         * class
         */
        TRANSLATE(TranslateTransition.class),
        /**
         * The {@code TransitionType} associated with the {@code ScaleTransition} class
         */
        SCALE(ScaleTransition.class);

        private Class<? extends Transition> transitionClass;

        private Type(Class<? extends Transition> transitionClass) {
            this.transitionClass = transitionClass;
        }

        /**
         * Gets the {@code Class} associated with this {@code Type} enum.
         * <p>
         * For example calling {@code Type.TRANSLATE.getTransitionClass()} will return
         * {TranslateTransition.class}.
         *
         * @return the {@code Class} associated with this {@code Type}
         */
        public Class<? extends Transition> getTransitionClass() { return transitionClass; }

    }

    // private enum TransitionName { PROMPT_TRANSLATE, PROMPT_SCALE,
    // UNDERLINE_SCALE; }

    /* Private Constants */
    
    private static final double DO_NOT_SET_X = Double.NEGATIVE_INFINITY;
    private static final double DO_NOT_SET_Y = Double.NEGATIVE_INFINITY;
    
    private static final boolean GET_X = true;
    private static final boolean GET_Y = false;

    // used to distinguish between getFooXY and setFooXY
    private static final int FROM = 0;
    private static final int TO = 1;
    private static final int BY = 2;
    
    /* Private Members */
    
    private Pair[] transitions;
    private ParallelTransition ptran; // the transition that runs the transitions
    private boolean wasPlaying; // set by 'stop()' method to play after adding
    private Duration stopTime; // set by 'stop()' and used by 'playFromStop()'

    /**
     *
     * @return
     */
    public abstract Class<? extends Enum<?>> getTransitionNames();

    /**
     *
     * @param transitionNames
     */
    public TransitionContainer() {
        transitions = new Pair[getTransitionNames().getEnumConstants().length];
    }

    /**
     * Stops any running transitions contained in this {@code TransitionContainer},
     * then creates and adds a transition to this container.
     * <p>
     * This method will not continue playing any stopped transitions after creating
     * and adding the new transition. If that is the desired behavior then
     * {@link #newTransitionAndPlay(Enum, Type, Node, Duration)} should be used, or
     * a call to {@link #play()} should be made directly after calling this method.
     *
     * @param transitionName - the name enum to associate with the newly created
     *        transition, see {@link #getTransitionNames()}
     * @param type - the type of this transition, see {@link Type}
     * @param node - the node the transition will act on
     * @param duration - the duration of the transition
     * @return a reference to {@code this}
     */
    public TransitionContainer newTransition(Enum<?> transitionName, Type type, Node node, Duration duration) {
        // stop transitions and remove old transition associated with name from ptran
        preNew(transitionName);
        
        Transition newTrans = newTransition(type, node, duration);
        
        // handle enum associated Transition-Type pair and add transition to ptran
        postNew(transitionName, newTrans, type);

        return this;
    }

    /**
     * Stops any running transitions contained in this {@code TransitionContainer},
     * then creates and adds a transition to this container, and finally plays the
     * newly added transitions and continues playing any stopped transitions.
     *
     * @param transitionName - the name enum to associate with the newly created
     *        transition, see {@link #getTransitionNames()}
     * @param type - the type of this transition, see {@link Type}
     * @param node - the node the transition will act on
     * @param duration - the duration of the transition
     * @return a reference to {@code this}
     */
    public TransitionContainer newTransitionAndPlay(Enum<?> transitionName, Type type, Node node, Duration duration) {
        newTransition(transitionName, type, node, duration);

        playFromStop();

        return this;
    }

    /* newTransition helper method */
    private Transition newTransition(Type type, Node node, Duration duration) {
        // create and return the new transition
        switch (type) {
            case TRANSLATE:
                return new TranslateTransition(duration, node);
            case SCALE:
                return new ScaleTransition(duration, node);
            default:
                return null;
        }
    }
    
    /* newTransition helper method */
    private void preNew(Enum<?> which) {
        // record current play time and stop transitions
        stop();

        // get the Transition-Type pair associated with the specified enum 'which'
        Pair transTypePair = get(which);

        if (transTypePair != null) {
            Transition t = transTypePair.getTransition();
            // remove old transition from ptran if it exists
            if (t != null) {
                ptran.getChildren().remove(t);
            }
        }

    }

    /* newTransition helper method */
    private void postNew(Enum<?> which, Transition newTrans, Type type) {
        // get Transition-Type pair associated with the specified enum
        Pair transTypePair = get(which);

        /* reuse the existing Transition-Type pair if type equals specified type
         * otherwise create a new pair */
        if (transTypePair != null && transTypePair.getType() == type) {
            transTypePair.setTransition(newTrans);
        } else {
            // associate new Transition-Type pair with the specified enum
            set(which, new Pair(newTrans, type));
        }

        ptran.getChildren().add(newTrans);
    }
    
    
    public double getFromX(Enum<?> transitionName) {
        Transition t = getTransitionOrThrow(transitionName);
        // TASK maybe finish this class
        return 0.0;
    }
    
    /* getFrom*, getBy* and getTo* methods helper */
    private double getFooHelper(Pair transTypePair, boolean getX) {
        // TASK maybe finish this class
        return 0.0;
    }
    
    /**
     * Calls the {@code setFromX(x)} and the {@code setFromY(y)} methods on the
     * transition associated with the specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setFromX(double)} and the
     * {@code setFromY(double)} methods or this method will throw an
     * {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param x - the {@code x} coordinate to set
     * @param y - the {@code y} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setFromX(double)} and
     *         the {@code setFromY(double)} methods
     */
    public TransitionContainer setFromXY(Enum<?> transitionName, double x, double y)
        throws UnsupportedOperationException
    {
        Pair transTypePair = getTransitionTypePairOrThrow(transitionName);
        
        // stop any transitions before setting anything
        stop();
        
        return setFromXY(transTypePair, x, y);
    }
    
    /**
     * Calls the {@code setFromX(double)} method on the transition associated with
     * the specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setFromX(double)} method or
     * this method will throw an {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param x - the {@code x} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setFromX(double)}
     *         method
     */
    public TransitionContainer setFromX(Enum<?> transitionName, double x) {
        return setFromXY(transitionName, x, DO_NOT_SET_Y);
    }
    
    /**
     * Calls the {@code setFromY(double)} method on the transition associated with
     * the specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setFromY(double)} method or
     * this method will throw an {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param y - the {@code y} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setFromY(double)}
     *         method
     */
    public TransitionContainer setFromY(Enum<?> transitionName, double y) {
        return setFromXY(transitionName, DO_NOT_SET_X, y);
    }

    /* setFrom* methods helper */
    private TransitionContainer setFromXY(Pair transTypePair, double x, double y) {
        switch (transTypePair.getType()) {
            case TRANSLATE:
                setFromXY((TranslateTransition) transTypePair.getTransition(), x, y);
                break;
            case SCALE:
                setFromXY((ScaleTransition) transTypePair.getTransition(), x, y);
                break;
            default:
                throw new UnsupportedOperationException(
                    "transition does not support 'setFromX(double)' or 'setFromY(double)' methods.");
        }

        return this;
    }

    /**
     * Calls the {@code setByX(x)} and the {@code setByY(y)} methods on the
     * transition associated with the specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setByX(double)} and the
     * {@code setByY(double)} methods or this method will throw an
     * {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param x - the {@code x} coordinate to set
     * @param y - the {@code y} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setFromX(double)} and
     *         the {@code setByY(double)} methods
     */
    public TransitionContainer setByXY(Enum<?> transitionName, double x, double y)
        throws UnsupportedOperationException
    {
        Pair transTypePair = getTransitionTypePairOrThrow(transitionName);
        
        // stop transitions before setting anything
        stop();
        
        return setByXY(transTypePair, x, y);
    }
    
    /**
     * Calls the {@code setByX(double)} method on the transition associated with the
     * specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setByX(double)} method or
     * this method will throw an {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param x - the {@code x} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setByX(double)}
     *         method
     */
    public TransitionContainer setByX(Enum<?> transitionName, double x) {
        return setByXY(transitionName, x, DO_NOT_SET_Y);
    }
    
    /**
     * Calls the {@code setByY(double)} method on the transition associated with the
     * specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setByY(double)} method or
     * this method will throw an {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param y - the {@code y} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setByY(double)}
     *         method
     */
    public TransitionContainer setByY(Enum<?> transitionName, double y) {
        return setByXY(transitionName, DO_NOT_SET_X, y);
    }
    
    /* setBy* methods helper */
    private TransitionContainer setByXY(Pair transTypePair, double x, double y) {
        switch (transTypePair.getType()) {
            case TRANSLATE:
                setByXY((TranslateTransition) transTypePair.getTransition(), x, y);
                break;
            case SCALE:
                setByXY((ScaleTransition) transTypePair.getTransition(), x, y);
                break;
            default:
                throw new UnsupportedOperationException(
                    "transition does not support 'setFromX(double)' or 'setFromY(double)' methods.");
        }

        return this;
    }

    /**
     * Calls the {@code setToX(x)} and the {@code setToY(y)} methods on the
     * transition associated with the specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setTo(double)} and the
     * {@code setToY(double)} methods or this method will throw an
     * {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param x - the {@code x} coordinate to set
     * @param y - the {@code y} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setFToX(double)} and
     *         the {@code setToY(double)} methods
     */
    public TransitionContainer setToXY(Enum<?> transitionName, double x, double y)
        throws UnsupportedOperationException
    {
        Pair transTypePair = getTransitionTypePairOrThrow(transitionName);
        
        // stop transitions before setting anything
        stop();
        
        return setToXY(transTypePair, x, y);
    }
    
    /**
     * Calls the {@code setToX(double)} method on the transition associated with the
     * specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setToX(double)} method or
     * this method will throw an {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param x - the {@code x} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setToX(double)}
     *         method
     */
    public TransitionContainer setToX(Enum<?> transitionName, double x) {
        return setToXY(transitionName, x, DO_NOT_SET_Y);
    }
    
    /**
     * Calls the {@code setToY(double)} method on the transition associated with the
     * specified enum name.
     * <p>
     * Calling this method will stop any playing transitions in this container, the
     * caller should invoke {@link #playFromStop()} when they want the transitions
     * to continue playing.
     * <p>
     * The associated transition must support the {@code setToY(double)} method or
     * this method will throw an {@code UnsupportedOperationException}.
     *
     * @param transitionName - the name enum associated with the transition to
     *        alter, see {@link #getTransitionNames()}
     * @param y - the {@code y} coordinate to set
     * @return a reference to {@code this}
     * @throws UnsupportedOperationException if the transition associated with the
     *         specified enum name does not support the {@code setToY(double)}
     *         method
     */
    public TransitionContainer setToY(Enum<?> transitionName, double y) {
        return setToXY(transitionName, DO_NOT_SET_X, y);
    }
    
    /* setTo* methods helper */
    private TransitionContainer setToXY(Pair transTypePair, double x, double y) {
        switch (transTypePair.getType()) {
            case TRANSLATE:
                setToXY((TranslateTransition) transTypePair.getTransition(), x, y);
                break;
            case SCALE:
                setToXY((ScaleTransition) transTypePair.getTransition(), x, y);
                break;
            default:
                throw new UnsupportedOperationException(
                    "transition does not support 'setFromX(double)' or 'setFromY(double)' methods.");
        }

        return this;
    }
    
    /* set*XY helper method */
    private TransitionContainer setFooXY(Pair transTypePair, int foo, double x, double y) {
        Transition t = getTransitionOrThrow(transTypePair);
        
        switch (transTypePair.getType()) {
            case TRANSLATE:
                setFooXY(((TranslateTransition) t), foo, x, y);
                break;
            
            case SCALE:
                setFooXY(((ScaleTransition) t), foo, x, y);
                break;

            default:
                throw new UnsupportedOperationException(
                    "transition does not support 'setFromX(double)' or 'setFromY(double)' methods.");
        }
        
        return this;
    }

    /* setFooXY TranslateTransition helper */
    private TransitionContainer setFooXY(TranslateTransition t, int foo, double x, double y) {
        switch (foo) {
            case FROM:
                setFromXY(t, x, y);
                break;
            case TO:
                setToXY(t, x, y);
                break;
            case BY:
                setByXY(t, x, y);
                break;
        }
        
        return this;
    }
    
    /* setFooXY ScaleTransition helper */
    private TransitionContainer setFooXY(ScaleTransition t, int foo, double x, double y) {
        switch (foo) {
            case FROM:
                setFromXY(t, x, y);
                break;
            case TO:
                setToXY(t, x, y);
                break;
            case BY:
                setByXY(t, x, y);
                break;
        }
        
        return this;
    }
    /**
     * Stops all of the transitions in this {@code TransitionContainer} instance.
     * <p>
     * This method also marks the current playing time of the transitions to allow
     * for them to be played from where they were stopped, via the
     * {@link #playFromStop()} method.
     */
    public void stop() {
        // record current play time before stop
        stopTime = ptran.getCurrentTime();
        // signal if the transition was playing before stopping
        wasPlaying = ptran.getStatus() == Animation.Status.RUNNING;
        // stop the animations
        ptran.stop();
    }
    
    /**
     * Plays the transitions in this {@code TransitionContainer} instance.
     * <p>
     * If any of the transitions in this {@code Transitions} instance have been
     * stopped by a call to {@link #stop()}, then this method will behave in the
     * same manner as {@link #playFromStop()}.
     */
    public void play() {
        wasPlaying = false;
        ptran.play();
    }

    /**
     * Plays the transitions in this {@code TransitionContainer} instance from their
     * initial positions in forward direction.
     */
    public void playFromStart() {
        wasPlaying = false; // remove signal if any
        ptran.playFromStart();
    }

    /**
     * Continues playing the transitions in this {@code TransitionContainer}
     * instance from where they were stopped by a call to {@link #stop()}.
     * <p>
     * If none of the transitions in this {@code TransitionContainer} instance were
     * stopped by a call to {@link #stop()}, then this method will behave in the
     * same manner as {@link #play()}.
     */
    public void playFromStop() {
        ptran.playFrom(stopTime);
        play();
    }
    
    
    /* Helper Methods */
    
    // helper Pair methods
    private Pair get(Enum<?> which) { return transitions[which.ordinal()]; }
    private void set(Enum<?> which, Pair pair) { transitions[which.ordinal()] = pair; }
    
    // helper Pair.Transition methods
    private Transition getTransition(Enum<?> which) { return transitions[which.ordinal()].getTransition(); }
    private void setTransition(Enum<?> which, Transition newTran) {
        transitions[which.ordinal()].setTransition(newTran);
    }
    // helper Pair.Type methods
    private Type getType(Enum<?> which) { return transitions[which.ordinal()].getType(); }
    private void setType(Enum<?> which, Type type) { transitions[which.ordinal()].setType(type); }
    
    // throwing Pair helper method
    private Pair getTransitionTypePairOrThrow(Enum<?> transitionName) {
        Enum<?> name = Objects.requireNonNull(transitionName);
        Pair transTypePair = get(name);

        if (transTypePair == null) {
            throw new NullPointerException("the transition name enum, '" + transitionName.name()
                + "', is not associated with any contained transission.");
        }

        return transTypePair;
    }

    // throwing Pair.Transition helper method
    private Transition getTransitionOrThrow(Enum<?> transitionName) {
        Transition transition = getTransitionTypePairOrThrow(transitionName).getTransition();
        if (transition == null) {
            throw new NullPointerException(
                "the transition associated with the transition name enum, '" + transitionName.name() + "', is null.");
        }

        return transition;
    }
    
    // throwing Transition helper method
    private Transition getTransitionOrThrow(Pair transTypePair) {
        Transition transition = transTypePair.getTransition();
        if (transition == null) { throw new NullPointerException("the transition is null."); }

        return transition;
    }

    /* Static Helper Methods */

    private static void setRate(Transition t, double rate) { t.setRate(rate); }

    // TranslateTransition *FromXY method helpers
    private static void setFromXY(TranslateTransition t, double x, double y) {
        if (shouldBeSet(x)) { t.setFromX(x); }
        if (shouldBeSet(y)) { t.setFromY(y); }
    }
    private static double getFrom(TranslateTransition t, boolean getX) { return getX ? t.getFromX() : t.getFromY(); }

    // ScaleTransition *FromXY method helpers
    private static void setFromXY(ScaleTransition t, double x, double y) {
        if (shouldBeSet(x)) { t.setFromX(x); }
        if (shouldBeSet(y)) { t.setFromY(y); }
    }
    private static double getFrom(ScaleTransition t, boolean getX) { return getX ? t.getFromX() : t.getFromY(); }
    
    // TranslateTransition *ToXY method helpers
    private static void setToXY(TranslateTransition t, double x, double y) {
        if (shouldBeSet(x)) { t.setToX(x); }
        if (shouldBeSet(y)) { t.setToY(y); }
    }
    private static double getTo(TranslateTransition t, boolean getX) { return getX ? t.getToX() : t.getToY(); }

    // ScaleTransition *ToXY method helpers
    private static void setToXY(ScaleTransition t, double x, double y) {
        if (shouldBeSet(x)) { t.setToX(x); }
        if (shouldBeSet(y)) { t.setToY(y); }
    }
    private static double getTo(ScaleTransition t, boolean getX) { return getX ? t.getToX() : t.getToY(); }

    // TranslateTransition *ByXY method helpers
    private static void setByXY(TranslateTransition t, double x, double y) {
        if (shouldBeSet(x)) { t.setByX(x); }
        if (shouldBeSet(y)) { t.setByY(y); }
    }
    private static double getBy(TranslateTransition t, boolean getX) { return getX ? t.getByX() : t.getByY(); }
    
    // ScaleTransition *ByXY method helpers
    private static void setByXY(ScaleTransition t, double x, double y) {
        if (shouldBeSet(x)) { t.setByX(x); }
        if (shouldBeSet(y)) { t.setByY(y); }
    }
    private static double getBy(ScaleTransition t, boolean getX) { return getX ? t.getByX() : t.getByY(); }

    // helper method to check if a variable should be set
    private static boolean shouldBeSet(double what) { return what == Double.NEGATIVE_INFINITY; }

    /* helper Transition-Type tuple */
    private static class Pair {
        private Transition tr;
        private Type ty;
        
        public Pair(Transition tr, Type ty) { this.tr = tr; this.ty = ty; }
        
        public Transition getTransition() { return tr; }
        public void setTransition(Transition tr) { this.tr = tr; }
        public Type getType() { return ty; }
        public void setType(Type ty) { this.ty = ty; }
    }

}

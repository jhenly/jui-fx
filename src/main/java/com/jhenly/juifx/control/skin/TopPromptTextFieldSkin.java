/** Copyright (c) 2021, JuiFX All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of JuiFX, any associated website,
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL JUIFX BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package com.jhenly.juifx.control.skin;

import com.jhenly.juifx.control.TopPromptTextField;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.WeakChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


/**
 * Basic skin for the {@link TopPromptTextField}.
 * <p>
 *
 * @author Jonathan Henly
 * @since JuiFX 0.1
 */
public abstract class TopPromptTextFieldSkin extends SkinBase<TopPromptTextField> {
    
    private static final PseudoClass SETTLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("settled"); //$NON-NLS-1$
    private static final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused"); //$NON-NLS-1$
    private static final PseudoClass NOT_EDITABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("not-editable"); //$NON-NLS-1$
    
//    private final TopPromptTextField control;
    
    private StackPane topPane;
    private StackPane promptPane;
    private VBox vbox;
    private HBox defaultPane;
    private Labeled promptNode;
    private Labeled dstubNode;
    private Labeled dtextNode;
    private TextField textField;
    private Line underline;
    
    // transitions
    private TranslateTransition moveTransition;
    private ScaleTransition scaleTransition;
    private ScaleTransition lineTransition;
    
    private ObservableBooleanValue textFieldHasText;
    private ObservableBooleanValue promptAsPromptText;
    
    private String oldPromptText;
    private String currentPromptText;
    
    private boolean focusedWhenUseDefaultTextChanged;
    
    
    private ChangeListener<Boolean> usePromptTextChanged = (observer, old, ne) -> { handlePromptAsPromptText(ne); };
    private WeakChangeListener<Boolean> weakUsePromptTextChanged = new WeakChangeListener<>(usePromptTextChanged);
    
    private ChangeListener<Boolean> editableChanged = (observer, old, ne) -> {
        textField.setFocusTraversable(ne);
        underline.setVisible(ne);
        getSkinnable().pseudoClassStateChanged(NOT_EDITABLE_PSEUDO_CLASS, !ne);
    };
    private WeakChangeListener<Boolean> weakEditableChanged = new WeakChangeListener<>(editableChanged);
    
    
    /**
     * Specifies the prompt that is to be displayed over the text field.
     *
     * @return the prompt property
     */
    public abstract ObjectProperty<Labeled> promptProperty();
    
    /**
     * Specifies the stub that is to be displayed under the text field to the
     * left of the default text.
     *
     * @return the default stub property
     */
    public abstract ObjectProperty<Labeled> defaultStubProperty();
    
    /**
     * Specifies the default value that is to be displayed under the text field
     * to the right of the default stub.
     *
     * @return the default value property
     */
    public abstract ObjectProperty<Labeled> defaultValueProperty();
    
    /**
     * Specifies the {@code TextField} that is to be displayed.
     *
     * @return the {@code TextField} to be displayed
     */
    public abstract TextField getTextField();
    
    /**
     * Skin constructor for a {@code TopPromptTextField} control.
     *
     * @param control the control to apply the skin to
     */
    public TopPromptTextFieldSkin(final TopPromptTextField control) {
        super(control);
        // forward control focus to textfield
        control.setFocusTraversable(false);
        
        topPane = new StackPane();
        vbox = new VBox();
        defaultPane = new HBox();
        textField = getTextField();
        underline = new Line(0.0, 0.0, 1, 0.0);
        
        
        // initialize prompt texts so a NPE isn't thrown later
        oldPromptText = "";
        currentPromptText = "";
        
        textFieldHasText = Bindings.greaterThan(textField.lengthProperty(), 0);
        
        // we manage vbox and underline in layoutChildren
        vbox.setManaged(false);
        underline.setManaged(false);
        
        underline.getStyleClass().add("underline"); //$NON-NLS-1$
        underline.setVisible(false);
        
        defaultPane.setFillHeight(true);
        defaultPane.getStyleClass().add("default-pane"); //$NON-NLS-1$
        
        vbox.getStyleClass().add("vbox"); //$NON-NLS-1$
        vbox.setFillWidth(true);
        vbox.setAlignment(Pos.BOTTOM_LEFT);
        
        vbox.getChildren().addAll(textField, defaultPane);
        
        // add vbox and underline to top stack pane
        topPane.getChildren().addAll(vbox, underline);
        
        // initialize underline transition before updating children
        initUnderlineTransition();
        
        // bind promptAsPromptText before updating children
        promptAsPromptText = new BooleanBinding()
        {
            {
                bind(control.usePromptAsPromptTextProperty());
            }
            @Override
            protected boolean computeValue() {
                boolean use = control.usePromptAsPromptTextProperty().get();
                return use;
            }
        };
        
        control.pseudoClassStateChanged(NOT_EDITABLE_PSEUDO_CLASS, !control.editableProperty().get());
        
        // add prompt, defaultStub and defaultText (if not null) to top pane
        updateChildren();
        
        getChildren().add(topPane);
        
        setUpListenersAndHandlers();
    }
    
    /* register most change listeners */
    private void setUpListenersAndHandlers() {
        registerChangeListener(getSkinnable().useDefaultValueProperty(), o -> useDefaultTextChanged());
        registerChangeListener(textField.focusedProperty(), o -> onTextFieldFocusEvent((ObservableBooleanValue) o));
//        registerChangeListener(textField.editableProperty(), e -> {});
        
        registerChangeListener(promptProperty(), e -> updateChildren());
        registerChangeListener(defaultStubProperty(), e -> updateChildren());
        registerChangeListener(defaultValueProperty(), e -> updateChildren());
        
        // listen for change in textField's editable state
        getSkinnable().editableProperty().addListener(weakEditableChanged);
        
        // allows us to be notified of when tf's prompt should change
        getSkinnable().usePromptAsPromptTextProperty().addListener(weakUsePromptTextChanged);
    }
    
    /* updates prompt, defaultStub and defaultText */
    private void updateChildren() {
        Labeled newPrompt = promptProperty().get();
        Labeled newDefaultStub = defaultStubProperty().get();
        Labeled newDefaultText = defaultValueProperty().get();
        
        if (newPrompt == null || !newPrompt.equals(promptNode)) {
            updatePromptChild(newPrompt);
        }
        
        if (newDefaultStub == null || !newDefaultStub.equals(dstubNode)) {
            updateDefaultStubChild(newDefaultStub);
        }
        
        if (newDefaultText == null || !newDefaultText.equals(dtextNode)) {
            updateDefaultValueChild(newDefaultText);
        }
        
        // TopPromptTextField is always settled when no prompt exists
        getSkinnable().pseudoClassStateChanged(SETTLED_PSEUDO_CLASS, newPrompt == null);
    }
    
    /* prompt update children helper */
    private void updatePromptChild(Labeled newPrompt) {
        // we're here because prompt property changed, so...
        removeChild(promptPane, promptNode);
        cleanUpPromptTransitions();
        removeChild(topPane, promptPane);
        
        if (newPrompt != null) {
            promptNode = newPrompt;
            promptPane = new StackPane(promptNode);
            // we'll manage promptPane in layoutChildren, not SkinBase
            promptPane.setManaged(false);
            promptPane.setAlignment(Pos.CENTER_LEFT);
            
            // add css style classes
            promptPane.getStyleClass().add("prompt-pane"); //$NON-NLS-1$
            promptNode.getStyleClass().add("prompt"); //$NON-NLS-1$
            
            topPane.getChildren().add(promptPane);
            
            // initialize prompt node transitions
            initPromptTransitions();
            
            promptPane.setVisible(false);
            
            Rectangle clip = new Rectangle();
            clip.setSmooth(false);
            clip.widthProperty().bind(textField.widthProperty());
            clip.heightProperty().bind(textField.heightProperty());
            
            promptPane.setClip(clip);
            
            // promptNode.clipProperty().bind(textField.clipProperty());
            
            handlePromptAsPromptText(promptAsPromptText.get());
        } else {
            promptPane = null;
            promptNode = null;
            moveTransition = null;
            scaleTransition = null;
        }
        
    }
    
    /* default stub update children helper */
    private void updateDefaultStubChild(Labeled newDefaultStub) {
        // we're here because default stub changed, so...
        removeChild(defaultPane, dstubNode);
        
        if (newDefaultStub != null) {
            dstubNode = newDefaultStub;
            dstubNode.setAlignment(Pos.BOTTOM_LEFT);
            dstubNode.getStyleClass().add("default-stub");
            // dstubNode.setMinWidth(dstubNode.prefWidth(-1));
            
            // insert dstubNode at 0 so that it's left of default text
            defaultPane.getChildren().add(0, dstubNode);
            // set dstubNode to never grow horizontally so default value will
            HBox.setHgrow(dstubNode, Priority.NEVER);
            
        } else {
            dstubNode = null;
        }
        
    }
    
    /* defaultText update children helper */
    private void updateDefaultValueChild(Labeled newDefaultText) {
        // we're here because default text changed, so...
        removeChild(defaultPane, dtextNode);
        
        if (newDefaultText != null) {
            dtextNode = newDefaultText;
            dtextNode.setAlignment(Pos.BOTTOM_LEFT);
            dtextNode.getStyleClass().add("default-text"); //$NON-NLS-1$
            defaultPane.getChildren().add(dtextNode);
            
            dtextNode.setMinWidth(0.0);
            dtextNode.setTooltip(new Tooltip(dtextNode.getText()));
            
            // we want dtextNode to grow and not dstubNode
            HBox.setHgrow(dtextNode, Priority.ALWAYS);
            // fill textfield if use default text is true
            useDefaultTextChanged();
        } else {
            dtextNode = null;
        }
        
    }
    
    /* helper method that removes labeled node */
    private void removeChild(Pane pane, Node node) {
        if (pane != null && node != null) {
            pane.getChildren().remove(node);
        }
    }
    
    /* helper that handles prompt node and tf's prompt text when promptAsPromptText
     * changes */
    private void handlePromptAsPromptText(boolean promptAsPromptText) {
        // check if we started using prompt as prompt text
        if (promptAsPromptText) {
            if (promptPane == null) { return; }
            
            // remember tf's prompt text
            oldPromptText = textField.getPromptText();
            // set tf's prompt text to prompt's text
            textField.setPromptText(promptNode.getText());
            
            if (textFieldHasText.get()) {
                currentPromptText = textField.getPromptText();
                textField.setPromptText("");
                jumpToEndOfTransitions();
                getSkinnable().pseudoClassStateChanged(SETTLED_PSEUDO_CLASS, true);
                
                getSkinnable().requestLayout();
                promptPane.setVisible(true);
            } else {
                
                getSkinnable().pseudoClassStateChanged(SETTLED_PSEUDO_CLASS, false);
            }
        } else {
            // no longer using prompt as prompt text, revert back to original
            textField.setPromptText(oldPromptText);
        }
    }
    
    /* updates text field text depending on control.useDefaultText */
    private void useDefaultTextChanged() {
        if (dtextNode == null) { return; }
        
        if (textField.isFocused()) {
            // mark that we need to update textfield's text after it loses focus
            focusedWhenUseDefaultTextChanged = true;
            return;
        }
        
        if (getSkinnable().useDefaultValueProperty().get()) {
            // if TF is empty then fill it with default value text
            if (!textFieldHasText.get()) {
                textField.setText(dtextNode.getText());
            }
            
            // move prompt up and jump transitions to end
            if (promptPane != null) {
                jumpToEndOfTransitions();
                promptPane.setVisible(true);
                getSkinnable().requestLayout();
                getSkinnable().pseudoClassStateChanged(SETTLED_PSEUDO_CLASS, true);
            }
            
        } else {
            // TF is no longer using default text
            if (textFieldHasText.get() && textFieldHasDefaultText()) {
                // clear TF if TF's text is the default text
                textField.clear();
                
                // move prompt down and jump transitions to start
                if (promptPane != null) {
                    jumpToStartOfTransitions();
                    // prompt's visibility depends on use prompt as prompt text
                    promptPane.setVisible(getSkinnable().usePromptAsPromptTextProperty().get());
                    getSkinnable().requestLayout();
                    getSkinnable().pseudoClassStateChanged(SETTLED_PSEUDO_CLASS, false);
                }
            }
        }
        
    }
    
    /* helper that jumps to the end of all transitions */
    private void jumpToEndOfTransitions() {
        moveTransition.jumpTo(moveTransition.getDuration());
        scaleTransition.jumpTo(scaleTransition.getDuration());
        lineTransition.jumpTo(lineTransition.getDuration());
    }
    
    /* helper that jumps to the start of all transitions */
    private void jumpToStartOfTransitions() {
        moveTransition.jumpTo(Duration.ZERO);
        scaleTransition.jumpTo(Duration.ZERO);
        lineTransition.jumpTo(Duration.ZERO);
    }
    
    private void cleanUpPromptTransitions() {
        if (moveTransition != null) {
            moveTransition.stop();
            moveTransition.setOnFinished(null);
        }
        if (scaleTransition != null) {
            scaleTransition.stop();
            scaleTransition.setOnFinished(null);
        }
    }
    
    /* convenience method */
    private boolean textFieldHasDefaultText() {
        return dtextNode != null && textField.getText().equals(dtextNode.getText());
    }
    
    /* helper that initializes move and scale transitions */
    private void initPromptTransitions() {
        Duration duration = Duration.millis(getSkinnable().getPromptTransitionDuration());
        
        moveTransition = new TranslateTransition(duration, promptPane);
        moveTransition.setFromY(0.0);
        moveTransition.setFromX(0.0);
        
        scaleTransition = new ScaleTransition(duration, promptPane);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
    }
    
    /* helper that initializes underline's scale transition */
    private void initUnderlineTransition() {
        Duration duration = Duration.millis(getSkinnable().getPromptTransitionDuration());
        
        lineTransition = new ScaleTransition(duration, underline);
        lineTransition.setFromX(1.0);
        lineTransition.setFromY(1.0);
    }
    
    /* handles the transitions and settled pseudo class state */
    private void onTextFieldFocusEvent(ObservableBooleanValue focus) {
        if (!textField.isEditable()) {
            giveFocusToSomethingElse();
            return;
        }
        
        // signal CSS 'top-prompt-text-field:focused' state
        getSkinnable().pseudoClassStateChanged(FOCUSED_PSEUDO_CLASS, focus.get());
        
        if (focus.get()) {
            updatePromptOnTextFieldInFocus();
        } else {
            // update textfield default text if it was focused when
            // useDefaultTextChanged was called
            if (focusedWhenUseDefaultTextChanged) {
                useDefaultTextChanged();
            }
            
            updatePromptOnTextFieldOutFocus();
        }
        
    }
    
    /* used when TF is not editable and gains focus */
    private void giveFocusToSomethingElse() {
        // try to give focus to a bottom relative first
        if (dstubNode != null) { dstubNode.requestFocus(); }
        if (dtextNode != null) { dtextNode.requestFocus(); }
        // then try to give it to an upper relative
        if (promptPane != null) { promptPane.requestFocus(); }
    }
    
    /* onTextFieldFocusEvent in focus helper */
    private void updatePromptOnTextFieldInFocus() {
        
        // if textfield has no content then we need to move prompt up
        if (!textFieldHasText.get() && promptPane != null) {
            // handle textfield showing prompt text before prompt re-settles
            if (promptAsPromptText.get()) {
                currentPromptText = promptNode.getText();
            } else {
                currentPromptText = textField.getPromptText();
            }
            
            textField.setPromptText("");
            promptPane.setVisible(true);
            
            moveTransition.setRate(1.0);
            scaleTransition.setRate(1.0);
            
            moveTransition.setOnFinished(event -> {
                // signal that the prompt node has settled above the textfield
                getSkinnable().pseudoClassStateChanged(SETTLED_PSEUDO_CLASS, true);
            });
            
            scaleTransition.play();
            moveTransition.play();
        } else {
            // prompt pane is already above textfield
            
        }
        
        // always show underline transition
        underline.setVisible(true);
        lineTransition.setRate(1.0);
        lineTransition.setOnFinished(null);
        lineTransition.play();
    }
    
    /* onTextFieldFocusEvent out focus helper */
    private void updatePromptOnTextFieldOutFocus() {
        
        // if textfield has no content then we need to go from top to bottom
        if (!textFieldHasText.get() && promptPane != null) {
            // signal that the prompt node is no longer settled
            getSkinnable().pseudoClassStateChanged(SETTLED_PSEUDO_CLASS, false);
            
            // animate prompt back to its original position
            moveTransition.setRate(-1.0);
            scaleTransition.setRate(-1.0);
            
            // handle first time deleting default prompt text
            if (promptAsPromptText.get()) {
                if (!textField.getPromptText().equals("")) {
                    currentPromptText = textField.getPromptText();
                    textField.setPromptText("");
                }
            }
            
            moveTransition.setOnFinished(event -> {
                promptPane.setVisible(false);
                if (promptAsPromptText.get()) {
                    textField.setPromptText(currentPromptText);
                }
            });
            
            scaleTransition.play();
            moveTransition.play();
        }
        
        // always show underline transition
        lineTransition.setRate(-1.0);
        lineTransition.setOnFinished(event -> { underline.setVisible(false); });
        lineTransition.play();
    }
    
    /** {@inheritDoc} */
    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        // let SkinBase layout all managed children first
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
        
        // lay out the vbox normally but use VPos.BOTTOM instead of CENTER
        layoutInArea(vbox, contentX, contentY, contentWidth, contentHeight, 0, HPos.CENTER, VPos.BOTTOM);
        
        // lay out underline at the bottom center of the textfield
        final double dtextNodeHeight = (dtextNode == null) ? 0.0 : dtextNode.prefHeight(-1);
        final double dstubNodeHeight = (dstubNode == null) ? 0.0 : dstubNode.prefHeight(-1);
        final double maxUnderNodeHeight = snapSizeY(Math.max(dtextNodeHeight, dstubNodeHeight));
        final double tfOffset = snapPositionY(contentHeight - maxUnderNodeHeight);
        
        underline.setLayoutY(snapPositionY(tfOffset - 1.0 + contentY));
        underline.setLayoutX(snapSizeX(textField.getWidth() / 2.0) + contentX);
        
        if (promptPane != null) {
            promptPane.resize(snapSizeX(promptPane.prefWidth(-1)), snapSizeY(promptPane.prefHeight(-1)));
            
            Insets tfInsets = textField.getInsets();
            // layout the prompt pane over the textfield prompt, account for
            // textfield insets, add 1 so that snapPosition does not round down
            promptPane.setLayoutX(snapPositionX(textField.getLayoutX() + tfInsets.getLeft() + 1 + contentX));
            promptPane.setLayoutY(snapPositionY(vbox.getLayoutY() + textField.getLayoutY() + 1
                                                + (textField.getHeight() - textField.getBaselineOffset())));
            
            handleTransitionsInLayout(contentX, contentY, contentWidth, contentHeight);
        }
        
        // handle line transition separate from move and scale
        final double lineTargetX = snapSizeX(vbox.getWidth() / (underline.getStrokeWidth() + 1));
        final double lineTargetY = 1.0;
        lineTransition.setToX(lineTargetX);
        lineTransition.setToY(lineTargetY);
        
        handleTransition(lineTransition, underline, lineTargetX, lineTargetY, 0.0, 0.0, false);
    }
    
    /** layoutChildren helper method that handles move and scale transitions */
    private void
    handleTransitionsInLayout(double contentX, double contentY, double contentWidth, double contentHeight)
    {
        final TopPromptTextField me = getSkinnable();
        final double textfieldHeight = snapSizeY(textField.prefHeight(-1));
        final Insets tfInsets = textField.getInsets();
        
        // each time the layout is done, recompute the prompt pane position
        // and apply it to the move and scale targets as well as the line
        // position
        final double moveTargetX = snapPositionX(-1 * promptPane.getLayoutX() + me.getPromptTranslateX() + contentX);
        final double moveTargetY = snapPositionY(-1 * (textfieldHeight - tfInsets.getTop()) + me.getPromptTranslateY());
        moveTransition.setToX(moveTargetX);
        moveTransition.setToY(moveTargetY);
        
        final double scaleTargetX = me.getPromptScaleX();
        final double scaleTargetY = me.getPromptScaleY();
        scaleTransition.setToX(scaleTargetX);
        scaleTransition.setToY(scaleTargetY);
        
        handleTransition(moveTransition, promptPane, moveTargetX, moveTargetY, 0.0, 0.0, true);
        handleTransition(scaleTransition, promptPane, scaleTargetX, scaleTargetY, 1.0, 1.0, false);
    }
    
    /* handleTransitionInLayout helper */
    private void handleTransition(Transition transition, Node node, double targetX, double targetY, double defX,
                                  double defY, boolean translate)
    {
        final boolean tfHasText = textFieldHasText.get();
        
        // if the transition is running, it must be restarted for the value to
        // be properly updated
        if (transition.getStatus() == Animation.Status.RUNNING) {
            final Duration currentMoveTime = transition.getCurrentTime();
            transition.stop();
            transition.playFrom(currentMoveTime);
        } else {
            // if the transition is not running, simply apply values
            if (translate) {
                node.setTranslateX(tfHasText ? targetX : defX);
                node.setTranslateY(tfHasText ? targetY : defY);
            } else {
                node.setScaleX(tfHasText ? targetX : defX);
                node.setScaleY(tfHasText ? targetY : defY);
            }
        }
        
    }
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        System.out.println("-+-+ RUNNING TopPromptTextFieldSkin.dispose() +-+-");
        // clean up change listeners
        getSkinnable().editableProperty().removeListener(editableChanged);
        getSkinnable().usePromptAsPromptTextProperty().removeListener(usePromptTextChanged);
        
        super.dispose();
    }
    
    /** {@inheritDoc} */
    @Override
    protected double
    computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset)
    {
        // only care about vbox and prompt stack pane heights
        final double upph = vbox.prefHeight(-1);
        final double ptph = (promptPane == null) ? 0.0 : promptPane.prefHeight(-1);
        
        return topInset + ptph + upph + bottomInset;
    }
    
    /** {@inheritDoc} */
    @Override
    protected double
    computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset)
    {
        // prefHeight is minHeight plus wiggle room
        final double minHeight = computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
        
        // add 25% of promptPane's pref height as wiggle room
        final double wiggle = (promptPane == null) ? 0.0 : 0.25 * promptPane.prefHeight(-1);
        
        // we may end up adding 25% of textFields pref height instead
        // final double tfph = textField.prefHeight(-1);
        
        return minHeight + wiggle;
    }
    
    /** {@inheritDoc} */
    @Override
    protected double
    computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset)
    {
        final double superMinWidth = super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset);
        
        // only care about vbox and prompt stack pane widths
        final double minWidth = Math.min(vbox.prefWidth(-1), (promptPane == null) ? 0.0 : promptPane.prefWidth(-1));
        
        return Math.max(superMinWidth, minWidth);
    }
    
}

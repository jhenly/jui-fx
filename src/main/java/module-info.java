/**
 * Defines the controls included in JuiFX.
 *
 * @since JuiFX 1.0
 */
module com.jhenly.juifx.controls {
    
    requires java.desktop;
    
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.base;
    
    exports com.jhenly.juifx;
    exports com.jhenly.juifx.animation;
    exports com.jhenly.juifx.control;
    exports com.jhenly.juifx.control.event;
    exports com.jhenly.juifx.control.skin;
    exports com.jhenly.juifx.layout;
    
    exports impl.com.jhenly.juifx.fill to com.jhenly.juifx.animation, com.jhenly.juifx.control;
}

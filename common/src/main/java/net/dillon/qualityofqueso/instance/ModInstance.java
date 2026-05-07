package net.dillon.qualityofqueso.instance;

/**
 * A representation of an "instance", which does things. Instances are utility classes for exposing methods and variables, specifically related to Quality of Queso, hence this class being called {@code QuesoScreen.}
 */
public interface ModInstance {

    /**
     * Simplifies the process of having to call "this.screen" each time.
     */
    QuesoScreen instance();

    /**
     * @return the default widget handler.
     */
    default WidgetHandler widgetHandler() {
        return (WidgetHandler) instance().getScreen();
    }
}
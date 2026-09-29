package net.dillon.qualityofqueso.event;

/**
 * A representation of an "instance", which does things. Instances are utility classes for exposing methods and variables, specifically related to Quality of Queso, hence this class being called {@code QuesoScreen.}
 */
public interface ModInstance {

    /**
     * Simplifies the process of having to call "this.screen" each time.
     */
    QuesoScreenHolder holder();

    /**
     * @return the default widget handler.
     */
    default WidgetHandler widgetHandler() {
        return (WidgetHandler) holder().screen();
    }
}
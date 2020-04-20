package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

/**
 * This class contains all the methods needed to set-up, maintain, and close a LWJGL display.
 */
public class DisplayManager {

    private static final int WIDTH = 1280,
                             HEIGHT = 720,
                             FPS_CAP = 120;
    private static final String TITLE = "LWJGL 2 Tutorial!";

    private static long lastFrameTime;
    private static float delta;


    /**
     * Creates a display window on which we can render our game. The dimensions
     * of the window are determined by setting the display mode. By using
     * "glViewport" we tell OpenGL which part of the window we want to render
     * our game onto. We indicated that we want to use the entire window.
     */
    public static void createDisplay(){

        ContextAttribs attribs = new ContextAttribs(3,2)
            .withForwardCompatible(true)
            .withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), attribs);
            Display.setTitle(TITLE);

        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT);

        lastFrameTime = getCurrentTime();
    }

    /**
     * This method is used to update the display at the end of every frame. When
     * we have set up a rendering process this method will display whatever
     * we've been rendering onto the screen. The "sync" method is used here to
     * cap the frame rate. Without this the computer would just try to run the
     * game as fast as it possibly can, doing more work than it needs to.
     */
    public static void updateDisplay(){
        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getFrameTimeSeconds() {
        return delta;
    }

    /**
     * This closes the window when the game is closed.
     */
    public static void closeDisplay() {
        Display.destroy();
    }

    private static long getCurrentTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }
}

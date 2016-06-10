package view;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class ViewManager {

    private Batch batch;
    private BitmapFont font;

    private Texture test0;
    private Texture test1;
    private Texture test2;
    private float rotationRadians = 0;
    private boolean fullscreen = false;

    public ViewManager() {
        try {
            setDisplayMode(800, 600, false);

            Display.create();

            System.out.println("OpenGL context created! Version: " + GL11.glGetString(GL11.GL_VERSION));
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        this.batch = new Batch();
        batch.resize(Display.getWidth(), Display.getHeight());

        try {
            font = new BitmapFont(getClass().getResource("/font/font.fnt"), getClass().getResource("/font/font.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        this.test0 = new Texture(getClass().getResource("/textures/test0.png"));
        this.test1 = new Texture(getClass().getResource("/textures/test1.png"));
        this.test2 = new Texture(getClass().getResource("/textures/test2.png"));
    }

    public void setDisplayMode(int width, int height, boolean fullscreen) {
        if ((Display.getDisplayMode().getWidth() == width) &&
                (Display.getDisplayMode().getHeight() == height) &&
                (Display.isFullscreen() == fullscreen)) {
            return;
        }

        try {
            DisplayMode targetDisplayMode = null;

            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;

                for (int i = 0; i < modes.length; i++) {
                    DisplayMode current = modes[i];

                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }

                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                                (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width,height);
            }

            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
                return;
            }

            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
            Display.setResizable(true);
            Display.setTitle("Kabuom! Tower Defense");
            Display.setVSyncEnabled(true);

        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
        }
    }

    public void render(float deltaTime) {
        if (Display.wasResized()) {
            onResize(Display.getWidth(), Display.getHeight());
        }

        while(Keyboard.next()){
            if(Keyboard.getEventKeyState()){
                int key = Keyboard.getEventKey();
                char c = Keyboard.getEventCharacter();

                if (key == Keyboard.KEY_F11) {
                    fullscreen = !fullscreen;

                    try {
                        if (fullscreen) {
                            int w0 = Toolkit.getDefaultToolkit().getScreenSize().width;
                            int h0 = Toolkit.getDefaultToolkit().getScreenSize().height;
                            setDisplayMode(w0, h0, true);
                            onResize(w0, h0);
                        } else {
                            setDisplayMode(800, 600, false);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        GL11.glClearColor(0f, 0f, 0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        batch.begin();

        /*float xStep = Display.getWidth() / 5;
        float yStep = Display.getHeight() / 5;
        for (float x = 0f; x < Display.getWidth(); x += xStep) {
            for (float y = 0f; y < Display.getHeight(); y += yStep) {
                Texture tex = null;
                int i = (int) x % 3;
                if (i == 0)
                    tex = test0;
                else if (i == 1)
                    tex = test1;
                else if (i == 2)
                    tex = test2;

                batch.draw(tex, x, y, xStep - (xStep / 5f), yStep - (yStep / 5f));
            }
        }*/

        rotationRadians += Math.toRadians(deltaTime * 50);

        batch.draw(test0, 0, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);
        batch.draw(test1, 200, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);
        batch.draw(test2, 400, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);

        font.drawText(batch, "Hallo Kabuom!     abcdefghijklmnopqrstuvwxyzß", 100, 300);

        batch.end();

        Display.update();
        Display.sync(60);
    }

    private void onResize(int width, int height) {
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        batch.resize(Display.getWidth(), Display.getHeight());
    }

    public void dispose() {
        Display.destroy();
    }

    public boolean isCloseRequested() {
        return Display.isCloseRequested();
    }

}

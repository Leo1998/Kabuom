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

    public static BitmapFont font;
    public static ITexture test0;
    public static ITexture test1;
    public static ITexture test2;
    public static ITexture buttonMainTexture;
    public static ITexture buttonPressedTexture;

    public static void load (){
        try {
            font = new BitmapFont(ViewManager.class.getResource("/font/font.fnt"), ViewManager.class.getResource("/font/font.png"));

            test0 = new Texture(ViewManager.class.getResource("/textures/test0.png"));
            test1 = new Texture(ViewManager.class.getResource("/textures/test1.png"));
            test2 = new Texture(ViewManager.class.getResource("/textures/test2.png"));
            buttonMainTexture = new Texture(ViewManager.class.getResource("/textures/viewTextures/mainButton.png"));
            buttonPressedTexture = new Texture(ViewManager.class.getResource("/textures/viewTextures/pressedButton.png"));
        } catch(IOException e) {
            e.printStackTrace();
            font = null;
        }
    }

    private View currentView;

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

        load();

        this.batch = new Batch();
        batch.resize(Display.getWidth(), Display.getHeight());

        currentView = new TestView();
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


        currentView.render(deltaTime,batch);

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

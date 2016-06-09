package view;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class ViewManager {

    private Batch batch;

    public ViewManager() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.setResizable(true);
            Display.setTitle("Kabuom! Tower Defense");

            Display.create();

            System.out.println("OpenGL context created! Version: " + GL11.glGetString(GL11.GL_VERSION));
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //GL11.glDisable(GL11.GL_DEPTH_TEST);

        this.batch = new Batch();
        batch.resize(Display.getWidth(), Display.getHeight());

        Texture tex = new Texture(getClass().getResource("/textures/test.png"));
    }

    public void render(float deltaTime) {
        if (Display.wasResized()) {
            GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
            batch.resize(Display.getWidth(), Display.getHeight());
        }

        GL11.glClearColor(0f, 0f, 0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float xStep = Display.getWidth() / 100;
        float yStep = Display.getHeight() / 100;
        for (float x = 0f; x < Display.getWidth(); x += xStep) {
            for (float y = 0f; y < Display.getHeight(); y += yStep) {
                batch.draw(x, y, xStep - (xStep / 5f), yStep - (yStep / 5f), 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f);
            }
        }

        batch.end();

        Display.update();
    }

    public void dispose() {
        Display.destroy();
    }

    public boolean isCloseRequested() {
        return Display.isCloseRequested();
    }

}

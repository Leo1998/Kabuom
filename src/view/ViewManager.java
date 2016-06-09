package view;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class ViewManager {

    private Batch batch;

    private Texture test0;
    private Texture test1;
    private Texture test2;

    public ViewManager() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.setResizable(true);
            Display.setTitle("Kabuom! Tower Defense");
            Display.setVSyncEnabled(true);

            Display.create();

            System.out.println("OpenGL context created! Version: " + GL11.glGetString(GL11.GL_VERSION));
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //GL11.glDisable(GL11.GL_DEPTH_TEST);

        this.batch = new Batch();
        batch.resize(Display.getWidth(), Display.getHeight());

        this.test0 = new Texture(getClass().getResource("/textures/test0.png"));
        this.test1 = new Texture(getClass().getResource("/textures/test1.png"));
        this.test2 = new Texture(getClass().getResource("/textures/test2.png"));
    }

    public void render(float deltaTime) {
        if (Display.wasResized()) {
            GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
            batch.resize(Display.getWidth(), Display.getHeight());
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

        batch.draw(test0, 0, 0, 200, 200);
        batch.draw(test1, 200, 0, 200, 200);
        batch.draw(test2, 400, 0, 200, 200);

        batch.end();

        Display.update();
        Display.sync(60);
    }

    public void dispose() {
        Display.destroy();
    }

    public boolean isCloseRequested() {
        return Display.isCloseRequested();
    }

}

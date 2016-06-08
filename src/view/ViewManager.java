package view;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class ViewManager {

    public ViewManager() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.setResizable(true);
            Display.setTitle("Kabuom! Tower Defense");

            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void render(float deltaTime) {
        GL11.glClearColor(1f, 0f, 0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glColor3f(0.5f,0.5f,1.0f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(100,100);
        GL11.glVertex2f(100+200,100);
        GL11.glVertex2f(100+200,100+200);
        GL11.glVertex2f(100,100+200);
        GL11.glEnd();

        Display.update();
    }

    public void dispose() {
        Display.destroy();
    }

    public boolean isCloseRequested() {
        return Display.isCloseRequested();
    }

}

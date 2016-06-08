package view;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class ViewManager {

    private static ShaderProgram createShader() {
        String vertexSource = "void main() {" +
                "gl_Position = gl_Vertex;" +
                "}";

        String fragmentSource = "void main() {" +
                "gl_FragColor = vec4(0, 1, 0, 1);" +
                "}";

        return new ShaderProgram(vertexSource, fragmentSource);
    }

    private ShaderProgram shader;

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

        shader = createShader();
    }

    public void render(float deltaTime) {
        if (Display.wasResized()) {
            GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        }

        GL11.glClearColor(1f, 0f, 0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        shader.use();

        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(0.0f,0.0f);
        GL11.glVertex2f(0.5f, 1.0f);
        GL11.glVertex2f(1.0f,0.0f);
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

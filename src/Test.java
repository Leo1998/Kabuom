import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Test {

    public void run() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.setResizable(true);
            Display.setTitle("Kabuom! Tower Defense");

            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        double deltaTime;
        long startTime = System.currentTimeMillis();

        while (!Display.isCloseRequested()) {
            long currentTime = System.currentTimeMillis();
            deltaTime = (double) ((currentTime - startTime) / 1000D);
            startTime = currentTime;

            System.out.println("Delta: " + deltaTime);

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

        Display.destroy();
    }

    public static void main(String[] argv) {
        Test test = new Test();
        test.run();
    }
}

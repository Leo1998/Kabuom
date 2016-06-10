package view.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class PostProcessingManager {

    public static boolean isSupported() {
        return FrameBuffer.isSupported();
    }

    private FrameBuffer mainFB;
    private Batch batch;

    public PostProcessingManager(Batch batch) {
        this.batch = batch;
    }

    private boolean isEnabled() {
        return false && isSupported();
    }

    public void resize(int width, int height) {
        if (isEnabled()) {
            try {
                if (mainFB != null) {
                    mainFB.dispose();
                }
                mainFB = new FrameBuffer(width, height);

            } catch(LWJGLException e) {
                e.printStackTrace();;
            }
        }
    }

    public void begin() {
        if (isEnabled()) {
            mainFB.begin();

            GL11.glClearColor(0f, 0f, 0f, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        }
    }

    public void end() {
        if (isEnabled()) {
            mainFB.end();

            batch.begin();
            batch.draw(mainFB, 0, 0, Display.getWidth(), Display.getHeight());
            batch.end();
        }
    }

}

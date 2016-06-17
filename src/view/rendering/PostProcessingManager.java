package view.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;

public class PostProcessingManager {

    public static boolean isSupported() {
        return FrameBuffer.isSupported();
    }

    private FrameBuffer sceneFB;
    private Batch batch;
    private boolean enabled = true;

    private ArrayList<PostProcessingEffect> effects = new ArrayList<>();

    public PostProcessingManager(Batch batch) {
        this.batch = batch;

        //effects.add(new RadialBlurEffect());
    }

    public void resize(int width, int height) {
        if (isEnabled()) {
            try {
                if (sceneFB != null) {
                    sceneFB.dispose();
                }
                sceneFB = new FrameBuffer(width, height, GL11.GL_LINEAR, GL12.GL_CLAMP_TO_EDGE);

            } catch(LWJGLException e) {
                e.printStackTrace();
            }
        }
    }

    public void begin() {
        if (isEnabled()) {
            sceneFB.begin();

            GL11.glClearColor(0f, 0f, 0f, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        }
    }

    public void end() {
        if (isEnabled()) {
            sceneFB.end();

            if (!effects.isEmpty()) {
                if (effects.size() > 1) {
                    throw new IllegalStateException("Only one Effect is supported yet!;");
                }
                for (PostProcessingEffect e : effects) {
                    e.render(sceneFB, batch);
                }
            } else {
                batch.begin();
                batch.draw(sceneFB, 0, 0, Display.getWidth(), Display.getHeight());
                batch.end();
            }
        }
    }

    public boolean isEnabled() {
        return enabled && isSupported();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

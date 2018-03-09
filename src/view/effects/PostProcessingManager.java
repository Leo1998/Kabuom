package view.effects;

import controller.Config;
import controller.Controller;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import view.math.Camera;
import view.rendering.Batch;
import view.rendering.FrameBuffer;

import java.util.ArrayList;
import java.util.List;

public class PostProcessingManager {

    public static boolean isSupported() {
        return FrameBuffer.isSupported();
    }

    private Batch batch;

    private float totalTime = 0;

    private FrameBuffer sceneFB;
    private List<PostProcessingEffect> effects = new ArrayList<>();
    private List<FrameBuffer> passThroughFrameBuffers = new ArrayList<>();

    public PostProcessingManager(Batch batch) {
        this.batch = batch;
    }

    public void init(int width, int height, List<PostProcessingEffect> effects) {
        this.effects.clear();
        this.effects.addAll(effects);

        if (isEnabled()) {
            try {
                if (sceneFB != null) {
                    sceneFB.dispose();
                }
                sceneFB = new FrameBuffer(width, height, GL11.GL_LINEAR, GL12.GL_CLAMP_TO_EDGE);

                for (int i = 0; i < passThroughFrameBuffers.size(); i++) {
                    passThroughFrameBuffers.get(i).dispose();
                    passThroughFrameBuffers.remove(i);
                }

                for (int i = 0; i < Math.max(0, effects.size() - 1); i++) {
                    passThroughFrameBuffers.add(new FrameBuffer(width, height, GL11.GL_LINEAR, GL12.GL_CLAMP_TO_EDGE));
                }

            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
    }

    public void begin(float deltaTime) {
        totalTime += deltaTime;

        if (isEnabled()) {
            sceneFB.begin();

            GL11.glClearColor(0f, 0f, 0f, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        }
    }

    public void end(Camera camera) {
        if (isEnabled()) {
            sceneFB.end();

            if (!effects.isEmpty()) {
                FrameBuffer inFrameBuffer = sceneFB;

                for (int i = 0; i < effects.size(); i++) {
                    PostProcessingEffect e = effects.get(i);

                    if (e.isEnabled()) {
                        FrameBuffer fb = null;
                        if (effects.size() > 1 && i < effects.size() - 1) {
                            fb = passThroughFrameBuffers.get(i);
                            fb.begin();
                        }

                        e.render(inFrameBuffer, camera, batch, totalTime);

                        if (fb != null) {
                            fb.end();
                            inFrameBuffer = fb;
                        }
                    } else {
                        if (i == effects.size() - 1) {
                            batch.begin(camera);
                            batch.draw(inFrameBuffer, 0, 0, Display.getWidth(), Display.getHeight());
                            batch.end();
                        }
                    }
                }
            } else {
                batch.begin(camera);
                batch.draw(sceneFB, 0, 0, Display.getWidth(), Display.getHeight());
                batch.end();
            }
        }
    }

    public boolean isEnabled() {
        return Controller.instance.getConfig().getGraphicMode() == Config.GraphicMode.High && isSupported();
    }

}

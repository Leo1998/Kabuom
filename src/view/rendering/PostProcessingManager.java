package view.rendering;

import controller.Config;
import controller.Controller;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

public class PostProcessingManager {

    public enum Effect {
        Drunk(DrunkEffect.class),
        RadialBlur(RadialBlurEffect.class);

        Class<? extends PostProcessingEffect> clazz;

        Effect(Class<? extends PostProcessingEffect> clazz) {
            this.clazz = clazz;
        }
    }

    public static boolean isSupported() {
        return FrameBuffer.isSupported();
    }

    private Batch batch;

    private float totalTime = 0;

    private FrameBuffer sceneFB;
    private ArrayList<PostProcessingEffect> effects = new ArrayList<>();
    private ArrayList<FrameBuffer> passThroughFrameBuffers = new ArrayList<>();

    public PostProcessingManager(Batch batch) {
        this.batch = batch;
    }

    public void enableEffect(Effect effect) {
        try {
            PostProcessingEffect e = effect.clazz.newInstance();

            addEffect(e);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void disableEffect(Effect effect) {
        for (int i = 0; i < effects.size(); i++) {
            if (effect.clazz.isInstance(effects.get(i))) {
                removeEffect(effects.get(i));
                return;
            }
        }
    }

    public void addEffect(PostProcessingEffect effect) {
        effects.add(effect);

        resize(Display.getWidth(), Display.getHeight());
    }

    public void removeEffect(PostProcessingEffect effect) {
        effects.remove(effect);
        effect.dispose();

        resize(Display.getWidth(), Display.getHeight());
    }

    public void resize(int width, int height) {
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

            } catch(LWJGLException e) {
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

    public void end() {
        if (isEnabled()) {
            sceneFB.end();

            if (!effects.isEmpty()) {
                FrameBuffer inFrameBuffer = sceneFB;

                for (int i = 0; i < effects.size(); i++) {
                    PostProcessingEffect e = effects.get(i);

                    FrameBuffer fb = null;
                    if (effects.size() > 1 && i < effects.size() - 1) {
                        fb = passThroughFrameBuffers.get(i);
                        fb.begin();
                    }

                    e.render(inFrameBuffer, batch, totalTime);

                    if (fb != null) {
                        fb.end();
                        inFrameBuffer = fb;
                    }
                }
            } else {
                batch.begin();
                batch.draw(sceneFB, 0, 0, Display.getWidth(), Display.getHeight());
                batch.end();
            }
        }
    }

    public boolean isEnabled() {
        return Controller.instance.getConfig().getGraphicMode() == Config.GraphicMode.High && isSupported();
    }

}

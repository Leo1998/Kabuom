package view.effects;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL13;
import view.math.Camera;
import view.math.Vector2;
import view.rendering.Batch;
import view.rendering.ShaderProgram;
import view.rendering.VertexAttrib;
import view.texture.ITexture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DrunkEffect extends PostProcessingEffect {

    private static ShaderProgram createShader() {
        try {
            File vert = new File(Batch.class.getResource("/shaders/drunk.vert").toURI());
            File frag = new File(Batch.class.getResource("/shaders/drunk.frag").toURI());

            VertexAttrib[] attribs = new VertexAttrib[]{
                    new VertexAttrib(0, "position", 2),
                    new VertexAttrib(1, "tid", 1),
                    new VertexAttrib(2, "color", 4),
                    new VertexAttrib(3, "texCoords", 2)
            };

            Map<Integer, String> map = new HashMap<>();

            for (VertexAttrib attrib : attribs) {
                map.put(attrib.location, attrib.name);
            }

            return new ShaderProgram(vert, frag, map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ShaderProgram shader;

    private Vector2 amount;

    public DrunkEffect() {
        this.amount = new Vector2(16.0f, 9.0f);
    }

    @Override
    public void render(ITexture sceneTexture, Camera camera, Batch batch, float totalTime) {
        if (shader == null)
            this.shader = createShader();

        batch.begin(camera, shader);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        sceneTexture.getTexture().bind();
        shader.setUniformi("sceneTexture", 0);

        shader.setUniformf("time", totalTime);
        shader.setUniformf("amount", amount.getX(), amount.getY());

        batch.draw(null, 0, 0, Display.getWidth(), Display.getHeight());
        batch.end();
    }

    @Override
    public void dispose() {
        if (shader != null)
            shader.dispose();
    }

    public Vector2 getAmount() {
        return amount;
    }
}

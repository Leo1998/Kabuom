package view.rendering;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL13;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DrunkEffect extends PostProcessingEffect {

    private static ShaderProgram createShader() {
        try {
            File vert = new File(Batch.class.getResource("/shaders/drunk.vert").toURI());
            File frag = new File(Batch.class.getResource("/shaders/drunk.frag").toURI());

            VertexAttrib[] attribs = new VertexAttrib[] {
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
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ShaderProgram shader;

    public DrunkEffect() {
        this.shader = createShader();
    }

    @Override
    public void render(ITexture sceneTexture, Batch batch, float totalTime) {
        batch.begin(shader);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        sceneTexture.getTexture().bind();
        shader.setUniformi("sceneTexture", 0);

        shader.setUniformf("time", totalTime);

        batch.draw(null, 0, 0, Display.getWidth(), Display.getHeight());
        batch.end();
    }
}

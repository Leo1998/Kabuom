package view;

import org.lwjgl.opengl.GL11;
import utility.Matrix4;
import utility.OrthographicCamera;

import java.util.HashMap;
import java.util.Map;

public class Batch {

    private static ShaderProgram createShader(VertexAttrib[] attribs) {
        String vertexSource = "#version 110\n" +
                "attribute vec2 position;\n" +
                "attribute float tid;\n" +
                "attribute vec4 color;\n" +
                "attribute vec2 texCoords;\n" +
                "" +
                "uniform mat4 projectionMatrix;\n" +
                "" +
                "varying vec4 v_color;\n" +
                "varying float v_tid;\n" +
                "" +
                "void main() {\n" +
                "v_color = color;\n" +
                "v_tid = tid;\n" +
                "gl_Position = vec4(position.xy, 0.0, 1.0) * projectionMatrix;\n" +
                "}\n";

        String fragmentSource = "#version 110\n" +
                "" +
                "varying vec4 v_color;\n" +
                "varying float v_tid;\n" +
                "" +
                "void main() {\n" +
                "gl_FragColor = v_color;\n" +
                "}\n";

        Map<Integer, String> map = new HashMap<>();

        for (VertexAttrib attrib : attribs) {
            map.put(attrib.location, attrib.name);
        }

        return new ShaderProgram(vertexSource, fragmentSource, map);
    }

    private static final int VERTICES_PER_SPRITE = 54;

    private ShaderProgram shader;
    private VertexData buffer;

    private Matrix4 projectionMatrix = new Matrix4();

    private int idx = 0;
    private int maxIdx;
    private int renderCalls;
    private boolean drawing = false;

    public Batch() {
        this(1000);
    }

    public Batch(int size) {
        VertexAttrib[] attribs = new VertexAttrib[] {
            new VertexAttrib(0, "position", 2),
                new VertexAttrib(1, "tid", 1),
                new VertexAttrib(2, "color", 4),
                new VertexAttrib(3, "texCoords", 2)
        };
        this.buffer = new VertexBuffer(size * VERTICES_PER_SPRITE, attribs);

        this.shader = createShader(attribs);

        maxIdx = buffer.getVertexCount();
    }

    public void resize(int w, int h) {
        float left = 0;
        float right = w;
        float bottom = h;
        float top = 0;
        this.projectionMatrix = OrthographicCamera.getOrtho(left, right, bottom, top, 0, 1);
    }

    public void begin() {
        drawing = true;

        idx = 0;
        renderCalls = 0;

        shader.use();
        shader.setUniformMatrix(shader.getUniformLocation("projectionMatrix"), false, this.projectionMatrix);
    }

    public void flush() {
        if (idx > 0) {
            buffer.flip();
            render();
            idx = 0;
            buffer.clear();
        }

    }

    public void end() {
        flush();

        drawing = false;
    }

    private void render() {
        //if (texture != null)
        //    texture.bind();
        buffer.bind();
        buffer.draw(GL11.GL_TRIANGLES, 0, idx);
        buffer.unbind();
        renderCalls++;
    }

    private void vertex(float x, float y, float tid, float r, float g, float b, float a, float u, float v) {
        checkFlush();

        buffer.put(x).put(y).put(tid).put(r).put(g).put(b).put(a).put(u).put(v);
        idx += 9;
    }

    private void checkFlush() {
        if (idx > maxIdx)
            flush();
    }

    public void draw(float x, float y, float width, float height, float originX, float originY, float rotationRadians, float r, float g, float b, float a) {
        draw(x, y, width, height, originX, originY, rotationRadians, r, g, b, a, 0, 0, 1, 1);
    }

    public void draw(float x, float y, float width, float height, float originX, float originY, float rotationRadians, float r, float g, float b, float a, float u, float v, float u2, float v2) {
        checkFlush();

        float x1,y1, x2,y2, x3,y3, x4,y4;

        if (rotationRadians != 0) {
            float scaleX = 1f;//width/tex.getWidth();
            float scaleY = 1f;//height/tex.getHeight();

            float cx = originX*scaleX;
            float cy = originY*scaleY;

            float p1x = -cx;
            float p1y = -cy;
            float p2x = width - cx;
            float p2y = -cy;
            float p3x = width - cx;
            float p3y = height - cy;
            float p4x = -cx;
            float p4y = height - cy;

            final float cos = (float) Math.cos(rotationRadians);
            final float sin = (float) Math.sin(rotationRadians);

            x1 = x + (cos * p1x - sin * p1y) + cx; // TOP LEFT
            y1 = y + (sin * p1x + cos * p1y) + cy;
            x2 = x + (cos * p2x - sin * p2y) + cx; // TOP RIGHT
            y2 = y + (sin * p2x + cos * p2y) + cy;
            x3 = x + (cos * p3x - sin * p3y) + cx; // BOTTOM RIGHT
            y3 = y + (sin * p3x + cos * p3y) + cy;
            x4 = x + (cos * p4x - sin * p4y) + cx; // BOTTOM LEFT
            y4 = y + (sin * p4x + cos * p4y) + cy;
        } else {
            x1 = x;
            y1 = y;

            x2 = x+width;
            y2 = y;

            x3 = x+width;
            y3 = y+height;

            x4 = x;
            y4 = y+height;
        }

        // top left, top right, bottom left
        vertex(x1, y1, 0, r, g, b, a, u, v);
        vertex(x2, y2, 0, r, g, b, a, u2, v);
        vertex(x4, y4, 0, r, g, b, a, u, v2);

        // top right, bottom right, bottom left
        vertex(x2, y2, 0, r, g, b, a, u2, v);
        vertex(x3, y3, 0, r, g, b, a, u2, v2);
        vertex(x4, y4, 0, r, g, b, a, u, v2);
    }

}

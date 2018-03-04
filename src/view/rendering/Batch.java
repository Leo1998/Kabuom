package view.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import utility.Matrix4;
import utility.OrthographicCamera;
import utility.Vector2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Batch {

    private static ShaderProgram createShader(VertexAttrib[] attribs) {
        try {
            InputStream vert = Batch.class.getResourceAsStream("/shaders/batch.vert");
            InputStream frag = Batch.class.getResourceAsStream("/shaders/batch.frag");

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

    private static final int VERTICES_PER_SPRITE = 6;

    private ShaderProgram shader;
    private VertexData buffer;

    private Matrix4 projectionMatrix = new Matrix4();

    private int texIdx = 0;
    private final int maxTextureIdx = Math.min(16, GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS));
    private List<Texture> textures = new ArrayList<Texture>();
    private int idx = 0;
    private int maxIdx;
    private int renderCalls;
    private boolean drawing = false;

    public Batch() {
        this(5000);
    }

    public Batch(int size) {
        VertexAttrib[] attribs = new VertexAttrib[]{
                new VertexAttrib(0, "position", 2),
                new VertexAttrib(1, "tid", 1),
                new VertexAttrib(2, "color", 4),
                new VertexAttrib(3, "texCoords", 2)
        };
        this.buffer = new VertexBuffer(size * VERTICES_PER_SPRITE, attribs);

        this.shader = createShader(attribs);

        maxIdx = buffer.getVertexCount();

        System.out.println("Batch created with " + size + " sprites and " + maxTextureIdx + " textures!");
    }

    public void resize(int w, int h) {
        float left = 0;
        float right = w;
        float bottom = h;
        float top = 0;
        this.projectionMatrix = OrthographicCamera.getOrtho(left, right, bottom, top, 0, 1);
    }

    public void begin() {
        begin(this.shader);
    }

    public void begin(ShaderProgram shader) {
        drawing = true;

        idx = 0;
        texIdx = 0;
        textures.clear();
        renderCalls = 0;

        shader.use();
        shader.setUniformMatrix(shader.getUniformLocation("projectionMatrix"), false, this.projectionMatrix);
    }

    public void flush() {
        if (idx > 0) {
            buffer.flip();
            render();
            idx = 0;
            texIdx = 0;
            textures.clear();
            buffer.clear();
        }
    }

    public void end() {
        flush();

        drawing = false;
    }

    private void render() {
        for (int i = 0; i < textures.size(); i++) {
            Texture tex = textures.get(i);

            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
            tex.bind();
            shader.setUniformi(shader.getUniformLocation("texture" + i), i);
        }
        buffer.bind();
        buffer.draw(GL11.GL_TRIANGLES, 0, idx);
        buffer.unbind();
        renderCalls++;
    }

    private void vertex(float x, float y, float tid, float r, float g, float b, float a, float u, float v) {
        buffer.put(x).put(y).put(tid).put(r).put(g).put(b).put(a).put(u).put(v);
        idx++;
    }

    private void checkFlush() {
        if (idx >= maxIdx)
            flush();
    }

    private int checkTexture(ITexture itex) {
        if (itex == null) return -1;

        Texture tex = itex.getTexture();

        if (textures.contains(tex)) {
            return textures.indexOf(tex);
        } else {
            if (texIdx >= maxTextureIdx) {
                flush();
            }
            textures.add(texIdx, tex);
            texIdx++;

            return texIdx - 1;
        }
    }

    public void draw(ITexture tex, float x, float y, float width, float height) {
        draw(tex, x, y, width, height,0, 1f, 1f, 1f, 1f);
    }

    public void draw(ITexture tex, float x, float y, float width, float height, float r, float g, float b, float a) {
        draw(tex, x, y, width, height,0, r, g, b, a);
    }

    public void draw(ITexture tex, float x, float y, float width, float height, float rotationRadians, float r, float g, float b, float a) {
        checkFlush();

        int tid = checkTexture(tex);

        float u = 0f;
        float v = 1f;
        float u2 = 1f;
        float v2 = 0f;
        if (tex != null) {
            u = tex.getU();
            v = tex.getV();
            u2 = tex.getU2();
            v2 = tex.getV2();
        }

        float x1, y1, x2, y2, x3, y3, x4, y4;

        if (rotationRadians != 0) {
            //Vector Rotation

            float halfWidth = width/2;
            float halfHeight = height/2;

            float centerX = x + halfWidth;
            float centerY = y + halfHeight;

            float sin = (float) Math.sin(rotationRadians);
            float cos = (float) Math.cos(rotationRadians);

            //Point 1: -Width, -Height
            x1 = centerX - cos*halfWidth + sin*halfHeight; // centerX + cos*(-halfWidth) - sin*(-halfHeight)
            y1 = centerY - sin*halfWidth - cos*halfHeight; // centerX + sin*(-halfWidth) + cos*(-halfHeight)

            //Point 2: +Width, -Height
            x2 = centerX + cos*halfWidth + sin*halfHeight; // centerX + cos*(halfWidth) - sin*(-halfHeight)
            y2 = centerY + sin*halfWidth - cos*halfHeight; // centerX + sin*(halfWidth) + cos*(-halfHeight)

            //Point 3: +Width, +Height
            x3 = centerX + cos*halfWidth - sin*halfHeight; // centerX + cos*(halfWidth) - sin*(halfHeight)
            y3 = centerY + sin*halfWidth + cos*halfHeight; // centerX + sin*(halfWidth) + cos*(halfHeight)

            //Point 4: -Width, +Height
            x4 = centerX - cos*halfWidth - sin*halfHeight; // centerX + cos*(-halfWidth) - sin*(halfHeight)
            y4 = centerY - sin*halfWidth + cos*halfHeight; // centerX + sin*(-halfWidth) + cos*(halfHeight)
        } else {
            x1 = x;
            y1 = y;

            x2 = x + width;
            y2 = y;

            x3 = x + width;
            y3 = y + height;

            x4 = x;
            y4 = y + height;
        }

        // top left, top right, bottom left
        vertex(x1, y1, tid, r, g, b, a, u, v);
        vertex(x2, y2, tid, r, g, b, a, u2, v);
        vertex(x4, y4, tid, r, g, b, a, u, v2);

        // top right, bottom right, bottom left
        vertex(x2, y2, tid, r, g, b, a, u2, v);
        vertex(x3, y3, tid, r, g, b, a, u2, v2);
        vertex(x4, y4, tid, r, g, b, a, u, v2);
    }

    /**
     * Draws a Circle
     * @param cx X-Coordinate of center
     * @param cy Y-Coordinate of center
     * @param rad Radius of circle
     * @param r Color Red
     * @param g Color Green
     * @param b Color Blue
     * @param a Color Alpha
     */
    public void circle(float cx, float cy, float rad, float r, float g, float b, float a){
        float[][] points = prepareCircle(cx,cy,rad);

        float u = 0f;
        float v = 1f;
        float u2 = 1f;
        float v2 = 0f;

        for(int i = 0; i < points.length; i++){
            vertex(cx, cy, -1, r, g, b, a, u, v);
            vertex(points[i][0], points[i][1], -1, r, g, b, a, u2, v);
            vertex(points[(i+1)%points.length][0], points[(i+1)%points.length][1], -1, r, g, b, a, u, v2);
        }
    }
    /**
     * Draws a Circle within a rectangle
     * @param cx X-Coordinate of center
     * @param cy Y-Coordinate of center
     * @param rad Radius of circle
     * @param r Color Red
     * @param g Color Green
     * @param b Color Blue
     * @param a Color Alpha
     * @param bx1 X-Coordinate of top-left corner of rectangle
     * @param by1 Y-Coordinate of top-left corner of rectangle
     * @param bx2 X-Coordinate of bottom-right corner of rectangle
     * @param by2 Y-Coordinate of bottom-right corner of rectangle
     */
    public void limitedCircle(float cx, float cy, float rad, float r, float g, float b, float a, int bx1, int by1, int bx2, int by2){
        float[][] points = prepareCircle(cx,cy,rad);

        float u = 0f;
        float v = 1f;
        float u2 = 1f;
        float v2 = 0f;

        cx = Math.max(bx1,Math.min(bx2,cx));
        cy = Math.max(by1,Math.min(by2,cy));

        points[0][0] = Math.max(bx1,Math.min(bx2,points[0][0]));
        points[0][1] = Math.max(by1,Math.min(by2,points[0][1]));

        for(int i = 0; i < points.length; i++){
            points[(i+1)%points.length][0] = Math.max(bx1,Math.min(bx2,points[(i+1)%points.length][0]));
            points[(i+1)%points.length][1] = Math.max(by1,Math.min(by2,points[(i+1)%points.length][1]));

            vertex(cx, cy, -1, r, g, b, a, u, v);
            vertex(points[i][0], points[i][1], -1, r, g, b, a, u2, v);
            vertex(points[(i+1)%points.length][0], points[(i+1)%points.length][1], -1, r, g, b, a, u, v2);
        }
    }

    /**
     * Calculates points along the outline of a circle
     * Source: http://slabode.exofire.net/circle_draw.shtml
     * Edited to return points within array
     * @param cx X-Coordinate of center
     * @param cy Y-Coordinate of center
     * @param rad Radius of circle
     */
    private float[][] prepareCircle(float cx, float cy, float rad){
        int num_segments = Math.min(Math.round(10 * (float)Math.sqrt(rad)),128);//change the 10 to a smaller/bigger number as needed

        float[][] result = new float[num_segments][2];

        float theta = 2 * 3.1415926f / (float)(num_segments);
        float tangetial_factor = (float)Math.tan(theta);//calculate the tangential factor

        float radial_factor = (float)Math.cos(theta);//calculate the radial factor

        float x = rad;//we start at angle = 0

        float y = 0;

        for(int i = 0; i < num_segments; i++)
        {
            //output vertex
            result[i][0] = x + cx;
            result[i][1] = y + cy;

            //calculate the tangential vector
            //remember, the radial vector is (x, y)
            //to get the tangential vector we flip those coordinates and negate one of them

            float tx = -y;
            float ty = x;

            //add the tangential vector

            x += tx * tangetial_factor;
            y += ty * tangetial_factor;

            //correct using the radial factor

            x *= radial_factor;
            y *= radial_factor;
        }

        return result;
    }
}

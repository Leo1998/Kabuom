package view.texture;

public class TextureRegion implements ITexture {

    protected Texture texture;
    protected float u;
    protected float v;
    protected float u2;
    protected float v2;
    protected int regionWidth;
    protected int regionHeight;

    public TextureRegion(Texture texture) {
        this(texture, 0, 0);
    }

    public TextureRegion(Texture texture, int x, int y) {
        this(texture, x, y, texture.getWidth(), texture.getHeight());
    }

    public TextureRegion(Texture texture, int x, int y, int width, int height) {
        set(texture, x, y, width, height);
    }

    public TextureRegion(Texture texture, float u, float v, float u2, float v2) {
        set(texture, u, v, u2, v2);
    }

    public TextureRegion(TextureRegion region, int x, int y, int width, int height) {
        set(region, x, y, width, height);
    }

    public void set(Texture texture, int x, int y, int width, int height) {
        set(texture, x / (float) texture.getWidth(),
                y / (float) texture.getHeight(),
                (x + width) / (float) texture.getWidth(),
                (y + height) / (float) texture.getHeight());
        regionWidth = Math.round(width);
        regionHeight = Math.round(height);
    }

    public void set(Texture texture, float u, float v, float u2, float v2) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
        regionWidth = Math.round(Math.abs(u2 - u) * texture.getWidth());
        regionHeight = Math.round(Math.abs(v2 - v) * texture.getHeight());
    }

    public void set(TextureRegion region, int x, int y, int width, int height) {
        set(region.texture, x + region.getRegionX(), y + region.getRegionY(), width, height);
    }

    public int getRegionX() {
        return Math.round(u * texture.getWidth());
    }

    public int getRegionY() {
        return Math.round(v * texture.getHeight());
    }

    public void flip(boolean x, boolean y) {
        if (x) {
            float temp = u;
            u = u2;
            u2 = temp;
        }
        if (y) {
            float temp = v;
            v = v2;
            v2 = temp;
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float getU() {
        return u;
    }

    public void setU(float u) {
        this.u = u;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public float getU2() {
        return u2;
    }

    public void setU2(float u2) {
        this.u2 = u2;
    }

    public float getV2() {
        return v2;
    }

    public void setV2(float v2) {
        this.v2 = v2;
    }

    public int getWidth() {
        return regionWidth;
    }

    public int getHeight() {
        return regionHeight;
    }

}

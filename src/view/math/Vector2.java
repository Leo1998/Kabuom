package view.math;

public class Vector2 {

    private float x;
    private float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getLength() {
        return (float) Math.abs(Math.sqrt((x * x) + (y * y)));
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void normalize() {
        float r = 1 / this.getLength();

        x *= r;
        y *= r;
    }

    public void multiply(float m) {
        x *= m;
        y *= m;
    }

    public void rotate(float rotationRadians){
        float tempX = x;
        float tempY = y;

        x = (float)(Math.cos(rotationRadians)*tempX - Math.sin(rotationRadians)*tempY);
        y = (float)(Math.sin(rotationRadians)*tempX + Math.cos(rotationRadians)*tempY);
    }

    public boolean nullVector(){
        return x == 0f && y == 0f;
    }

    public float getAngle(){
        return (float)Math.atan2(x,-y);
    }

    @Override
    public Vector2 clone()  {
        return new Vector2(x, y);
    }
}

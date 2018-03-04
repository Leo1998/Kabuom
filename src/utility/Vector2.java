package utility;

import java.util.Arrays;

public class Vector2 {

    private float[] coords;

    public Vector2(float x, float y) {
        coords = new float[2];
        coords[0] = x;
        coords[1] = y;
    }

    public float getLength() {
        return (float) Math.abs(Math.sqrt((coords[0] * coords[0]) + (coords[1] * coords[1])));
    }

    public float[] getCoords() {
        return coords;
    }

    public void normalize() {
        float r = 1 / this.getLength();

        coords[0] *= r;
        coords[1] *= r;
    }

    public void multiply(float m) {
        coords[0] = coords[0] * m;
        coords[1] = coords[1] * m;
    }

    public void rotate(float rotationRadians){
        float tempX = coords[0];
        float tempY = coords[1];

        coords[0] = (float)(Math.cos(rotationRadians)*tempX - Math.sin(rotationRadians)*tempY);
        coords[1] = (float)(Math.sin(rotationRadians)*tempX + Math.cos(rotationRadians)*tempY);
    }

    public boolean nullVector(){
        return coords[0] == 0f && coords[1] == 0f;
    }

    public float getAngle(){
        return (float)Math.atan2(coords[0],-coords[1]);
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "coords=" + Arrays.toString(coords) +
                '}';
    }

    @Override
    public Vector2 clone() {
        return new Vector2(coords[0],coords[1]);
    }
}

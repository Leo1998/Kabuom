package utility;

public class Vector2 {

    private float[] coords;

    public Vector2(float x, float y){
        coords = new float[2];
        coords[1] = x;
        coords[2] = y;
    }

    public float getLength(){
        return (float) Math.sqrt((coords[1]*coords[1])+(coords[2]*coords[2]));
    }

    public float[] getCoords() {
        return coords;
    }

}

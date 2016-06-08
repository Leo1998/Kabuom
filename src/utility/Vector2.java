package utility;

public class Vector2 {

    private float[] koords;

    public Vector2(float x1, float y1,float x2, float y2){
        koords = new float[2];
        koords[1] = x2-x1;
        koords[2] = y2-y1;
    }

    public float[] getKoords() {
        return koords;
    }

}

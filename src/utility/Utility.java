package utility;

import model.Position;
import view.components.ViewComponent;
import java.util.Random;


public class Utility {

    public static Random random = new Random();

    public static float getAngle(Position o1, Position o2) {
        double theta = Math.atan2(o1.getY() - o2.getY(), o1.getX() - o2.getX());

        theta += Math.PI / 2.0;

        if (theta < 0) {
            theta += Math.PI * 2.0;
        }

        return (float) theta;
    }

    public static boolean viewComponentIsCollidingWithMouse(ViewComponent o1, float mouseX, float mouseY) {
        if (o1 != null) {
            return mouseX >= (o1.getX()) &&
                    mouseX <= (o1.getX()) + (o1.getWidth()) &&
                    mouseY >= (o1.getY()) &&
                    mouseY <= (o1.getY()) + (o1.getHeight());
        } else {
            return false;
        }
    }

    public static String intToRoman(int num) {
        StringBuilder sb = new StringBuilder();
        int times = 0;
        String[] romans = new String[] { "I", "IV", "V", "IX", "X", "XL", "L",
                "XC", "C", "CD", "D", "CM", "M" };
        int[] ints = new int[] { 1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500,
                900, 1000 };
        for (int i = ints.length - 1; i >= 0; i--) {
            times = num / ints[i];
            num %= ints[i];
            while (times > 0) {
                sb.append(romans[i]);
                times--;
            }
        }
        return sb.toString();
    }

    public static String niceNumber(int number){
        String result = Integer.toString(number);
        for (int i = result.length() - 3; i > 0; i -= 3) {
            result = result.substring(0, i) + "," + result.substring(i);
        }
        return result;
    }

    public static boolean isMask(byte mask, byte b){
        return (b & mask) == mask;
    }

    public static byte setMask(byte mask, byte b, boolean set){
        if(set){
            return (byte)(b | mask);
        } else {
            return b;
        }
    }
}

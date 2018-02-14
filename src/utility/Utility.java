package utility;

import model.GameObject;
import view.components.ViewComponent;

import java.util.Random;


public class Utility {

    public static Random random = new Random();

    public static float calculateAngleBetweenTwoPoints(float x1, float y1, float x2, float y2) {
        double deltaX, deltaY;
        int tempCoord;

        if (x2 - x1 != 0) {
            deltaX = x2 - x1;
        } else {
            deltaX = 0.00000001;
        }
        if (y2 - y1 != 0) {
            deltaY = y2 - y1;
        } else {
            deltaY = 0.00000001;
        }

        if (x2 < x1) {
            if (y2 < y1) {
                tempCoord = 3;
            } else {
                tempCoord = 2;
            }
        } else {
            if (y2 < y1) {
                tempCoord = 4;
            } else {
                tempCoord = 1;
            }
        }

        double angle = Math.atan(deltaX / deltaY);
        if (tempCoord == 1) {
            angle = (float) (Math.atan((deltaX) / (-deltaY)));
        } else if (tempCoord == 2) {
            angle = (float) (Math.atan((deltaX) / (-deltaY)));
        } else if (tempCoord == 3) {
            angle = (float) (Math.atan((deltaX) / (-deltaY))) + Math.PI;
        } else if (tempCoord == 4) {
            angle = (float) (Math.atan((deltaX) / (-deltaY))) + Math.PI;
        }

        return (float) (angle);
    }

    public static float getAngle(GameObject o1, GameObject o2) {
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
}

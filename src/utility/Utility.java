package utility;

import model.GameObject;
import org.lwjgl.opengl.Display;
import view.ViewManager;
import view.components.ViewComponent;


public class Utility {

    public boolean gameObjectIsCollidingWithGameObject(GameObject o1, GameObject o2){
        if(o1!= null && o2 != null){
            if(new Vector2(Utility.layoutX(o1.getX()),Utility.layoutY(o1.getY()),Utility.layoutX(o2.getX()),Utility.layoutY(o2.getY())).getLength() >= o1.getRadius()+o2.getRadius()) {
                return true;
            }else{
                return false;
            }
        }else{
            throw new IllegalArgumentException();
        }
    }
    public boolean gameObjectIsCollidingWithMouse(GameObject o1, int mouseX, int mouseY){
        if(o1 != null ){
            if(mouseX>= Utility.layoutX(o1.getX()) &&
                    mouseX <= Utility.layoutX(o1.getX())+Utility.layoutX(o1.getRadius())&&
                    mouseY >= Utility.layoutY(o1.getY())&&
                            mouseY <= Utility.layoutY(o1.getY())+Utility.layoutY(o1.getRadius())){
                return true;
            }else{
                return false;
            }

        }else{
            throw new IllegalArgumentException();
        }

    }

    public static float calculateAngleBetweenTwoPoints(float x1, float y1,float x2, float y2){
        double deltaX , deltaY;
        int tempCoord;

        if(x2-x1 != 0) {
            deltaX = x2 - x1;
        }else{
            deltaX = 1;
        }
        if(y2-y1 != 0){
            deltaY = y2 - y1;
        }else{
            deltaY = 1;
        }

        if(x2 < x1) {
            if (y2 < y1) {
                tempCoord = 3;
            } else {
                tempCoord = 2;
            }
        }else{
            if (y2 < y1) {
                tempCoord = 4;
            } else {
                tempCoord = 1;
            }
        }

        double angle = Math.atan(deltaX/deltaY);
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

    public boolean viewComponentIsCollidingWithMouse(ViewComponent o1, int mouseX, int mouseY) {
        if (o1 != null) {
            if (mouseX >= Utility.layoutX(o1.getX()) &&
                    mouseX <= Utility.layoutX(o1.getX()) + Utility.layoutX(o1.getWidth()) &&
                    mouseY >= Utility.layoutY(o1.getY()) &&
                            mouseY <= Utility.layoutY(o1.getY()) + Utility.layoutY(o1.getHeight())) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException();

        }
    }

    public static float layoutX(float koord){
        return koord * Display.getWidth() / 800;
    }
    public static float layoutY(float koord){
        return koord * Display.getHeight() / 600 ;
    }
}

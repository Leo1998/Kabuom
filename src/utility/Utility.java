package utility;

import model.GameObject;
import org.lwjgl.input.Mouse;
import view.components.ViewComponent;


public class Utility {

    public boolean gameObjectIsCollidingWithGameObject(GameObject o1, GameObject o2){
        if(o1!= null && o2 != null){
            if(new Vector2(o1.getX(),o1.getY(),o2.getX(),o2.getY()).getLength() >= o1.getRadius()+o2.getRadius()) {
                return true;
            }else{
                return false;
            }
        }else{
            throw new IllegalArgumentException();
        }
    }
    public boolean gameObjectIsCollidingWithMouse(GameObject o1){
        if(o1 != null ){
            if(Mouse.getX()>= o1.getX() &&
                    Mouse.getX() <= o1.getX()+o1.getRadius()&&
                    Mouse.getY() >= o1.getY()&&
                    Mouse.getY() <= o1.getY()+o1.getRadius()){
                return true;
            }else{
                return false;
            }

        }else{
            throw new IllegalArgumentException();
        }

    }

    public boolean viewComponentIsCollidingWithMouse(ViewComponent o1) {
        if (o1 != null) {
            if (Mouse.getX() >= o1.getX() &&
                    Mouse.getX() <= o1.getX() + o1.getWidth() &&
                    Mouse.getY() >= o1.getY() &&
                    Mouse.getY() <= o1.getY() + o1.getHeight()) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException();

        }
    }
}

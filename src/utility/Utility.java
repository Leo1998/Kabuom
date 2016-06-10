package utility;

import model.GameObject;


public class Utility {

    public boolean isColliding(GameObject o1, GameObject o2){
        if(o1!= null && o2 != null){
            if(new Vector2(o1.getX(),o1.getY(),o2.getX(),o2.getY()).getLength() > o1.getRadius()+o2.getRadius()) {
                return true;
            }
        }
        return false;
    }
}

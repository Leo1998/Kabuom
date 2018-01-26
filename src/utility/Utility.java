package utility;

import entity.model.Entity;
import model.GameObject;
import model.Position;
import view.components.ViewComponent;
import world.Block;

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

    public static boolean viewComponentIsCollidingWithMouse(ViewComponent o1, int mouseX, int mouseY) {
        if (o1 != null) {
            return mouseX >= (o1.getX()) &&
                    mouseX <= (o1.getX()) + (o1.getWidth()) &&
                    mouseY >= (o1.getY()) &&
                    mouseY <= (o1.getY()) + (o1.getHeight());
        } else {
            return false;
        }
    }

    public static Entity findCollidingEntity(Entity source, Block[][] blocks){
        float x = source.getX(), y = source.getY(), radius = source.entityType.getRadius();
        Entity closest = null;
        for (int i = Math.max(0,(int) (Math.floor(x - 1))); i < Math.min(blocks.length,Math.ceil(x + 2)); i++) {
            for (int j = Math.max(0, (int) (Math.floor(y - 1))); j < Math.min(blocks[i].length, Math.ceil(y + 2)); j++) {
                for(Entity entity : blocks[i][j].getEntities()){
                    if(!source.allyOf(entity)){
                        if (closest == null || getDist(source, entity) - entity.entityType.getRadius() < getDist(source, closest) - closest.entityType.getRadius()) {
                            closest = entity;
                        }
                    }
                }
            }
        }

        if(closest != null && getDist(source,closest) > closest.entityType.getRadius() + radius){
            closest = null;
        }
        return closest;
    }

    public static float getDist(Position p1, Position p2){
        return getDist(p1.getX(),p1.getY(),p2.getX(),p2.getY());
    }

    public static float getDist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
    }

    public static float addo(float old, float add){
        if(old < 0 || add < 0){
            throw new IllegalArgumentException();
        }else if(old+add < 0){
            return Float.MAX_VALUE;
        }else{
            return old+add;
        }
    }

}

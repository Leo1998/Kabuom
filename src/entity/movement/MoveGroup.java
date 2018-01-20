package entity.movement;

import entity.model.Entity;
import entity.model.Partisan;
import model.Position;

import java.util.Stack;

public class MoveGroup implements Partisan, Position {
    private Stack<Step> steps;
    private float x,y;
    private Entity target;
    private int members;

    private final boolean isEnemy;

    public MoveGroup(boolean isEnemy){
        members = 0;
        this.isEnemy = isEnemy;
    }

    public void addMember(){
        members++;
    }

    public boolean removeMember(){
        members--;
        return members <= 0;
    }

    public Stack<Step> getSteps() {
        return steps;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Entity getTarget() {
        return target;
    }

    public void setSteps(Stack<Step> steps) {
        this.steps = steps;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public boolean isEnemy() {
        return isEnemy;
    }

    @Override
    public boolean allyOf(Partisan partisan) {
        return partisan.isEnemy() == isEnemy;
    }
}

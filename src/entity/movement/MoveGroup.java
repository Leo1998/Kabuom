package entity.movement;

import entity.model.Partisan;

import java.util.Stack;

public class MoveGroup implements Partisan {
    private final boolean isEnemy;
    private float x, y, speed;
    private Stack<Step> steps;
    private boolean hasMembers;

    public MoveGroup(boolean isEnemy, float x, float y) {
        this.isEnemy = isEnemy;
        this.x = x;
        this.y = y;
        speed = 1;
        steps = null;
        hasMembers = false;
    }

    public float getSpeed() {
        return speed;
    }

    public Stack<Step> getSteps() {
        return steps;
    }

    public boolean hasMembers() {
        return hasMembers;
    }

    public void setSpeed(float speed) {
        if(speed < this.speed) {
            this.speed = speed;
        }
    }

    public void register(){
        hasMembers = true;
    }

    public void resetSpeed(){
        this.speed *= 2;
        hasMembers = false;
    }

    public void setSteps(Stack<Step> steps) {
        this.steps = steps;
    }

    @Override
    public boolean isEnemy() {
        return isEnemy;
    }

    @Override
    public boolean allyOf(Partisan partisan) {
        return isEnemy == partisan.isEnemy();
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }
}

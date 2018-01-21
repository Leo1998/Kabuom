package entity.movement;

import entity.model.Partisan;

import java.util.Stack;

public class MoveGroup implements Partisan {
    private Stack<Step> steps;
    private float x,y;
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

    public void setSteps(Stack<Step> steps) {
        this.steps = steps;
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

    @Override
    public boolean isEnemy() {
        return isEnemy;
    }

    @Override
    public boolean allyOf(Partisan partisan) {
        return partisan.isEnemy() == isEnemy;
    }
}

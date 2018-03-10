package entity.utility;

import model.Position;

public class Step implements Position{
    private float x,y;
    public final StepType stepType;

    public Step(StepType stepType, float x, float y) {
        this.stepType = stepType;
        this.x = x;
        this.y = y;
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

    public enum StepType{
        GoTo,
        StayInRange,
        MoveInRange,
    }
}

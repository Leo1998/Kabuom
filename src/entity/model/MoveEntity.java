package entity.model;

import entity.movement.Step;
import utility.Vector2;
import world.Block;

import java.util.Stack;

public class MoveEntity extends Entity {

    private Vector2 movement;
    private Stack<Step> steps;

    public MoveEntity(EntityType entityType, int level, float x, float y, int wave, Block block) {
        super(entityType, level, x, y, wave, block);
        movement = new Vector2(0,0);
    }

    public Vector2 getMovement() {
        return movement;
    }

    public Stack<Step> getSteps() {
        return steps;
    }

    public void setMovement(Vector2 movement) {
        this.movement = movement;
    }

    public void setSteps(Stack<Step> steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "MoveEntity{" +
                "x=" + getX() +
                ", y=" + getY() +
                ", entityType=" + entityType +
                ", wave=" + wave +
                ", block=" + block +
                ", movement=" + movement +
                '}';
    }
}

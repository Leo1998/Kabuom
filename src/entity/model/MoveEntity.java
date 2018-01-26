package entity.model;

import entity.movement.MoveGroup;
import utility.Vector2;
import world.Block;

public class MoveEntity extends Entity {

    private Vector2 movement;
    private MoveGroup group;

    public MoveEntity(EntityType entityType, int level, float x, float y, int wave, Block block) {
        super(entityType, level, x, y, wave, block);
        movement = new Vector2(0,0);
    }

    public Vector2 getMovement() {
        return movement;
    }

    public MoveGroup getGroup() {
        return group;
    }

    public void setMovement(Vector2 movement) {
        this.movement = movement;
    }

    public void setGroup(MoveGroup group) {
        this.group = group;
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
                ", group=" + group +
                '}';
    }
}

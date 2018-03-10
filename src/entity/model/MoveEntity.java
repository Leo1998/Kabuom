package entity.model;

import entity.utility.Step;
import org.json.JSONObject;
import view.math.Vector2;
import world.Block;

import java.util.Stack;

public class MoveEntity extends Entity {

    private Vector2 movement;
    private Stack<Step> steps;

    public MoveEntity(EntityType entityType, int level, float x, float y, int wave, Block block, boolean isEnemy) {
        super(entityType, level, x, y, wave, block, isEnemy);
        movement = new Vector2(0,0);
        steps = new Stack<>();
    }

    public MoveEntity(JSONObject object, Block[][] blocks){
        super(object,blocks);
        movement = new Vector2(0,0);
        steps = new Stack<>();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject object = super.toJSON();
        object.put("isMinion", this instanceof Minion);
        return object;
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

    public byte getKey(){
        byte key = 0x00;
        if(isEnemy())
            key |= 0x01;
        if(isRanged())
            key |= 0x02;
        if(getTarget() != null && getTarget().isEnemy())
            key |= 0x04;
        return key;
    }

    @Override
    public String toString() {
        return super.toString() + ", MoveEntity{" +
                ", block=(" + block.x + "|" + block.y + ")" +
                ", movement=" + movement +
                '}';
    }
}

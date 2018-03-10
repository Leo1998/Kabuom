package entity.model;

import org.json.JSONObject;
import world.Block;

public class Minion extends MoveEntity {
    public final Entity source;

    public Minion(EntityType entityType, int level, float x, float y, int wave, Block block, boolean isEnemy, Entity source) {
        super(entityType, level, x, y, wave, block, isEnemy);
        this.source = source;
    }

    public Minion(JSONObject object, Block[][] blocks, Entity source) {
        super(object, blocks);
        this.source = source;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject object = super.toJSON();
        object.put("sX",Math.round(source.getX()));
        object.put("sY",Math.round(source.getY()));
        return object;
    }
}

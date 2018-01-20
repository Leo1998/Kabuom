package tower;

import enemy.Enemy;
import model.GameObject;
import model.ObjectType;
import projectile.ProjectileType;

public class Tower extends GameObject {
    private float cooldown;
    public final TowerType towerType;
    private Enemy target;

    public Tower(TowerType type, int level, float x, float y) {
        super(type, level, x, y);
        this.towerType = type;
        cooldown = towerType.frequency;
    }

    public Enemy getTarget() {
        return target;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setTarget(Enemy newTarget) {
        target = newTarget;
    }

    public void setCooldown(float newCooldown) {
        cooldown = newCooldown;
    }

    @Override
    public ObjectType getObjectType() {
        return towerType;
    }
}

package projectile;

import enemy.Enemy;
import graph.List;
import model.GameObject;

/**
 * Created by Daniel on 09.06.2016.
 */
public class Projectile extends GameObject{
    private ProjectileType projectileType;
    private int speed,impactDamage,range;
    private float targetX,targetY,distance;
    private List<Enemy> hitEnemies;

    public Projectile(int maxHp, int level, String name, float x, float y, ProjectileType projectileType, float targetX, float targetY) {
        super(maxHp, level, name, x, y);
        this.projectileType = projectileType;
        this.targetX = targetX;
        this.targetY = targetY;
    }
}

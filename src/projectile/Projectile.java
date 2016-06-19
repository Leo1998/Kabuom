package projectile;

import enemy.Enemy;
import model.GameObject;

import java.util.ArrayList;

public class Projectile extends GameObject{
    private ProjectileType projectileType;
    private int speed,impactDamage,range;
    private float targetX,targetY,distance;
    private ArrayList<Enemy> hitEnemies;

    public Projectile(int maxHp, int level, String name, float x, float y, float radius, ProjectileType projectileType, float targetX, float targetY) {
        super(maxHp, level, name, x, y,radius);
        this.projectileType = projectileType;
        this.targetX = targetX;
        this.targetY = targetY;
    }
}

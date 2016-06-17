package tower;

import enemy.Enemy;
import model.GameObject;
import projectile.ProjectileType;

/**
 * Created by Daniel on 09.06.2016.
 */
public class Tower extends GameObject{
    private ProjectileType projectile;
    private int attackRadius,cost,HP;
    private float frequency,radius,cooldown;
    private TowerType type;
    private Enemy target;

    public Tower(TowerType type, int level, String name, float x, float y, float radius) {
        super(type.HP, level, name, x, y,radius);
        this.HP = type.HP;
        this.projectile = type.projectileType;
        this.attackRadius = type.attackRadius;
        this.cost = type.cost;
        this.type = type;
        this.frequency = type.frequency;
        this.radius=radius;
    }

    public int getAttackRadius() {
        return attackRadius;
    }

    public ProjectileType getProjectile() {
        return projectile;
    }

    public TowerType getType(){return type;}

    public int getCost(){return cost;}

    public Enemy getTarget(){return target;}

    public float getFrequency(){return frequency;}

    public int getHP(){return HP;}

    public float getRadius(){return radius;}

    public float getCooldown(){return cooldown;}

    public void setTarget(Enemy newTarget){target=newTarget;}

    public void setCooldown(Float newCooldown){cooldown=newCooldown;}
}

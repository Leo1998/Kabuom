package tower;

import enemy.Enemy;
import model.GameObject;
import projectile.ProjectileType;

public class Tower extends GameObject{
    private ProjectileType projectile;
    private int attackRadius,cost,HP;
    private float frequency,cooldown;
    private TowerType type;
    private Enemy target;

    public Tower(TowerType type, int level, String name, float x, float y, float radius) {
        super(type.getHP(), level, name, x, y, radius,type.getName());
        this.projectile = type.getProjectileType();
        this.attackRadius = type.getAttackRadius();
        this.cost = type.getCost();
        this.type = type;
        this.frequency = type.getFrequency();
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

    public float getCooldown(){return cooldown;}

    public void setTarget(Enemy newTarget){target=newTarget;}

    public void setCooldown(float newCooldown){cooldown=newCooldown;}
}

package tower;

import model.GameObject;
import projectile.ProjectileType;

/**
 * Created by Daniel on 09.06.2016.
 */
public class Tower extends GameObject{
    private ProjectileType projectile;
    private int attackRadius;

    public Tower(int maxHp, int level, String name, float x, float y, ProjectileType projectile, int attackRadius) {
        super(maxHp, level, name, x, y);
        this.projectile = projectile;
        this.attackRadius = attackRadius;
    }

    public int getAttackRadius() {
        return attackRadius;
    }

    public ProjectileType getProjectile() {
        return projectile;
    }
}

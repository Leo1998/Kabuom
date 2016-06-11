package enemy;

import graph.*;
import model.GameObject;

/**
 * Created by Daniel on 09.06.2016.
 */
public class Enemy extends GameObject{
    private int attackSpeed, speed;
    private List<Vertex> path;
    private float attackCooldown;
    private EnemyType enemyType;

    public Enemy(EnemyType enemyType,int level,int x,int y) {
        super(enemyType.getMaxHP(), level, enemyType.getName(), x, y,enemyType.getRadius());
        this.enemyType = enemyType;
        attackCooldown = 0;
        speed = enemyType.getSpeed();
        attackSpeed = enemyType.getAttackSpeed();
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getSpeed() {
        return speed;
    }

    public List<Vertex> getPath() {
        return path;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public void setPath(List<Vertex> path) {
        this.path = path;
    }

    public void setAttackCooldown(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }
}

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

    public Enemy(int maxHp, int level, String name, float x, float y,float radius, int attackSpeed, int speed, float attackCooldown) {
        super(maxHp, level, name, x, y,radius);
        this.attackSpeed = attackSpeed;
        this.speed = speed;
        this.attackCooldown = attackCooldown;
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

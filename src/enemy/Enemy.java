package enemy;

import graph.*;
import model.GameObject;

/**
 * Created by Daniel on 09.06.2016.
 */
public class Enemy extends GameObject{
    private float attackSpeed, speed;
    private Queue<Vertex> path;
    private float attackCooldown;
    private EnemyType enemyType;
    private Vertex pos;

    public Enemy(EnemyType enemyType,int level,int x,int y,Vertex pos) {
        super(enemyType.getMaxHP(), level, enemyType.getName(), x, y,enemyType.getRadius());
        this.enemyType = enemyType;
        attackCooldown = 0;
        speed = enemyType.getSpeed();
        attackSpeed = enemyType.getAttackSpeed();
        this.pos = pos;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public float getSpeed() {
        return speed;
    }

    public Queue<Vertex> getPath() {
        return path;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public void setPath(Queue<Vertex> path) {
        this.path = path;
    }

    public void setAttackCooldown(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public Vertex getPos() {
        return pos;
    }

    public void setPos(Vertex pos) {
        this.pos = pos;
    }

    public int getDamage(){
        return enemyType.getDamage();
    }
}

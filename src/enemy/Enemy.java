package enemy;

import graph.*;
import model.GameObject;


public class Enemy extends GameObject{
    private float attackSpeed, speed;
    private Queue<Vertex> path;
    private float attackCooldown;
    private EnemyType enemyType;
    private Vertex pos;
    private float[] movement;

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

    public float[] getMovement() {
        return movement;
    }

    public void setMovement(float[] movement) {
        this.movement = movement;
    }
}

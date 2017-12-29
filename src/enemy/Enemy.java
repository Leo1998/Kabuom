package enemy;

import enemy.step.Step;
import model.GameObject;
import utility.Constants;
import utility.Vector2;

import java.util.LinkedList;
import java.util.Queue;


public class Enemy extends GameObject {
    private Queue<Step> path;
    private float attackCooldown, slowDuration;
    private EnemyType enemyType;
    private Vector2 movement;

    /**
     * Konstruktor des Enemy
     *
     * @param enemyType Type des Gegners
     * @param level     Level des Gegners (WIP)
     * @param x         X-Position des Gegners
     * @param y         Y-Position des Gegners
     */
    public Enemy(EnemyType enemyType, int level, float x, float y) {
        super(enemyType.getMaxHP(), level, enemyType.getName(), x, y, enemyType.getRadius(), enemyType.getTextureID());
        this.path = new LinkedList<>();
        this.enemyType = enemyType;
        attackCooldown = 0;
        slowDuration = 0;
        this.movement = new Vector2(0,0);
    }

    /**
     * Gibt zurück, wie weit sich der Gegner innerhalb einer Millisekunde bewegen kann
     *
     * @return
     */
    public float getSpeed() {
        return enemyType.getSpeed() / (slowDuration > 0 ? Constants.slowDebuff : 1);
    }

    /**
     * Gibt die Zeit, die zwischen zwei Angriffen des Gegners verstreichen muss, zurück
     */
    public float getAttackSpeed() {
        return enemyType.getAttackSpeed();
    }

    /**
     * Gibt den, für diesen Gegner berechneten, Pfad zurück
     */
    public Queue<Step> getPath() {
        return path;
    }

    /**
     * Gibt die Zeit, die seit dem letztem Angriff des Gegners verstrichen ist, zurück
     */
    public float getAttackCooldown() {
        return attackCooldown;
    }

    public float getSlowDuration() {
        return slowDuration;
    }

    /**
     * Setzt den Pfad, dem der Gegner folgen soll
     */
    public void setPath(Queue<Step> path) {
        this.path = path;
    }

    /**
     * Setzt die Zeit, die seit dem letztem Angriff verstrichen ist
     */
    public void setAttackCooldown(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public void setSlowDuration(float slowDuration) {
        this.slowDuration = slowDuration;
    }

    /**
     * Gibt den Schaden, den der Gegner mit einem Angriff verursacht, zurück
     */
    public int getDamage() {
        return enemyType.getDamage();
    }

    /**
     * Gibt zurück, in welche Richtung mit welcher Geschwindigkeit sich der Gegner im letzem Frame bewegt hat
     */
    public Vector2 getMovement() {
        return movement;
    }

    /**
     * Setzt, in welche Richtung mit welcher Geschwindigkeit sich der Gegner im letzem Frame bewegt hat
     */
    public void setMovement(Vector2 movement) {
        this.movement = movement;
    }

    public void addAttackCooldown(float attackCooldown){
        this.attackCooldown += attackCooldown;
    }

    public void addSlowDuration(float slowDuration){
        this.slowDuration += slowDuration;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }
}
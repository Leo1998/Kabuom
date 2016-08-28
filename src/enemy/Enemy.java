package enemy;

import graph.*;
import model.GameObject;


public class Enemy extends GameObject {
    private Queue<Vertex> path;
    private float attackCooldown;
    private EnemyType enemyType;
    private Vertex pos;
    private float[] movement;

    /**
     * Konstruktor des Enemy
     *
     * @param enemyType Type des Gegners
     * @param level     Level des Gegners (WIP)
     * @param x         X-Position des Gegners
     * @param y         Y-Position des Gegners
     * @param pos       Vertex, auf dem sich der Gegner befindet
     */
    public Enemy(EnemyType enemyType, int level, float x, float y, Vertex pos) {
        super(enemyType.getMaxHP(), level, enemyType.getName(), x, y, enemyType.getRadius(), enemyType.getTextureID());
        this.path = new Queue<>();
        this.enemyType = enemyType;
        attackCooldown = 0;
        this.pos = pos;
        movement = new float[]{0f,0f};
    }

    /**
     * Gibt zurück, wie weit sich der Gegner innerhalb einer Millisekunde bewegen kann
     *
     * @return
     */
    public float getSpeed() {
        return enemyType.getSpeed();
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
    public Queue<Vertex> getPath() {
        return path;
    }

    /**
     * Gibt die Zeit, die seit dem letztem Angriff des Gegners verstrichen ist, zurück
     */
    public float getAttackCooldown() {
        return attackCooldown;
    }

    /**
     * Setzt den Pfad, dem der Gegner folgen soll
     */
    public void setPath(Queue<Vertex> path) {
        this.path = path;
    }

    /**
     * Setzt die Zeit, die seit dem letztem Angriff verstrichen ist
     */
    public void setAttackCooldown(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    /**
     * Gibt den letzten Vertex im normalen Graphen, an dem sich der Gegner befand, zurück
     */
    public Vertex getPos() {
        return pos;
    }

    /**
     * Setzt den letzten Vertex im normalen Graphen, an dem sich der Gegner befand
     */
    public void setPos(Vertex pos) {
        this.pos = pos;
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
    public float[] getMovement() {
        return movement;
    }

    /**
     * Setzt, in welche Richtung mit welcher Geschwindigkeit sich der Gegner im letzem Frame bewegt hat
     */
    public void setMovement(float[] movement) {
        this.movement = movement;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }
}

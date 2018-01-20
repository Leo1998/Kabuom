package enemy;

import enemy.step.Step;
import model.GameObject;
import model.ObjectType;
import utility.Constants;
import utility.Vector2;
import world.Block;

import java.util.Stack;


public class Enemy extends GameObject {
    private Stack<Step> path;
    private float attackCooldown;
    public final EnemyType enemyType;
    private Vector2 movement;
    private float[] effects;
    private EnemyHandler enemyHandler;
    private Block block;
    public final int wave;

    /**
     * Konstruktor des Enemy
     *
     * @param enemyType Type des Gegners
     * @param level     Level des Gegners (WIP)
     * @param x         X-Position des Gegners
     * @param y         Y-Position des Gegners
     */
    public Enemy(EnemyType enemyType, int level, float x, float y, EnemyHandler enemyHandler, int wave) {
        super(enemyType, level, x, y);
        this.path = new Stack<>();
        this.enemyType = enemyType;
        attackCooldown = 0;
        this.movement = new Vector2(0, 0);
        this.effects = new float[EffectType.values().length];
        this.enemyHandler = enemyHandler;
        this.block = null;
        this.wave = wave;
    }

    /**
     * Gibt zurück, wie weit sich der Gegner innerhalb einer Millisekunde bewegen kann
     *
     * @return
     */
    public float getSpeed() {
        return enemyType.speed / getStrength(EffectType.SLOW);
    }

    /**
     * Gibt die Zeit, die zwischen zwei Angriffen des Gegners verstreichen muss, zurück
     */
    public float getAttackSpeed() {
        return enemyType.attackSpeed * getStrength(EffectType.SLOW);
    }

    /**
     * Gibt den, für diesen Gegner berechneten, Pfad zurück
     */
    public Stack<Step> getPath() {
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
    public void setPath(Stack<Step> path) {
        this.path = path;
    }

    /**
     * Setzt die Zeit, die seit dem letztem Angriff verstrichen ist
     */
    public void setAttackCooldown(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    /**
     * Gibt den Schaden, den der Gegner mit einem Angriff verursacht, zurück
     */
    public int getDamage() {
        return enemyType.damage;
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

    public void addAttackCooldown(float attackCooldown) {
        this.attackCooldown += attackCooldown;
    }

    public void addEffect(EffectType effectType){
        effects[effectType.id] = effectType.duration;
        if(effectType == EffectType.BURNING && Constants.fireBreaksSlow){
            effects[EffectType.SLOW.id] = 0;
        }else if(effectType == EffectType.SLOW && Constants.fireBreaksSlow){
            effects[EffectType.BURNING.id] = 0;
        }
    }

    public void updateEffects(float duration) {
        for (int i = 0; i < effects.length; i++) {
            if (effects[i] < 0) {
                effects[i] = 0;
            } else if(effects[i] > 0){
                effects[i] -= duration;
                switch (EffectType.values()[i]){
                    case BURNING:
                    case POISON:
                        float damage = enemyType.getMaxHP()*EffectType.values()[i].strength*duration;
                        addHp(-damage);
                        break;
                }
            }
        }
    }

    @Override
    public void addHp(float hp) {
        float temp = hp * getStrength(EffectType.BLEEDING);
        super.addHp(temp);
        if(hp < 0) {
            enemyHandler.addDamage(-hp, getX(), getY());
        }
    }

    private float getStrength(EffectType effectType) {
        return (effects[effectType.id] > 0) ? effectType.strength : 1;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    @Override
    public ObjectType getObjectType() {
        return enemyType;
    }
}
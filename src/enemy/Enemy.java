package enemy;

import enemy.effect.Effect;
import enemy.effect.EffectType;
import enemy.step.Step;
import model.GameObject;
import utility.Vector2;
import world.Block;

import java.util.ArrayList;
import java.util.Stack;


public class Enemy extends GameObject {
    private Stack<Step> path;
    private float attackCooldown;
    public final EnemyType enemyType;
    private Vector2 movement;
    private ArrayList<Effect> effects;
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
        this.effects = new ArrayList<>();
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

    public void addEffect(EffectType effectType) {
        boolean inList = false;
        for (Effect effect : effects) {
            if (effect.effectType == effectType) {
                inList = true;
                effect.setDuration(effectType.duration);
                break;
            }
        }
        if (!inList) {
            effects.add(new Effect(effectType));
        }
    }

    public void removeEffect(EffectType effectType) {
        for (int i = 0; i < effects.size(); ) {
            Effect effect = effects.get(i);
            if (effect.effectType == effectType) {
                effects.remove(effect);
            } else {
                i++;
            }
        }
    }

    public boolean hasEffect(EffectType effectType) {
        return effects.contains(new Effect(effectType));
    }

    public void addEffectDuration(float duration) {
        for (int i = 0; i < effects.size(); ) {
            Effect effect = effects.get(i);
            if (effect.getDuration() < 0) {
                effects.remove(effect);
            } else {
                effect.addDuration(duration);
                i++;
            }
        }
    }

    @Override
    public void addHp(float hp) {
        float temp = hp * getStrength(EffectType.BLEEDING);
        super.addHp(temp);
        enemyHandler.addDamage(-hp,getX(),getY());
    }

    private float getStrength(EffectType effectType) {
        return (effects.contains(new Effect(effectType)) ? effectType.strength : 1);
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}
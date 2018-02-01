package entity.model;

import entity.EntityHandler;
import model.GameObject;
import model.ObjectType;
import utility.Constants;
import world.Block;

import static utility.Utility.random;

public class Entity extends GameObject implements Partisan {

    private Entity target;
    public final EntityType entityType;
    private final float[] effects;
    private int wave;
    protected Block block;
    private boolean isEnemy;
    private float attackCooldown;

    private static EntityHandler entityHandler;

    public Entity(EntityType entityType, int level, float x, float y, int wave, Block block, boolean isEnemy) {
        super(entityType, level, x, y);
        this.entityType = entityType;
        this.effects = new float[EffectType.values().length];
        this.wave = wave;
        this.block = block;
        attackCooldown = 0;
        this.isEnemy = isEnemy;
    }

    public static void setEntityHandler(EntityHandler entityHandler){
        Entity.entityHandler = entityHandler;
    }

    @Override
    public ObjectType getObjectType() {
        return entityType;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public void addEffect(EffectType effectType){
        effects[effectType.ordinal()] = effectType.duration;
        if(effectType == EffectType.BURNING && Constants.fireBreaksSlow){
            effects[EffectType.SLOW.ordinal()] = 0;
        }else if(effectType == EffectType.SLOW && Constants.fireBreaksSlow){
            effects[EffectType.BURNING.ordinal()] = 0;
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
                        float damage = entityType.getMaxHP()*EffectType.values()[i].strength*duration;
                        addHp(-damage, "Effect");
                        break;
                }
            }
        }
    }

    public void addHp(float hp, String source) {
        float temp = hp * getStrength(EffectType.BLEEDING);
        super.addHp(temp);
        if(isEnemy()) {
            entityHandler.addDamage(-temp, this);
        }

        if(getHp() <= 0 && getHp() - temp > 0){
            System.out.println(entityType.name + " killed by " + source);
        }
    }

    private float getStrength(EffectType effectType) {
        return (effects[effectType.ordinal()] > 0) ? effectType.strength : 1;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public boolean addAttackCooldown(float dt){
        if(entityType.frequency > 0) {
            attackCooldown += dt / getStrength(EffectType.SLOW);
            if (attackCooldown > entityType.frequency) {
                attackCooldown = random.nextFloat()*entityType.frequency/2 - entityType.frequency/4;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public float getSpeed(){
        return entityType.speed / getStrength(EffectType.SLOW);
    }

    @Override
    public boolean isEnemy() {
        return isEnemy;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "x=" + getX() +
                ", y=" + getY() +
                ", hp=" + getHp() +
                ", entityType=" + entityType +
                ", wave=" + wave +
                ", block=(" + block.x + "|" + block.y + ")" +
                '}';
    }
}

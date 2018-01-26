package entity.model;

import entity.EntityHandler;
import model.GameObject;
import model.ObjectType;
import utility.Constants;
import world.Block;

public class Entity extends GameObject implements Partisan {

    private Entity target;
    public final EntityType entityType;
    private final float[] effects;
    public final int wave;
    protected Block block;
    private float attackCooldown;

    private static EntityHandler entityHandler;

    public Entity(EntityType entityType, int level, float x, float y, int wave, Block block) {
        super(entityType, level, x, y);
        this.entityType = entityType;
        this.effects = new float[EffectType.values().length];
        this.wave = wave;
        this.block = block;
        attackCooldown = 0;
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
        if(isEnemy()) {
            entityHandler.addDamage(-temp, this);
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
            attackCooldown += dt;
            if (attackCooldown > entityType.frequency) {
                attackCooldown = 0;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isEnemy() {
        return wave >= 0;
    }

    @Override
    public boolean allyOf(Partisan partisan) {
        if(partisan == null){
            return true;
        } else {
            return partisan.isEnemy() == isEnemy();
        }
    }

    @Override
    public String toString() {
        return "Entity{" +
                "x=" + getX() +
                ", y=" + getY() +
                ", entityType=" + entityType +
                ", wave=" + wave +
                ", block=" + block +
                '}';
    }
}

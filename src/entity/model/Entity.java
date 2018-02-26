package entity.model;

import entity.EntityHandler;
import model.GameObject;
import model.ObjectType;
import projectile.ProjectileType;
import utility.Constants;
import world.Block;

import static utility.Utility.random;

public class Entity extends GameObject implements Partisan {

    private Entity target;
    private final EntityType entityType;
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
    protected ObjectType getObjectType() {
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
            System.out.println(getName() + " killed by " + source);
        }
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public boolean addAttackCooldown(float dt){
        if(getFrequency() > 0) {
            attackCooldown += dt / getStrength(EffectType.SLOW);
            if (attackCooldown > getFrequency()) {
                attackCooldown = random.nextFloat()*getFrequency()/2 - getFrequency()/4;
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

    /**
     * Getter for Enum
     */

    public float getSpeed(){
        return entityType.speed * getUpgrade(1) / getStrength(EffectType.SLOW);
    }

    public String getTurretTexture(){
        return entityType.turretTexture;
    }

    public String getBaseTexture(){
        return entityType.baseTexture;
    }

    public float getFrequency(){
        return entityType.frequency * 1/getUpgrade(2);
    }

    public float getRange(){
        return entityType.range * getUpgrade(3);
    }

    public float getAccuracy(){
        return entityType.accuracy * 1/getUpgrade(4);
    }

    public int getAttack(){
        return Math.round(entityType.attack * getUpgrade(5));
    }

    public boolean attacksAllies(){
        return entityType.attacksAllies;
    }

    public boolean attacksHostiles(){
        return entityType.attacksHostiles;
    }

    public boolean hitsAllies(){
        return entityType.hitsAllies;
    }

    public boolean hitsHostiles(){
        return entityType.hitsHostiles;
    }

    public ObjectType getProjectile(){
        return entityType.projectile;
    }

    public int getCost(){
        return Math.round(entityType.cost * getUpgrade(6));
    }

    public boolean isRanged(){
        return entityType.isRanged();
    }

    public boolean isMaintower(){
        return entityType == EntityType.MAINTOWER;
    }

    private float getStrength(EffectType effectType) {
        return (effects[effectType.ordinal()] > 0) ? effectType.strength : 1;
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
                ", level=" + level +
                '}';
    }

    @Override
    public Entity clone(){
        return new Entity(entityType,level,getX(),getY(),wave,block,isEnemy);
    }
}

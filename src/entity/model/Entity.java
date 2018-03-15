package entity.model;

import entity.EntityHandler;
import entity.utility.EffectType;
import entity.utility.Partisan;
import model.GameObject;
import model.ObjectType;
import world.Block;

import java.nio.ByteBuffer;

import static utility.Utility.random;

public class Entity extends GameObject implements Partisan, Comparable<Entity> {

    private Entity target;
    private final EntityType entityType;
    private final float[] effects;
    private final float[] buffs;
    private int wave;
    protected Block block;
    private boolean isEnemy;
    private float attackCooldown;
    private final int i;
    private int ammo;

    private static int num = Integer.MIN_VALUE;

    private static EntityHandler entityHandler;

    public Entity(EntityType entityType, int level, float x, float y, int wave, Block block, boolean isEnemy) {
        super(entityType, level, x, y);
        this.entityType = entityType;
        this.effects = new float[EffectType.values().length];
        this.buffs = new float[EffectType.BuffType.values().length];
        this.wave = wave;
        this.block = block;
        attackCooldown = 0;
        this.isEnemy = isEnemy;
        ammo = 0;
        i = num;
        num++;
    }

    public Entity(ByteBuffer buf, Block[][] blocks){
        super(buf);

        this.entityType = EntityType.values()[buf.getInt()];
        this.wave = buf.getInt();
        this.ammo = buf.getInt();

        this.effects = new float[EffectType.values().length];
        this.buffs = new float[EffectType.BuffType.values().length];
        this.block = blocks[Math.round(getY())][Math.round(getY())];
        this.block.addEntity(this);
        attackCooldown = 0;

        i = num;
        num++;
    }

    @Override
    public void write(ByteBuffer buf){
        super.write(buf);
        buf.putInt(entityType.ordinal());
        buf.putInt(wave);
        buf.putInt(ammo);
    }

    public static int byteSize(){
        return GameObject.byteSize() + 3*4;
    }

    @Override
    public byte firstByte(){
        byte src = super.firstByte();
        if(isEnemy){
            return (byte)(src | byteMask.isEnemy.mask);
        } else {
            return src;
        }
    }

    @Override
    public void firstByte(byte b){
        super.firstByte(b);
        isEnemy = ((b & byteMask.isEnemy.mask) == byteMask.isEnemy.mask);
    }

    public enum byteMask{
        isMove((byte)0x01),
        isMinion((byte)0x02),
        isEnemy((byte)0x04);

        public final byte mask;

        byteMask(byte mask){
            this.mask = mask;
        }
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
        if(effects[effectType.ordinal()] <= 0){
            for(int j = 0; j < effectType.buffs.length; j++){
                buffs[j] += effectType.buffs[j];
            }
        }
        effects[effectType.ordinal()] = effectType.duration;
    }

    public void updateEffects(float duration) {
        for (int i = 0; i < effects.length; i++) {
            EffectType effectType = EffectType.values()[i];
            if (effects[i] < 0) {
                effects[i] = 0;
                for(int j = 0; j < effectType.buffs.length; j++){
                    buffs[j] -= effectType.buffs[j];
                }
            } else if(effects[i] > 0){
                effects[i] -= duration;
            }
        }
        addHp(getMaxHp() * buffs[EffectType.BuffType.DOT.ordinal()] * duration);
    }

    public void addHp(float hp, String source) {
        float temp = hp * (1+buffs[EffectType.BuffType.DAMAGE.ordinal()]);
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
            attackCooldown += dt;

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

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo){
        this.ammo = ammo;
    }

    public void addAmmo(int ammo) {
        this.ammo += ammo;
    }

    /**
     * Getter for Enum
     */

    public float getSpeed(){
        float temp = entityType.speed * getUpgrade(1);
        temp += temp * buffs[EffectType.BuffType.SPEED.ordinal()];
        return temp;
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

    public ObjectType getProjectile(){
        return entityType.projectile;
    }

    public int getCost(){
        return Math.round(entityType.cost * getUpgrade(6));
    }

    public boolean isRanged(){
        return entityType.isRanged();
    }

    public boolean isSpawner(){
        return isRanged() && entityType.projectile instanceof EntityType;
    }

    public int getReward(){
        return (level+1)*entityType.cost;
    }

    public boolean attacks(Partisan partisan){
        return (allyOf(partisan) ? entityType.attacksAllies : entityType.attacksHostiles);
    }

    public boolean isType(EntityType entityType){
        return this.entityType == entityType;
    }

    @Override
    public boolean isEnemy() {
        return isEnemy;
    }

    @Override
    public int compareTo(Entity o) {
        return o.i - this.i;
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
                ", ammo=" + ammo +
                '}';
    }

    @Override
    public Entity clone(){
        return new Entity(entityType,level,getX(),getY(),wave,block,isEnemy);
    }
}

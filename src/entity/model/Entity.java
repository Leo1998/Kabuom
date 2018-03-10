package entity.model;

import entity.EntityHandler;
import entity.utility.EffectType;
import entity.utility.Partisan;
import model.GameObject;
import model.ObjectType;
import org.json.JSONObject;
import world.Block;

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

    public Entity(JSONObject object, Block[][] blocks){
        super(object);

        this.entityType = EntityType.values()[object.getInt("type")];
        this.effects = new float[EffectType.values().length];
        this.buffs = new float[EffectType.BuffType.values().length];
        this.wave = object.getInt("wave");
        this.block = blocks[Math.round(getX())][Math.round(getY())];
        this.block.addEntity(this);
        attackCooldown = 0;
        this.isEnemy = object.getBoolean("isEnemy");
        ammo = object.getInt("ammo");
        i = num;
        num++;
    }

    public JSONObject toJSON(){
        JSONObject object = super.toJSON();

        object.put("type",entityType.ordinal());
        object.put("wave",wave);
        object.put("isEnemy",isEnemy);
        object.put("isMove",this instanceof MoveEntity);
        object.put("ammo",ammo);

        return object;
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

package model;

import utility.Utility;

import java.nio.ByteBuffer;

public abstract class GameObject implements Position {
    protected int level;
    private float x, y, hp;

    public GameObject(ObjectType objectType, int level, float x, float y) {
        this.level = level;
        this.x = x;
        this.y = y;
        //Folgende Berechnung kann nicht durch getMaxHp() ersetzt werden, da objectType zuvor von der Unterklasse gesetzt werden muss
        this.hp = objectType.getMaxHP() * objectType.getUpgrade().getStrength(level,0);
    }

    public GameObject(ByteBuffer buf){
        firstByte(buf.get());
        this.level = buf.getInt();
        this.x = buf.getFloat();
        this.y = buf.getFloat();
        this.hp = buf.getFloat();
    }

    public void write(ByteBuffer buf){
        buf.put(firstByte());
        buf.putInt(level);
        buf.putFloat(x);
        buf.putFloat(y);
        buf.putFloat(hp);
    }

    public static int byteSize(){
        return 4*4 + 1;
    }

    public byte firstByte(){
        return 0x00;
    }

    public void firstByte(byte b){

    }

    public float getHp() {
        return hp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level){
        this.hp += getObjectType().getMaxHP() * (getObjectType().getUpgrade().getStrength(level, 0) - getObjectType().getUpgrade().getStrength(this.level, 0));
        this.level = level;
    }

    public void upgrade(){
        level++;
        this.hp += getObjectType().getMaxHP() * (getObjectType().getUpgrade().getStrength(level,0) - getObjectType().getUpgrade().getStrength(level-1,0));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public void addHp(float hp) {
        this.hp += hp;
        if(this.hp > getMaxHp()){
            this.hp = getMaxHp();
        }
    }

    public float getRadius(){
        return getObjectType().getRadius();
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    protected abstract ObjectType getObjectType();

    public float getMaxHp(){
        return getObjectType().getMaxHP() * getUpgrade(0);
    }

    protected float getUpgrade(int index){
        return getObjectType().getUpgrade().getStrength(level,index);
    }

    public String getName(){
        String name = getObjectType().getName();

        if(level >= 0){
            name += " " + Utility.intToRoman(level + 1);
            //name += " " + (level + 1);
        }

        return name;
    }
}

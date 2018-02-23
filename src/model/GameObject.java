package model;

import utility.Utility;

public abstract class GameObject implements Position {
    protected int level;
    private float x, y, hp;

    public GameObject(ObjectType objectType, int level, float x, float y) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.hp = objectType.getMaxHP() + objectType.getMaxHP() * 0.5f * level;
    }

    public float getHp() {
        return hp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level){
        this.hp += getObjectType().getMaxHP()*0.5f*(level-this.level);
        this.level = level;
    }

    public void upgrade(){
        level++;
        this.hp += getObjectType().getMaxHP()*0.5f;
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
        if(this.hp > getObjectType().getMaxHP()){
            this.hp = getObjectType().getMaxHP();
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
        return getObjectType().getMaxHP() + getObjectType().getMaxHP() * 0.5f * level;
    }

    public String getName(){
        String name = getObjectType().getName();

        if(level > 0){
            name += " " + Utility.intToRoman(level);
        }

        return name;
    }
}

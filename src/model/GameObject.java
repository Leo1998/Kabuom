package model;


public abstract class GameObject implements Position {
    private int level;
    private float x, y, hp;

    public GameObject(ObjectType objectType, int level, float x, float y) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.hp = objectType.getMaxHP();
    }

    public float getHp() {
        return hp;
    }

    public int getLevel() {
        return level;
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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public abstract ObjectType getObjectType();
}

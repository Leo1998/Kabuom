package model;

public abstract class GameObject {
    private int level;
    private String name;
    private float x, y, radius, hp, maxHp;

    public GameObject(ObjectType type, int level, float x, float y) {
        this.maxHp = type.getMaxHP();
        this.level = level;
        this.name = type.getName();
        this.x = x;
        this.y = y;
        this.radius = type.getRadius();
        this.hp = type.getMaxHP();
    }

    public float getRadius() {
        return radius;
    }

    public float getHp() {
        return hp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
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
        if(this.hp > maxHp){
            this.hp = maxHp;
        }
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}

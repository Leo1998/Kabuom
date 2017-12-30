package model;

public abstract class GameObject {
    private int level;
    private String name;
    private float x, y, radius, hp, maxHp;

    public GameObject(float maxHp, int level, String name, float x, float y, float radius, String textureID) {
        this.maxHp = maxHp;
        this.level = level;
        this.name = name;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.hp = maxHp;
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

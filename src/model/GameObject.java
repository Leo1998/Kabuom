package model;

import view.rendering.ITexture;

public class GameObject {
    private int hp,maxHp,level;
    private String name;
    private float x,y,radius;
    protected ITexture texture;




    public GameObject(int maxHp, int level, String name, float x, float y, float radius) {
        this.maxHp = maxHp;
        this.level = level;
        this.name = name;
        this.x = x;
        this.y = y;
        this.radius = radius;
        hp = maxHp;


    }

    public ITexture getTexture(){return texture;}

    public float getRadius(){return radius; }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
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

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}

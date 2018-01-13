package enemy;

import model.ObjectType;

public enum EnemyType implements ObjectType {


    Cheap(0.3f,/* maxHP*/ 30,/*speed*/ 2, 5, 0.5f, false, "Cheap", "enemyTextures/Gegner1SLinks.png"),
    Tank(1,/* maxHP*/ 480, /*speed*/ 1.5f, 5, 0.5f, false, "Tank", "enemyTextures/Gegner2SLinks.png"),
    Speed(0.2f,/* maxHP*/ 75, /*speed*/ 3, 15, 0.5f, false, "Speed", "enemyTextures/Gegner3SLinks.png"),
    Damage(0.1f,/* maxHP*/ 150, /*speed*/ 2, 60, 0.5f, false, "Damage", "enemyTextures/Endgegner1SLinks.png"),
    Super(0.5f,/* maxHP*/ 480, /*speed*/ 1f, 70, 0.5f, true, "Super", "enemyTextures/Endgegner2SLinks.png"),
    Troll(1,/* maxHP*/ 600, /*speed*/ 0.5f, 75, 0.5f, true, "Troll", "enemyTextures/Endgegner3SLinks.png"),
    Cheat(0.0000000f,/* maxHP*/ Integer.MAX_VALUE, /*speed*/ Float.MAX_VALUE, Integer.MAX_VALUE, 0.5f, true, "Cheat", "test1.png");

    public final float attackSpeed;
    public final int maxHP;
    public final float speed;
    public final int damage;
    public final float radius;
    public final boolean aggressive;
    public final String name;
    public final String textureID;

    EnemyType(float attackSpeed, int maxHP, float speed, int damage, float radius, boolean aggressive, String name, String textureID) {
        this.attackSpeed = attackSpeed;
        this.maxHP = maxHP;
        this.speed = speed;
        this.damage = damage;
        this.radius = radius;
        this.aggressive = aggressive;
        this.name = name;
        this.textureID = textureID;
    }


    public float getMaxHP() {
        return maxHP;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public String getName() {
        return name;
    }


}

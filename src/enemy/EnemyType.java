package enemy;

public enum EnemyType {


    Cheap (0.2f,/* HP*/ 25,/*speed*/ 2,5,0.5f,"Cheap", "enemyTextures/Gegner1SLinks.png"),
    Tank (1,/* HP*/ 400, /*speed*/ 1f,5,0.5f,"Tank", "enemyTextures/Gegner2SLinks.png"),
    Speed (0.1f,/* HP*/ 50, /*speed*/ 3,15,0.5f,"Speed", "enemyTextures/Gegner3SLinks.png"),
    Damage (0.2f,/* HP*/ 150, /*speed*/ 2,60,0.5f,"Damage", "enemyTextures/Endgegner1SLinks.png"),
    Super (0.1f,/* HP*/ 500, /*speed*/ 1f,70,0.5f,"Super", "enemyTextures/Endgegner2SLinks.png"),
    Troll (1,/* HP*/ 500, /*speed*/ 0.2f,0,0.5f,"Troll", "enemyTextures/Endgegner3SLinks.png"),
    Cheat (0.0000000f,/* HP*/ Integer.MAX_VALUE, /*speed*/ Float.MAX_VALUE,Integer.MAX_VALUE,0.5f,"Cheat", "test1.png");

    private final float attackSpeed;
    private final int maxHP;
    private final float speed;
    private final int damage;
    private final float radius;
    private final String name;
    private final String textureID;

    EnemyType(float attackSpeed, int maxHP, float speed, int damage, float radius, String name, String textureID) {
        this.attackSpeed = attackSpeed;
        this.maxHP = maxHP;
        this.speed = speed;
        this.damage = damage;
        this.radius = radius;
        this.name = name;
        this.textureID = textureID;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public String getName() {
        return name;
    }

    public float getRadius() {
        return radius;
    }

    public float getSpeed() {
        return speed;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public int getDamage() {
        return damage;
    }

    public String getTextureID() {
        return textureID;
    }
}

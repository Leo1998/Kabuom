package enemy;

/**
 * Created by 204g11 on 10.06.2016.
 */
public enum EnemyType {
    Cheap (0.2f,25,1,5,10,"Cheap"),
    Tank (1,400,0.5f,5,50,"Tank"),
    Speed (0.1f,50,2,15,10,"Speed"),
    Damage (0.2f,150,0.8f,60,30,"Damage"),
    Super (0.1f,500,1,70,50,"Super"),
    Troll (1,500,0.2f,0,50,"Troll"),
    Cheat (0,Integer.MAX_VALUE,Float.MAX_VALUE,Integer.MAX_VALUE,1,"Cheat");

    private final float attackSpeed;
    private final int maxHP;
    private final float speed;
    private final int damage;
    private final int radius;
    private final String name;

    EnemyType(float attackSpeed, int maxHP, float speed, int damage, int radius, String name) {
        this.attackSpeed = attackSpeed;
        this.maxHP = maxHP;
        this.speed = speed;
        this.damage = damage;
        this.radius = radius;
        this.name = name;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public String getName() {
        return name;
    }

    public int getRadius() {
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
}

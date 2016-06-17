package enemy;

/**
 * Created by 204g11 on 10.06.2016.
 */
public enum EnemyType {
    TYPE1 (50,50,50,50,50,"1"),
    TYPE2 (50,50,50,50,50,"2"),
    TYPE3 (50,50,50,50,50,"3");

    private final int maxHP;
    private final float speed;
    private final float attackSpeed;
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

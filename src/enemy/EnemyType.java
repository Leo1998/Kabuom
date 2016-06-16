package enemy;

/**
 * Created by 204g11 on 10.06.2016.
 */
public enum EnemyType {
    TYPE1 (50,50,50,50,50,"1"),
    TYPE2 (50,50,50,50,50,"2"),
    TYPE3 (50,50,50,50,50,"3");

    private final int maxHP;
    private final int speed;
    private final int attackSpeed;
    private final int damage;
    private final int radius;
    private final String name;

    EnemyType(int attackSpeed, int maxHP, int speed, int damage, int radius, String name) {
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

    public int getSpeed() {
        return speed;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getDamage() {
        return damage;
    }
}

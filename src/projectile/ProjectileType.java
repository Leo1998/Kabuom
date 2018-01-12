package projectile;

import enemy.effect.EffectType;
import model.ObjectType;

public enum ProjectileType implements ObjectType {


    BULLET(1, 1, 0.0625f, "BULLET", 25f,/*Damage*/8, /*Range*/  8, EffectType.BLEEDING, "projektil.png"),
    MISSILE(1, 1, 0.0625f, "MISSILE", 15,/*Damage*/200, /*Range*/14, null, "missileKlein.png"),
    FLAME(3, 1, 0.0625f, "FLAME", 5,/*Damage*/ 8, /*Range*/ 7, EffectType.BURNING, "flame.png"),
    ICE(1, 1, 0.0625f, "ICE", 5,/*Damage*/ 200, /*Range*/ 10, EffectType.SLOW, "IceBullet.png"),
    LIGHTNING(5, 1, 0.0625f, "LIGHTNING", 15,/*Damage*/3, /*Range*/ 12, null, "Laser.png"),
    PIERCINGBULLET(3, 1, 0.0625f, "PIERCINGBULLET", 27,/*Damage*/150, /*Range*/30, EffectType.BLEEDING, "Piercingprojektil.png"),
    FRAGGRENADE(1, 1, 0.0625f, "FRAGGRENADE", 30,/*Damage*/100, /*Range*/ 20, EffectType.BURNING, "missile.png"),
    POISON(1, 1, 0.0625f, "POISON", 5,/*Damage*/25,/*Range*/ 10, EffectType.POISON, "toxicBullet.png"),
    POISONTRAIL(1, 1, 0.0625f, "POISON", 0.002f,/*Damage*/15,/*Range*/ 0.01f, EffectType.POISON, "Giftgas.png"),
    EXPLOSION(Integer.MAX_VALUE, 5, 2.5f, "EXPLOSION", 0.01f, /*Damage*/ 100, /*Range*/ 0.002f, EffectType.BURNING, "Explosion.png");

    public final int maxHP;
    public final String name;
    public final float radius, speed, range, damageRadius;
    public final int impactDamage;
    public final String textureID;
    public final EffectType effectType;

    ProjectileType(int maxHP, float radius, float damageRadius, String name, float speed, int impactDamage, float range, EffectType effectType, String textureID) {
        this.maxHP = maxHP;
        this.radius = radius;
        this.damageRadius = damageRadius;
        this.name = name;
        this.speed = speed;
        this.impactDamage = impactDamage;
        this.range = range;
        this.effectType = effectType;
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

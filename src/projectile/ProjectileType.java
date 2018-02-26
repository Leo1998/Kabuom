package projectile;

import entity.model.EffectType;
import model.ObjectType;
import model.UpgradeOrder;

public enum ProjectileType implements ObjectType {


    BULLET(1, 0.5f, "BULLET", 25f,/*Damage*/8, /*Range*/  8, EffectType.BLEEDING, "projektil.png", false, true, Ability.NULL, UpgradeOrder.DEFAULTPROJECTILE),
    MISSILE(1, 0.5f, "MISSILE", 15,/*Damage*/200, /*Range*/14, EffectType.BURNING, "missileKlein.png", false, true, Ability.NULL, UpgradeOrder.DEFAULTPROJECTILE),
    FLAME(3, 0.5f, "FLAME", 5,/*Damage*/ 8, /*Range*/ 7, EffectType.BURNING, "flame.png", false, true, Ability.NULL, UpgradeOrder.DEFAULTPROJECTILE),
    ICE(1, 0.5f, "ICE", 5,/*Damage*/ 200, /*Range*/ 10, EffectType.SLOW, "IceBullet.png", false, true, Ability.NULL, UpgradeOrder.DEFAULTPROJECTILE),
    LIGHTNING(10, 0.5f, "LIGHTNING", 20,/*Damage*/5, /*Range*/ 16, null, "Laser.png", false, true, Ability.RANDOMROTATION, UpgradeOrder.DEFAULTPROJECTILE),
    PIERCINGBULLET(3, 0.5f, "PIERCINGBULLET", 27,/*Damage*/150, /*Range*/30, EffectType.BLEEDING, "Piercingprojektil.png", false, true, Ability.NULL, UpgradeOrder.DEFAULTPROJECTILE),
    FRAGGRENADE(1, 0.5f, "FRAGGRENADE", 30,/*Damage*/25, /*Range*/ 20, EffectType.BURNING, "missile.png", false, true, Ability.EXPLOSION, UpgradeOrder.DEFAULTPROJECTILE),
    POISON(1, 0.5f, "POISON", 5,/*Damage*/25,/*Range*/ 10, EffectType.POISON, "toxicBullet.png", false, true, Ability.POISONCLOUD, UpgradeOrder.DEFAULTPROJECTILE),
    POISONTRAIL(5, 0.5f, "POISON", 0.002f,/*Damage*/15,/*Range*/ 0.01f, EffectType.POISON, "Giftgas.png", false, true, Ability.NULL, UpgradeOrder.DEFAULTPROJECTILE),
    EXPLOSION(Integer.MAX_VALUE, 2.5f, "EXPLOSION", 0.01f, /*Damage*/ 50, /*Range*/ 0.002f, EffectType.BURNING, "Explosion.png", true, true, Ability.NULL, UpgradeOrder.DEFAULTPROJECTILE);

    public final int maxHP;
    public final String name;
    public final float radius, speed, range;
    public final int impactDamage;
    public final String textureID;
    public final EffectType effectType;
    public final boolean hitHostiles, hitAllies;
    public final Ability ability;
    public final UpgradeOrder upgradeOrder;

    ProjectileType(int maxHP, float radius, String name, float speed, int impactDamage, float range, EffectType effectType, String textureID, boolean hitAllies, boolean hitHostiles, Ability ability, UpgradeOrder upgradeOrder) {
        this.maxHP = maxHP;
        this.radius = radius;
        this.name = name;
        this.speed = speed;
        this.impactDamage = impactDamage;
        this.range = range;
        this.effectType = effectType;
        this.textureID = textureID;
        this.hitAllies = hitAllies;
        this.hitHostiles = hitHostiles;
        this.ability = ability;
        this.upgradeOrder = upgradeOrder;
    }

    @Override
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

    @Override
    public UpgradeOrder getUpgradeOrder() {
        return upgradeOrder;
    }

    public enum Ability{
        NULL,
        RANDOMROTATION,
        EXPLOSION,
        POISONCLOUD,
    }
}

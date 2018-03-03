package projectile;

import entity.model.EffectType;
import model.ObjectType;
import model.Upgrade;

public enum ProjectileType implements ObjectType {

    //              maxHP               radius  name                speed   dmg     range   effectType              textureID                   hAlly   hHost   ability                 upgrade
    BULLET(         1,                  0.5f,   "BULLET",           25,     8,      8,      EffectType.BLEEDING,    "projektil.png",            false,  true,   Ability.NULL,           Upgrade.DEFAULTPROJECTILE),
    MISSILE(        1,                  0.5f,   "MISSILE",          15,     200,    14,     EffectType.BURNING,     "missileKlein.png",         false,  true,   Ability.NULL,           Upgrade.DEFAULTPROJECTILE),
    FLAME(          3,                  0.5f,   "FLAME",            5,      8,      7,      EffectType.BURNING,     "flame.png",                false,  true,   Ability.NULL,           Upgrade.DEFAULTPROJECTILE),
    ICE(            1,                  0.5f,   "ICE",              5,      200,    10,     EffectType.SLOW,        "IceBullet.png",            false,  true,   Ability.NULL,           Upgrade.DEFAULTPROJECTILE),
    LIGHTNING(      10,                 0.5f,   "LIGHTNING",        20,     5,      16,     null,                   "Laser.png",                false,  true,   Ability.RANDOMROTATION, Upgrade.DEFAULTPROJECTILE),
    PIERCINGBULLET( 3,                  0.5f,   "PIERCINGBULLET",   27,     150,    30,     EffectType.BLEEDING,    "Piercingprojektil.png",    false,  true,   Ability.NULL,           Upgrade.DEFAULTPROJECTILE),
    FRAGGRENADE(    1,                  0.5f,   "FRAGGRENADE",      30,     25,     20,     EffectType.BURNING,     "missile.png",              false,  true,   Ability.EXPLOSION,      Upgrade.DEFAULTPROJECTILE),
    POISON(         10,                 0.5f,   "POISON",           5,      25,     10,     EffectType.POISON,      "toxicBullet.png",          false,  true,   Ability.POISONCLOUD,    Upgrade.DEFAULTPROJECTILE),
    POISONTRAIL(    5,                  0.5f,   "POISON",           0.002f, 15,     0.01f,  EffectType.POISON,      "Giftgas.png",              false,  true,   Ability.NULL,           Upgrade.DEFAULTPROJECTILE),
    EXPLOSION(      Integer.MAX_VALUE,  2.5f,   "EXPLOSION",        0.01f,  50,     0.002f, EffectType.BURNING,     "Explosion.png",            true,   true,   Ability.NULL,           Upgrade.DEFAULTPROJECTILE),
    HEALING(        3,                  0.375f, "HEALING",          4,      0,      3,   EffectType.HEALING,     "Heilung.png",              true,   false,  Ability.NULL,           Upgrade.DEFAULTPROJECTILE);

    public final int maxHP;
    public final String name;
    public final float radius, speed, range;
    public final int impactDamage;
    public final String textureID;
    public final EffectType effectType;
    public final boolean hitHostiles, hitAllies;
    public final Ability ability;
    public final Upgrade upgrade;

    ProjectileType(int maxHP, float radius, String name, float speed, int impactDamage, float range, EffectType effectType, String textureID, boolean hitAllies, boolean hitHostiles, Ability ability, Upgrade upgrade) {
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
        this.upgrade = upgrade;
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
    public Upgrade getUpgrade() {
        return upgrade;
    }

    public enum Ability{
        NULL,
        RANDOMROTATION,
        EXPLOSION,
        POISONCLOUD,
    }
}

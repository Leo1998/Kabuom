package entity.model;

import model.ObjectType;
import model.Upgrade;
import projectile.ProjectileType;

public enum EntityType implements ObjectType {
    //Player    maxHp   radius  speed   name            turretTexture       baseTexture         frequency   range   accur   att aAlly   aHost   projectile                      cost    upgrade
    MGTURRET(   100,    0.5f,   0,      "MG Turret",    "MgTurret.png",     "Base.png",         0.125f,     8,      0.25f,  1,  false,  true,   ProjectileType.BULLET,          100,    Upgrade.DEFAULTENTITY),
    MISSILE(    75,     0.5f,   0,      "Missilelauncher","MissileLauncher.png","Base.png",     2,          14,     0,      1,  false,  true,   ProjectileType.MISSILE,         300,    Upgrade.DEFAULTENTITY),
    FLAME(      60,     0.5f,   0,      "Flamethrower", "Flamethrower.png", "Base.png",         0.125f,     7,      0.5f,   1,  false,  true,   ProjectileType.FLAME,           150,    Upgrade.DEFAULTENTITY),
    POISON(     75,     0.5f,   0,      "Poisontower",  "MgTurretGruen.png","Base.png",         3,          10,     0.1f,   1,  false,  true,   ProjectileType.POISON,          160,    Upgrade.DEFAULTENTITY),
    CYROGUN(    90,     0.5f,   0,      "Cyrogun",      "MgTurret.png",     "Base.png",         2,          10,     1,      3,  false,  true,   ProjectileType.ICE,             260,    Upgrade.DEFAULTENTITY),
    TESLACOIL(  50,     0.325f, 0,      "Teslacoil",    "LaserLamp.png",    "Base.png",         0.03125f,   6,      0.5f,   2,  false,  true,   ProjectileType.LIGHTNING,       400,    Upgrade.DEFAULTENTITY),
    SNIPER(     20,     0.325f, 0,      "Sniper",       "LaserLampRot.png", "Base.png",         3,          16,     0,      1,  false,  true,   ProjectileType.PIERCINGBULLET,  500,    Upgrade.DEFAULTENTITY),
    MORTAR(     40,     0.625f, 0,      "Mortar",       "MissileLauncherGruen.png","Base.png",  4,          20,     0.01f,  1,  false,  true,   ProjectileType.FRAGGRENADE,     750,    Upgrade.DEFAULTENTITY),
    BARRICADE(  1000,   0.625f, 0,      "Barricade",    null,               "barricade2.png",   0.5f,       2,      0,      8,  false,  true,   null,                           75,     Upgrade.DEFAULTENTITY),
    MINE(       10,     0.125f, 0,      "Mine",         null,               "Base.png",         0.125f,     1,      0,      1,  false,  true,   ProjectileType.EXPLOSION,       75,     Upgrade.DEFAULTENTITY),
    MAINTOWER(  1000,   0.625f, 0,      "Maintower",    null,               "MainTower.png",    0,          0,      0,      0,  false,  false,  null,                           250,    Upgrade.DEFAULTENTITY),

    //Enemy     maxHp   radius  speed   name            turretTexture       baseTexture         frequency   range   accur   att aAlly   aHost   projectile                      cost    upgrade
    CHEAP(      30,     0.375f, 2,      "Cheap",        null,               "Cheap.png",        1,          0.375f, 0,      5,  false,  true,   null,                           1,      Upgrade.DEFAULTENTITY),
    TANK(       480,    0.625f, 1.5f,   "Tank",         null,               "Tank.png",         3,          0.625f, 0,      5,  false,  true,   null,                           1,      Upgrade.DEFAULTENTITY),
    SPEED(      75,     0.25f,  3,      "Speed",        null,               "Speed.png",        0.5f,       0.25f,  0,      15, false,  true,   null,                           1,      Upgrade.DEFAULTENTITY),
    DAMAGE(     150,    0.5f,   2,      "Damage",       null,               "Damage.png",       0.3125f,    0.5f,   0,      60, false,  true,   null,                           1,      Upgrade.DEFAULTENTITY),
    SUPER(      480,    0.5f,   1,      "Super",        null,               "Super.png",        1.5f,       0.5f,   0,      70, false,  true,   null,                           1,      Upgrade.DEFAULTENTITY),
    TROLL(      600,    0.75f,  0.5f,   "Troll",        null,               "Troll.png",        3,          0.75f,  0,      75, false,  true,   null,                           1,      Upgrade.DEFAULTENTITY),
    RANGED(     100,    0.325f, 2,      "Mobile MG",    "MgTurret.png",     "Troll.png",        0.5f,       4,      0.5f,   1,  false,  true,   ProjectileType.BULLET,          1,      Upgrade.DEFAULTENTITY),
    RANGED2(    100,    0.325f, 0.5f,   "Mobile Tesla", "LaserLamp.png",    "Tank.png",         0.0625f,    4,      0.5f,   1,  false,  true,   ProjectileType.LIGHTNING,       1,      Upgrade.DEFAULTENTITY),
    SUICIDE(    300,    0.25f,  2,      "Suicide",      null,               "Super.png",        0.03125f,   0.5f,   0,      1,  false,  true,   ProjectileType.EXPLOSION,       1,      Upgrade.DEFAULTENTITY),
    ;
    public final static int firstEnemyIndex = 11;
    public final static int mainTowerIndex = 10;

    public final float maxHP,radius,speed;
    public final String name,turretTexture,baseTexture;
    public final float frequency, range, accuracy;
    public final int attack, cost;
    public final boolean attacksAllies, attacksHostiles;
    public final ObjectType projectile;
    public final Upgrade upgrade;

    EntityType(float maxHP, float radius, float speed, String name, String turretTexture, String baseTexture, float frequency, float range, float accuracy, int attack, boolean attacksAllies, boolean attacksHostiles, ObjectType projectile, int cost, Upgrade upgrade) {
        this.maxHP = maxHP;
        this.radius = radius;
        this.speed = speed;
        this.name = name;
        this.turretTexture = turretTexture;
        this.baseTexture = baseTexture;
        this.frequency = frequency;
        this.range = range;
        this.accuracy = accuracy;
        this.attack = attack;
        this.cost = cost;
        this.attacksAllies = attacksAllies;
        this.attacksHostiles = attacksHostiles;
        this.projectile = projectile;
        this.upgrade = upgrade;
    }

    public boolean isRanged(){
        return projectile != null;
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
}

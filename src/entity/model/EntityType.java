package entity.model;

import model.ObjectType;
import projectile.ProjectileType;

public enum EntityType implements ObjectType {
    //Player    maxHp   radius  speed   name            turretTexture       baseTexture         frequency   range   accur   att aAlly   aHost   hAlly   hHost   projectile                      cost
    MGTURRET(   100,    0.5f,   0,      "MG Turret",    "MgTurret.png",     "Base.png",         0.125f,     8,      0.25f,  1,  false,  true,   false,  true,   ProjectileType.BULLET,          100),
    MISSILE(    75,     0.5f,   0,      "Missilelauncher","MissileLauncher.png","Base.png",     2,          14,     0,      1,  false,  true,   false,  true,   ProjectileType.MISSILE,         300),
    FLAME(      60,     0.5f,   0,      "Flamethrower", "Flamethrower.png", "Base.png",         0.125f,     7,      0.5f,   1,  false,  true,   false,  true,   ProjectileType.FLAME,           150),
    POISON(     75,     0.5f,   0,      "Poisontower",  "MgTurretGruen.png","Base.png",         3,          10,     0.1f,   1,  false,  true,   false,  true,   ProjectileType.POISON,          160),
    CYROGUN(    90,     0.5f,   0,      "Cyrogun",      "MgTurret.png",     "Base.png",         2,          10,     1,      3,  false,  true,   false,  true,   ProjectileType.ICE,             260),
    TESLACOIL(  50,     0.325f, 0,      "Teslacoil",    "LaserLamp.png",    "Base.png",         0.03125f,   6,      0.5f,   2,  false,  true,   false,  true,   ProjectileType.LIGHTNING,       400),
    SNIPER(     20,     0.325f, 0,      "Sniper",       "LaserLampRot.png", "Base.png",         3,          16,     0,      1,  false,  true,   false,  true,   ProjectileType.PIERCINGBULLET,  500),
    MORTAR(     40,     0.625f, 0,      "Mortar",       "MissileLauncherGruen.png","Base.png",  4,          20,     0.01f,  1,  false,  true,   false,  true,   ProjectileType.FRAGGRENADE,     750),
    BARRICADE(  1000,   0.625f, 0,      "Barricade",    null,               "barricade2.png",   0.5f,       2,      1,      8,  false,  true,   false,  true,   null,                           75),
    MINE(       10,     0.125f, 0,      "Mine",         null,               "Base.png",         0.125f,     1,      0,      1,  false,  true,   false,  true,   ProjectileType.EXPLOSION,       75),
    MAINTOWER(  1000,   0.625f, 0,      "Maintower",    null,               "MainTower.png",    0,          0,      0,      0,  false,  false,  false,  false,  null,                           0),

    //Enemy     maxHp   radius  speed   name            turretTexture       baseTexture         frequency   range   accur   att aAlly   aHost   hAlly   hHost   projectile                      cost
    CHEAP(      30,     0.375f, 2,      "Cheap",        null,               "Cheap.png",        1,          0.375f, 1,      5,  false,  true,   false,  true,   null,                           1),
    TANK(       480,    0.625f, 1.5f,   "Tank",         null,               "Tank.png",         3,          0.625f, 1,      5,  false,  true,   false,  true,   null,                           1),
    SPEED(      75,     0.25f,  3,      "Speed",        null,               "Speed.png",        0.5f,       0.25f,  1,      15, false,  true,   false,  true,   null,                           1),
    DAMAGE(     150,    0.5f,   2,      "Damage",       null,               "Damage.png",       0.3125f,    0.5f,   1,      60, false,  true,   false,  true,   null,                           1),
    SUPER(      480,    0.5f,   1,      "Super",        null,               "Super.png",        1.5f,       0.5f,   1,      70, false,  true,   false,  true,   null,                           1),
    TROLL(      600,    0.75f,  0.5f,   "Troll",        null,               "Troll.png",        3,          0.75f,  1,      75, false,  true,   false,  true,   null,                           1),
    RANGED(     100,    0.325f, 2,      "Mobile MG",    "MgTurret.png",     "Troll.png",        0.5f,       4,      0.5f,   1,  false,  true,   false,  true,   ProjectileType.BULLET,          1),
    RANGED2(    100,    0.325f, 0.5f,   "Mobile Tesla", "LaserLamp.png",    "Tank.png",         0.0625f,    4,      0.5f,   1,  false,  true,   false,  true,   ProjectileType.LIGHTNING,       1),
    SUICIDE(    300,    0.25f,  2,      "Suicide",      null,               "Super.png",        0.03125f,   0.5f,   0,      1,  false,  true,   true,   true,   ProjectileType.EXPLOSION,       1),
    ;
    public final static int firstEnemyIndex = 11;
    public final static int mainTowerIndex = 10;

    public final float maxHP,radius,speed;
    public final String name,turretTexture,baseTexture;
    public final float frequency, range, accuracy;
    public final int attack, cost;
    public final boolean attacksAllies, attacksHostiles, hitsAllies, hitsHostiles;
    public final ObjectType projectile;

    EntityType(float maxHP, float radius, float speed, String name, String turretTexture, String baseTexture, float frequency, float range, float accuracy, int attack, boolean attacksAllies, boolean attacksHostiles, boolean hitsAllies, boolean hitsHostiles, ObjectType projectile, int cost) {
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
        this.hitsAllies = hitsAllies;
        this.hitsHostiles = hitsHostiles;
        this.projectile = projectile;
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
}

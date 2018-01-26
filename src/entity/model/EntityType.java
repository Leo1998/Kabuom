package entity.model;

import model.ObjectType;
import projectile.Projectile;
import projectile.ProjectileType;

public enum EntityType implements ObjectType {
    //Player    maxHp   radius  speed   name            textureId           frequency   range   accur   att aAlly   aHost   hAlly   hHost   projectile                      cost
    MGTURRET(   100,    0.5f,   0,      "MG Turret",    "MgTurret.png",     1,          8,      1.5f,   16, false,  true,   false,  true,   ProjectileType.BULLET,          100),
    MISSILE(    75,     0.5f,   0,      "Missilelauncher","MissileLauncher.png",2,      14,     0,      1,  false,  true,   false,  true,   ProjectileType.MISSILE,         300),
    FLAME(      60,     0.5f,   0,      "Flamethrower", "Flamethrower.png",  0.125f,     7,      0.5f,   1,  false,  true,   false,  true,   ProjectileType.FLAME,           150),
    POISON(     75,     0.5f,   0,      "Poisontower",  "MgTurretGruen.png",3,          10,     0.1f,   1,  false,  true,   false,  true,   ProjectileType.POISON,          160),
    CYROGUN(    90,     0.5f,   0,      "Cyrogun",      "MgTurret.png",     2,          10,     2,      3,  false,  true,   false,  true,   ProjectileType.ICE,             260),
    TESLACOIL(  50,     0.325f, 0,      "Teslacoil",    "LaserLamp.png",    0.03125f,   7,      0.5f,   1,  false,  true,   false,  true,   ProjectileType.LIGHTNING,       400),
    SNIPER(     20,     0.325f, 0,      "Sniper",       "LaserLampRot.png", 3,          16,     0,      1,  false,  true,   false,  true,   ProjectileType.PIERCINGBULLET,  500),
    MORTAR(     40,     0.625f, 0,      "Mortar",       "MissileLauncherGruen.png",4,   20,     0.01f,  1,  false,  true,   false,  true,   ProjectileType.FRAGGRENADE,     750),
    BARRICADE(  1000,   0.625f, 0,      "Barricade",    "barricade2.png",   0,          1,      1,      8,  false,  true,   false,  true,   null,                           75),
    MAINTOWER(  100,    1,      0,      "Maintower",    "MainTower.png",    0,          0,      0,      0,  false,  false,  false,  false,  null,                           0),
    //MAINTOWER(  100000, 2,      0,      "OP Tower",     "MainTower.png",    0.03125f,   20,     7,      128,false,  true,   false,  true,   ProjectileType.LIGHTNING        0),

    //Enemy     maxHp   radius  speed   name            textureId           frequency   range   accur   att aAlly   aHost   hAlly   hHost   projectile                      cost
    CHEAP(      30,     0.375f, 2,      "Cheap",        "Cheap.png",        0.3f,       0.375f, 1,      5,  false,  true,   false,  true,   null,                           1),
    TANK(       480,    0.625f, 1.5f,   "Tank",         "Tank.png",         1,          0.625f, 1,      5,  false,  true,   false,  true,   null,                           1),
    SPEED(      75,     0.25f,  3,      "Speed",        "Speed.png",        0.2f,       0.25f,  1,      15, false,  true,   false,  true,   null,                           1),
    DAMAGE(     150,    0.5f,   2,      "Damage",       "Damage.png",       0.1f,       0.5f,   1,      60, false,  true,   false,  true,   null,                           1),
    SUPER(      480,    0.5f,   1,      "Super",        "Super.png",        0.5f,       0.5f,   1,      70, false,  true,   false,  true,   null,                           1),
    TROLL(      600,    0.75f,  0.5f,   "Troll",        "Troll.png",        1,          0.75f,  1,      75, false,  true,   false,  true,   null,                           1),
    ;
    public final static int firstEnemyIndex = 10;
    public final static int mainTowerIndex = 9;

    public final float maxHP,radius,speed;
    public final String name,textureId;
    public final float frequency, range, accuracy;
    public final int attack, cost;
    public final boolean attacksAllies, attacksHostiles, hitsAllies, hitsHostiles;
    public final ObjectType projectile;

    EntityType(float maxHP, float radius, float speed, String name, String textureId, float frequency, float range, float accuracy, int attack, boolean attacksAllies, boolean attacksHostiles, boolean hitsAllies, boolean hitsHostiles, ObjectType projectile, int cost) {
        this.maxHP = maxHP;
        this.radius = radius;
        this.speed = speed;
        this.name = name;
        this.textureId = textureId;
        this.frequency = frequency;
        this.range = range;
        this.accuracy = accuracy;
        this.attack = attack;
        this.attacksAllies = attacksAllies;
        this.attacksHostiles = attacksHostiles;
        this.hitsAllies = hitsAllies;
        this.hitsHostiles = hitsHostiles;
        this.projectile = projectile;
        this.cost = cost;
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
    public String getTextureId() {
        return textureId;
    }
}

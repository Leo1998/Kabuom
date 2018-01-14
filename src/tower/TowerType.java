package tower;

import projectile.ProjectileType;
import model.ObjectType;

public enum TowerType implements ObjectType {

    MGTURRET(100, true,/*Frequency*/ 0.125f,/*AttackRadius : */ 8, 100, ProjectileType.BULLET, "MG Turret", "MgTurret.png",0.5f),
    MISSILELAUNCHER(75, true,/*Frequency*/ 2,/*AttackRadius : */ 14, 300, ProjectileType.MISSILE, "Missilelauncher", "MissileLauncher.png",0.5f),
    FLAMETHROWER(60, true,/*Frequency*/ 0.125f,/*AttackRadius : */ 7, 200, ProjectileType.FLAME, "Flamethrower", "Flamethrower.png",0.5f),
    POISONTOWER(75, true,/*Frequency*/ 3,/*AttackRadius : */ 10, 160, ProjectileType.POISON, "Poisontower", "MgTurretGruen.png",0.5f),
    CYROGUN(90, true,/*Frequency*/ 2,/*AttackRadius : */ 10, 275, ProjectileType.ICE, "Cyrogun", "MgTurret.png",0.5f),
    TESLACOIL(50, true,/*Frequency*/ 0.0625f,/*AttackRadius : */ 4, 700, ProjectileType.LIGHTNING, "Teslacoil", "LaserLamp.png",0.325f),
    SNIPER(20, true,/*Frequency*/ 3,/*AttackRadius : */ 16, 250, ProjectileType.PIERCINGBULLET, "Sniper", "LaserLampRot.png",0.325f),
    MORTAR(40, true,/*Frequency*/ 4,/*AttackRadius : */ 20, 500, ProjectileType.FRAGGRENADE, "Mortar", "MissileLauncherGruen.png",0.625f),
    BARRICADE(500, false,/*Frequency*/ 0,/*AttackRadius : */ 0, 50, null, "Barricade", "barricade2.png",0.625f),
    //MAINTOWER(100000, true,/*Frequency*/ 0.03125f,/*AttackRadius : */ 20, 0, ProjectileType.FRAGGRENADE, "OP Tower", "MainTower.png",2);
    MAINTOWER(1000, false,/*Frequency*/ 0,/*AttackRadius : */ 0, 0, null, "Maintower", "MainTower.png",0.5f);


    public int maxHP, attackRadius, cost;
    public float frequency, radius;
    public ProjectileType projectileType;
    public String name;
    public final String textureID;
    public boolean canShoot;

    TowerType(int maxHP, boolean canShoot, float frequency, int attackRadius, int cost, ProjectileType proType, String name, String textureID, float radius) {
        this.maxHP = maxHP;
        this.canShoot = canShoot;
        projectileType = proType;
        this.frequency = frequency;
        this.attackRadius = attackRadius;
        this.cost = cost;
        this.name = name;
        this.textureID = textureID;
        this.radius = radius;
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
        return textureID;
    }
}

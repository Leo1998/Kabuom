package tower;

import projectile.ProjectileType;
import model.ObjectType;

public enum TowerType implements ObjectType {

    MGTURRET(100, true,/*Frequency*/ 1,/*AttackRadius : */ 8, 100, ProjectileType.BULLET, "SHOTGUN", "MgTurret.png",0.5f, 1.5f, 16),
    MISSILELAUNCHER(75, true,/*Frequency*/ 2,/*AttackRadius : */ 14, 300, ProjectileType.MISSILE, "Missilelauncher", "MissileLauncher.png",0.5f, 0, 1),
    FLAMETHROWER(60, true,/*Frequency*/ 0.125f,/*AttackRadius : */ 7, 200, ProjectileType.FLAME, "Flamethrower", "Flamethrower.png",0.5f, 0.5f, 1),
    POISONTOWER(75, true,/*Frequency*/ 3,/*AttackRadius : */ 10, 160, ProjectileType.POISON, "Poisontower", "MgTurretGruen.png",0.5f, 0.01f, 1),
    CYROGUN(90, true,/*Frequency*/ 2,/*AttackRadius : */ 10, 275, ProjectileType.ICE, "Cyrogun", "MgTurret.png",0.5f, 2, 3),
    TESLACOIL(50, true,/*Frequency*/ 0.03125f,/*AttackRadius : */ 7, 250, ProjectileType.LIGHTNING, "Teslacoil", "LaserLamp.png",0.325f, 0.5f, 1),
    SNIPER(20, true,/*Frequency*/ 3,/*AttackRadius : */ 16, 250, ProjectileType.PIERCINGBULLET, "Sniper", "LaserLampRot.png",0.325f, 0, 1),
    MORTAR(40, true,/*Frequency*/ 4,/*AttackRadius : */ 20, 500, ProjectileType.FRAGGRENADE, "Mortar", "MissileLauncherGruen.png",0.625f, 0.01f, 1),
    BARRICADE(500, false,/*Frequency*/ 0,/*AttackRadius : */ 0, 50, null, "Barricade", "barricade2.png",0.625f, 0, 0),
    //MAINTOWER(100000, true,/*Frequency*/ 0.03125f,/*AttackRadius : */ 20, 0, ProjectileType.MISSILE, "OP Tower", "MainTower.png",2, 2, 128);
    MAINTOWER(1000, false,/*Frequency*/ 0,/*AttackRadius : */ 0, 0, null, "Maintower", "MainTower.png",0.5f, 0, 0);


    public final int maxHP, attackRadius, cost, shots;
    public final float frequency, radius, accuracy;
    public final ProjectileType projectileType;
    public final String name;
    public final String textureID;
    public final boolean canShoot;

    TowerType(int maxHP, boolean canShoot, float frequency, int attackRadius, int cost, ProjectileType proType, String name, String textureID, float radius, float accuracy, int shots) {
        this.maxHP = maxHP;
        this.canShoot = canShoot;
        projectileType = proType;
        this.frequency = frequency;
        this.attackRadius = attackRadius;
        this.cost = cost;
        this.name = name;
        this.textureID = textureID;
        this.radius = radius;
        this.accuracy = accuracy;
        this.shots = shots;
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


    public float getFrequency() {
        return frequency;
    }


    public float getAimRadius() {
        return attackRadius;
    }


    public float getAccuracy() {
        return accuracy;
    }


    public int getShots() {
        return shots;
    }


    public ProjectileType getProjectileType() {
        return projectileType;
    }
}

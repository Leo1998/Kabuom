package tower;

import projectile.ProjectileType;

public enum TowerType {

    MGTURRET(100, true,/*Frequency*/ 0.15f,/*AttackRadius : */ 8, 100, ProjectileType.BULLET, "MGTurret", "MgTurret.png"),
    MISSILELAUNCHER(75, true,/*Frequency*/ 2,/*AttackRadius : */ 14, 300, ProjectileType.MISSILE, "Missilelauncher", "MissileLauncher.png"),
    FLAMETHROWER(60, true,/*Frequency*/ 0.05f,/*AttackRadius : */ 7, 200, ProjectileType.FLAME, "Flamethrower", "Flamethrower.png"),
    POISONTOWER(75, true,/*Frequency*/ 3,/*AttackRadius : */ 10, 160, ProjectileType.POISON, "Poisontower", "MgTurretGruen.png"),
    CYROGUN(90, true,/*Frequency*/ 2,/*AttackRadius : */ 10, 275, ProjectileType.ICE, "Cyrogun", "MgTurret.png"),
    TESLACOIL(50, true,/*Frequency*/ 0.01f,/*AttackRadius : */ 4, 700, ProjectileType.LIGHTNING, "Teslacoil", "LaserLamp.png"),
    SNIPER(20, true,/*Frequency*/ 3,/*AttackRadius : */ 16, 250, ProjectileType.PIERCINGBULLET, "Sniper", "LaserLampRot.png"),
    MORTAR(40, true,/*Frequency*/ 4f,/*AttackRadius : */ 20, 500, ProjectileType.FRAGGRENADE, "Mortar", "MissileLauncherGruen.png"),
    BARRICADE(500, false,/*Frequency*/ 0,/*AttackRadius : */ 0, 50, ProjectileType.BULLET, "Barricade", "barricade2.png"),
    MAINTOWER(1000, false,/*Frequency*/ 0,/*AttackRadius : */ 0, 0, ProjectileType.BULLET, "Maintower", "MainTower.png");


    private int HP, attackRadius, cost;
    private float frequency;
    private ProjectileType projectileType;
    private String name;
    private final String textureID;
    private boolean canShoot;

    TowerType(int hp, boolean canShoot, float frequency, int attackRadius, int cost, ProjectileType proType, String name, String textureID) {
        HP = hp;
        this.canShoot = canShoot;
        projectileType = proType;
        this.frequency = frequency;
        this.attackRadius = attackRadius;
        this.cost = cost;
        this.name = name;
        this.textureID = textureID;
    }

    public String getName() {
        return name;
    }

    public int getHP() {
        return HP;
    }

    public int getAttackRadius() {
        return attackRadius;
    }

    public int getCost() {
        return cost;
    }

    public float getFrequency() {
        return frequency;
    }

    public ProjectileType getProjectileType() {
        return projectileType;
    }

    public String getTextureID() {
        return textureID;
    }

    public boolean canShoot() {
        return this.canShoot;
    }
}

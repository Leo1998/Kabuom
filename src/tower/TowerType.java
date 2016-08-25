package tower;

import projectile.ProjectileType;

public enum TowerType{

    MGTURRET(100, true, 0.1f,250,100,ProjectileType.BULLET, "MGTurret", "MgTurret.png"),
    MISSILELAUNCHER(75,true, 2,400,200,ProjectileType.MISSILE, "Missilelauncher", "MissileLauncher.png"),
    FLAMETHROWER(60, true, 0.001f,300,300,ProjectileType.FLAME, "Flamethrower", "Flamethrower.png"),
    POISONTOWER(75, true,3,200,175,ProjectileType.POISON, "Poisontower", "MgTurret.png"),
    CYROGUN(90, true,5,200,350,ProjectileType.ICE, "Cyrogun", "MgTurret.png"),
    TESLACOIL(50, true, 0.001f,150,500,ProjectileType.LIGHTNING, "Teslacoil", "MgTurret.png"),
    SNIPER(20, true, 4,2000,250,ProjectileType.PIERCINGBULLET, "Sniper", "MgTurret.png"),
    MORTAR(40, true, 6,1500,750,ProjectileType.FRAGGRENADE,"Mortar", "MgTurret.png"),
    BARRICADE(250, false, 0,0,50,ProjectileType.BULLET, "Barricade", "MgTurret.png"),
    MAINTOWER(1000, false, 0,0,0,ProjectileType.BULLET,"Maintower", "MgTurret.png");

    private int HP,attackRadius,cost;
    private float frequency;
    private ProjectileType projectileType;
    private String name;
    private final String textureID;
    private boolean canShoot;

    TowerType(int hp, boolean canShoot, float frequency, int attackRadius, int cost, ProjectileType proType, String name, String textureID){
        HP = hp;
        this.canShoot = canShoot;
        projectileType=proType;
        this.frequency = frequency;
        this.attackRadius = attackRadius;
        this.cost=cost;
        this.name = name;
        this.textureID = textureID;
    }

    public String getName(){ return name; }

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

    public boolean canShoot(){
        return this.canShoot;
    }
}

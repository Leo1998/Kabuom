package tower;

import projectile.ProjectileType;

public enum TowerType{

    MGTURRET(100, 0.1f,250,100,ProjectileType.BULLET, "MGTurret", "MgTurret.png"),
    MISSILELAUNCHER(75,2,400,200,ProjectileType.MISSILE, "Missilelauncher", "MissileLauncher.png"),
    FLAMETHROWER(60,0.001f,200,300,ProjectileType.FLAME, "Flamethrower", "Flamethrower.png"),
    BARRICADE(250,0,0,50,ProjectileType.BULLET, "Barricade", "MgTurret.png"),
    MAINTOWER(1000,0,0,0,ProjectileType.BULLET,"Maintower", "MgTurret.png"),
    POISONTOWER(75,3,200,175,ProjectileType.POISON, "Poisontower", "MgTurret.png"),
    CYROGUN(90,5,200,350,ProjectileType.ICE, "Cyrogun", "MgTurret.png"),
    TESLACOIL(50,0.001f,150,500,ProjectileType.LIGHTNING, "Teslacoil", "MgTurret.png"),
    SNIPER(20,4,2000,250,ProjectileType.PIERCINGBULLET, "Sniper", "MgTurret.png"),
    MORTAR(40,6,1500,750,ProjectileType.FRAGGRENADE,"Mortar", "MgTurret.png");

    private int HP,attackRadius,cost;
    private float frequency;
    private ProjectileType projectileType;
    private String name;
    private final String textureID;

    TowerType(int hp, float frequency, int attackRadius, int cost, ProjectileType proType, String name, String textureID){
        HP = hp;
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
}

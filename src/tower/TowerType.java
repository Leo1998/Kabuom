package tower;

import projectile.ProjectileType;

public enum TowerType{

    MGTURRET(100, 0.1f,250,100,ProjectileType.BULLET, "MGTurret"),
    MISSILELAUNCHER(75,2,400,200,ProjectileType.MISSILE, "Missilelauncher"),
    POISONTOWER(75,3,200,175,ProjectileType.POISON, "Poisontower"),
    CYROGUN(90,5,200,350,ProjectileType.ICE, "Cyrogun"),
    TESLACOIL(50,0.001f,150,500,ProjectileType.LIGHTNING, "Teslacoil"),
    FLAMETHROWER(60,0.001f,200,300,ProjectileType.FLAME, "Flamethrower"),
    SNIPER(20,4,2000,250,ProjectileType.PIERCINGBULLET, "Sniper"),
    MORTAR(40,6,1500,750,ProjectileType.FRAGGRENADE,"Mortar"),
    BARRICADE(250,0,0,50,ProjectileType.BULLET, "Barricade"),
    MAINTOWER(1000,0,0,0,ProjectileType.BULLET,"Maintower"),
    DUMMY(0,0,0,0,ProjectileType.BULLET,"Dummy");

    private int HP,attackRadius,cost;
    private float frequency;
    private ProjectileType projectileType;
    private String name;

    TowerType(int hp, float frequency, int attackRadius, int cost, ProjectileType proType, String name){
        HP = hp;
        projectileType=proType;
        this.frequency = frequency;
        this.attackRadius = attackRadius;
        this.cost=cost;
        this.name = name;
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
}

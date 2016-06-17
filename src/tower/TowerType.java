package tower;

import projectile.ProjectileType;

public enum TowerType{

    MGTURRET(100, 0.1f,250,100,ProjectileType.BULLET),
    MISSILELAUNCHER(75,2,400,200,ProjectileType.MISSILE),
    POISONTOWER(75,3,200,175,ProjectileType.POISON),
    CYROGUN(90,5,200,350,ProjectileType.ICE),
    TESLACOIL(50,0.001f,150,500,ProjectileType.LIGHTNING),
    FLAMETHROWER(60,0.001f,200,300,ProjectileType.FLAME),
    SNIPER(20,4,2000,250,ProjectileType.PIERCINGBULLET),
    MORTAR(40,6,1500,750,ProjectileType.FRAGGRANADE),
    BARRICADE(250,0,0,50,null),
    MAINTOWER(1000,0,0,0,null),
    DUMMY(0,0,0,0,null);

    int HP,attackRadius,cost;
    float frequency;
    ProjectileType projectileType;

    TowerType(int Hp, float frequency, int attackRadius, int cost, ProjectileType proType){
        HP = Hp;
        projectileType=proType;
        this.frequency = frequency;
        this.attackRadius = attackRadius;
        this.cost=cost;
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
}

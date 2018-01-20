package entity.model;

import model.ObjectType;
import projectile.ProjectileType;

public enum EntityType implements ObjectType {
    ;

    public final float maxHP,radius,speed;
    public final String name,textureId;
    public final float frequency, range, accuracy;
    public final int attack;
    public final boolean attacksAllies, attacksHostiles, hitsAllies, hitsHostiles;
    public final ProjectileType projectileType;

    EntityType(float maxHP, float radius, float speed, String name, String textureId, float frequency, float range, float accuracy, int attack, boolean attacksAllies, boolean attacksHostiles, boolean hitsAllies, boolean hitsHostiles, ProjectileType projectileType) {
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
        this.projectileType = projectileType;
    }

    public boolean isRanged(){
        return projectileType != null;
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

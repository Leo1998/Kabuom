package projectile;

import entity.model.EffectType;
import entity.model.Entity;
import entity.model.EntityType;
import entity.model.Partisan;
import model.GameObject;
import model.ObjectType;
import utility.Vector2;

import java.util.ArrayList;

public class Projectile extends GameObject implements Partisan {
    private final ProjectileType projectileType;
    private float distance;
    private Vector2 dir;
    private ArrayList<Entity> hitEntities; // Zur Lösung von Kollisionsproblemen beim durchdringen von Gegnern.
    private boolean isEnemy;
    public final String source;

    public Projectile(ProjectileType projectileType, int level, float x, float y, Vector2 dir, boolean isEnemy, String source) {
        super(projectileType, level, x, y);
        this.projectileType = projectileType;
        this.dir = dir;
        this.hitEntities = new ArrayList<>();
        this.isEnemy = isEnemy;
        this.source = source;
    }

    public Vector2 getDir() {
        return dir;
    }

    /**
     * Die Anfrage liefert die Distanz zum Ziel des Projektils als float.
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Die Methode setzt die Distanz zum Ziel des Projektils als float.
     */
    public void setDistance(float pDistance) {
        distance = pDistance;
    }

    /**
     * Die Anfrage liefert die bereits getroffenen Gegner des Projektils als ArrayList.
     */
    public boolean hasHitEntity(Entity entity) {
        return hitEntities.contains(entity);
    }

    /**
     * Die Methode fügt die bereits getroffenen Gegner des Projektils in eine ArrayList.
     */
    public void addToHitEntities(Entity entity) {
        hitEntities.add(entity);
    }

    /**
     * Getter for Enum
     */

    public float getSpeed(){
        return projectileType.speed * getUpgrade(1);
    }

    public int getDamage(){
        return Math.round(projectileType.impactDamage * getUpgrade(2));
    }

    public float getRange(){
        return projectileType.range * getUpgrade(3);
    }

    public EffectType getEffect(){
        return projectileType.effectType;
    }

    public String getTexture(){
        return projectileType.textureID;
    }

    public boolean hitsAllies(){
        return projectileType.hitAllies;
    }

    public boolean hitsHostiles(){
        return projectileType.hitHostiles;
    }

    public ProjectileType.Ability getAbility(){
        return projectileType.ability;
    }

    @Override
    protected ObjectType getObjectType() {
        return projectileType;
    }

    public boolean hits(Partisan partisan){
        if(allyOf(partisan)){
            return projectileType.hitAllies;
        } else {
            return projectileType.hitHostiles;
        }
    }

    @Override
    public boolean isEnemy() {
        return isEnemy;
    }
}

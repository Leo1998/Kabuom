package projectile;

import entity.model.Entity;
import entity.model.Partisan;
import model.GameObject;
import model.ObjectType;
import utility.Vector2;

import java.util.ArrayList;

public class Projectile extends GameObject {
    public final ProjectileType projectileType;
    private float distance;
    private Vector2 dir;
    private ArrayList<Entity> hitEntities; // Zur Lösung von Kollisionsproblemen beim durchdringen von Gegnern.

    public Projectile(ProjectileType projectileType, int level, float x, float y, Vector2 dir) {
        super(projectileType, level, x, y);
        this.projectileType = projectileType;
        this.dir = dir;
        this.hitEntities = new ArrayList<>();
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

    @Override
    public ObjectType getObjectType() {
        return projectileType;
    }

    public boolean hits(Partisan partisan){
        if(partisan.isEnemy()){
            return projectileType.hitEnemy;
        } else {
            return projectileType.hitPlayer;
        }
    }
}

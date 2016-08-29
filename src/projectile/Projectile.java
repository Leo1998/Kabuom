package projectile;

import enemy.Enemy;
import model.GameObject;
import utility.Vector2;

import java.util.ArrayList;

public class Projectile extends GameObject{
    private ProjectileType projectileType;
    private int impactDamage;
    private float distance,speed,range;
    private Vector2 dir;
    private ArrayList<Enemy> hitEnemies; // Zur Lösung von Kollisionsproblemen beim durchdringen von Gegnern.

    public Projectile(ProjectileType projectileType, int level, float x, float y, Vector2 dir) {
        super(projectileType.getMaxHP(), level, projectileType.getName(), x, y,projectileType.getRadius(),projectileType.getTextureID());
        this.projectileType = projectileType;
        impactDamage = projectileType.getImpactDamage();
        speed = projectileType.getSpeed();
        range = projectileType.getRange();
        this.dir = dir;
        this.hitEnemies = new ArrayList<>();
    }

    /**
     * Die Anfrage liefert die Schaden des Projektils als Integer.
     */
    public int getImpactDamage(){
        return impactDamage;
    }

    /**
     * Die Anfrage liefert die Geschwindigkeit des Projektils als float.
     */
    public float getSpeed(){
        return speed;
    }

    /**
     * Die Anfrage liefert die Reichweite des Projektils als float.
     */
    public float getRange(){
        return range;
    }

    public Vector2 getDir(){
        return dir;
    }


    /**
     * Die Anfrage liefert die Distanz zum Ziel des Projektils als float.
     */
    public float getDistance(){
        return distance;
    }

    /**
     * Die Methode setzt die Distanz zum Ziel des Projektils als float.
     */
    public void setDistance(float pDistance){
        distance = pDistance;
    }

    /**
     * Die Anfrage liefert die Projektilart des Projektils als ProjectileType.
     */
    public ProjectileType getProjectileType(){
        return projectileType;
    }

    /**
     * Die Anfrage liefert die bereits getroffenen Gegner des Projektils als ArrayList.
     */
    public ArrayList<Enemy> getHitEnemies(){
        return hitEnemies;
    }

    /**
     * Die Methode fügt die bereits getroffenen Gegner des Projektils in eine ArrayList.
     */
    public void addToHitEnemies(Enemy pEnemy){
        hitEnemies.add(pEnemy);
    }
}

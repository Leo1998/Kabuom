package projectile;

import entity.utility.EffectType;
import entity.model.Entity;
import entity.utility.Partisan;
import model.BinarySearchTree;
import model.GameObject;
import model.ObjectType;
import view.math.Vector2;

public class Projectile extends GameObject implements Partisan {
    private final ProjectileType projectileType;
    private float distance;
    private Vector2 dir;
    private BinarySearchTree<Entity> hitEntities; // Zur Lösung von Kollisionsproblemen beim durchdringen von Gegnern.
    private boolean isEnemy;
    public final String source;

    public Projectile(ProjectileType projectileType, int level, float x, float y, Vector2 dir, boolean isEnemy, String source) {
        super(projectileType, level, x, y);
        this.projectileType = projectileType;
        this.dir = dir;
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
     * Die Methode fügt die bereits getroffenen Gegner des Projektils in einen Binären Suchbaum.
     * @return Gibt zurück, ob sich der Gegner bereits im Baum befand
     */
    public boolean addToHitEntities(Entity entity) {
        if(hitEntities == null){
            hitEntities = new BinarySearchTree<>(entity);
            return false;
        } else {
            return hitEntities.add(entity);
        }
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

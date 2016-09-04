package projectile;

import tower.TowerType;

public enum ProjectileType {
    

    BULLET (1,0.05f,"BULLET",25f,/*Damage*/8, /*Range*/  8, "projektil.png"),
    MISSILE (1,0.1f,"MISSILE",15,/*Damage*/200, /*Range*/14, "missileKlein.png"),
    FLAME (3,0.1f,"FLAME",5,/*Damage*/ 8, /*Range*/ 7, "flame.png"),
    ICE (1,0.1f,"ICE",5,/*Damage*/ 200, /*Range*/ 10, "IceBullet.png"),
    LIGHTNING (5,0.01f,"LIGHTNING", 15,/*Damage*/3, /*Range*/ 12, "Laser.png"),
    PIERCINGBULLET (3,0.05f,"PIERCINGBULLET",27,/*Damage*/150, /*Range*/30, "Piercingprojektil.png"),
    FRAGGRENADE (1,0.1f,"FRAGGRENADE",30,/*Damage*/470, /*Range*/ 20, "missile.png"),
    POISON (1,0.05f,"POISON",5,/*Damage*/25,/*Range*/ 10, "toxicBullet.png"),
    POISONTRAIL(1,0.05f,"POISON",0.002f,/*Damage*/15,/*Range*/ 0.01f, "Giftgas.png");

    private final int maxHP;
    private final String name;
    private final float radius;
    private final float speed;
    private final int impactDamage;
    private final float range;
    private final String textureID;

    ProjectileType( int maxHP, float radius, String name, float speed, int impactDamage, float range, String textureID) {
        this.maxHP = maxHP;
        this.radius = radius;
        this.name = name;
        this.speed = speed;
        this.impactDamage = impactDamage;
        this.range = range;
        this.textureID = textureID;
    }

    /**
     * Die Anfrage liefert die Lebenspunkte des Projektils als Integer.
     */
    public int getMaxHP() {
        return maxHP;
    }

    /**
     * Die Anfrage liefert den Namen des Projektils als String.
     */
    public String getName() {
        return name;
    }

    /**
     * Die Anfrage liefert den Radius des Projektils als float.
     */
    public float getRadius() {
        return radius;
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

    public String getTextureID() {
        return textureID;
    }


}

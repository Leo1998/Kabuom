package projectile;

public enum ProjectileType {
    
    BULLET (1,0.05f,"BULLET",5.5f,/*Damage*/90,10, "projektil.png"),
    MISSILE (1,0.1f,"MISSILE",2,/*Damage*/200,50, "missileKlein.png"),
    FLAME (1,0.1f,"FLAME",3,/*Damage*/ 7,50, "flame.png"),
    ICE (1,0.1f,"ICE",3,/*Damage*/ 50,50, "test1.png"),
    LIGHTNING (5,0.05f,"LIGHTNING",6,/*Damage*/1,50, "test1.png"),
    PIERCINGBULLET (3,0.05f,"PIERCINGBULLET",7,/*Damage*/150,50, "Piercingprojektil.png"),
    FRAGGRENADE (1,0.1f,"FRAGGRENADE",7,/*Damage*/450,50, "missile.png"),
    POISON (1,0.05f,"POISON",4,/*Damage*/50,50, "test1.png");

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

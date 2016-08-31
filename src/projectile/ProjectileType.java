package projectile;

public enum ProjectileType {

    BULLET (1,0.5f,"BULLET",5,50,10, "test1.png"),
    MISSILE (1,0.5f,"MISSILE",2,50,50, "test1.png"),
    FLAME (1,0.5f,"FLAME",3,2,50, "test1.png"),
    ICE (1,0.5f,"ICE",3,50,50, "test1.png"),
    LIGHTNING (5,0.5f,"LIGHTNING",6,50,50, "test1.png"),
    PIERCINGBULLET (3,0.5f,"PIERCINGBULLET",7,50,50, "test1.png"),
    FRAGGRENADE (1,0.5f,"FRAGGRENADE",50,50,50, "test1.png"),
    POISON (1,0.5f,"POISON",50,50,50, "test1.png");

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

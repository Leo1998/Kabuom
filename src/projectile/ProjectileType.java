package projectile;

public enum ProjectileType {

    BULLET (1,50,"BULLET",150,50,500, "test1.png"),
    MISSILE (1,50,"MISSILE",2,50,50, "test1.png"),
    FLAME (1,50,"FLAME",3,50,50, "test1.png"),
    ICE (1,50,"ICE",3,50,50, "test1.png"),
    LIGHTNING (5,50,"LIGHTNING",6,50,50, "test1.png"),
    PIERCINGBULLET (3,50,"PIERCINGBULLET",7,50,50, "test1.png"),
    FRAGGRENADE (1,50,"FRAGGRENADE",50,50,50, "test1.png"),
    POISON (1,50,"POISON",50,50,50, "test1.png");

    private final int maxHP;
    private final String name;
    private final float radius;
    private final float speed;
    private final int impactDamage;
    private final float range;
    private final String textureID;

    ProjectileType( int maxHP, int radius, String name, float speed, int impactDamage, float range, String textureID) {
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

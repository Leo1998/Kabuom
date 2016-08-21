package projectile;

public enum ProjectileType {

    BULLET (1,50,"BULLET",4,50,50),
    MISSILE (1,50,"MISSILE",2,50,50),
    FLAME (1,50,"FLAME",3,50,50),
    ICE (1,50,"ICE",3,50,50),
    LIGHTNING (5,50,"LIGHTNING",6,50,50),
    PIERCINGBULLET (3,50,"PIERCINGBULLET",7,50,50),
    FRAGGRENADE (1,50,"FRAGGRENADE",50,50,50),
    POISON (1,50,"POISON",50,50,50);

    private final int maxHP;
    private final String name;
    private final float radius;
    private final float speed;
    private final int impactDamage;
    private final float range;

    ProjectileType( int maxHP, int radius, String name, float speed, int impactDamage, float range) {
        this.maxHP = maxHP;
        this.radius = radius;
        this.name = name;
        this.speed = speed;
        this.impactDamage = impactDamage;
        this.range = range;
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




}

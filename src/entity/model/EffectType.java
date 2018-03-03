package entity.model;

/**
 * Created by Daniel on 30-Dec-17.
 */
public enum EffectType {
    //          dur     strength    dot
    SLOW(       4,      2,          false),
    BLEEDING(   0.5f,   2,          false),
    BURNING(    2,      0.25f,      true),
    POISON(     5,      0.125f,     true),
    HEALING(    1,      -0.03125f,  true);

    public final float duration, strength;
    public final boolean dot;

    EffectType(float duration, float strength, boolean dot) {
        this.duration = duration;
        this.strength = strength;
        this.dot = dot;
    }
}

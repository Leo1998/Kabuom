package enemy;

/**
 * Created by Daniel on 30-Dec-17.
 */
public enum EffectType {
    SLOW(0, 4, 4),
    BLEEDING(1, 0.5f, 2),
    BURNING(2, 2, 0.25f),
    POISON(3, 5, 0.125f);

    public final float duration, strength;
    public final int id;

    EffectType(int id, float duration, float strength) {
        this.id = id;
        this.duration = duration;
        this.strength = strength;
    }
}

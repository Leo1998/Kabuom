package enemy.effect;

/**
 * Created by Daniel on 30-Dec-17.
 */
public enum EffectType {
    SLOW(4, 4),
    BLEEDING(0.5f, 2),
    BURNING(2, 0.25f),
    POISON(5, 0.125f);

    public final float duration, strength;

    EffectType(float duration, float strength) {
        this.duration = duration;
        this.strength = strength;
    }
}

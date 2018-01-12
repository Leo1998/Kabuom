package enemy.effect;

/**
 * Created by Daniel on 30-Dec-17.
 */
public enum EffectType {
    SLOW(4, 4),
    BLEEDING(0.5f, 2),
    BURNING(4, 2),
    POISON(10, 0.5f);

    public final float duration, strength;

    EffectType(float duration, float strength) {
        this.duration = duration;
        this.strength = strength;
    }
}

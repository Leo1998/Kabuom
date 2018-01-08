package enemy.effect;

/**
 * Created by Daniel on 30-Dec-17.
 */
public enum EffectType {
    Slow(4.0f, 4.0f),
    Bleeding(0.5f, 2.0f),
    Burning(4.0f, 2.0f);

    public final float duration, strength;

    EffectType(float duration, float strength) {
        this.duration = duration;
        this.strength = strength;
    }
}

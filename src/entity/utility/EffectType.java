package entity.utility;

/**
 * Created by Daniel on 30-Dec-17.
 */
public enum EffectType {
    //          dur     dot,        damage,     speed
    SLOW(       4,      0,          0,          -0.5f),
    BLEEDING(   0.5f,   0,          1),
    BURNING(    2,      -0.25f),
    POISON(     5,      -0.125f),
    HEALING(    1,      0.125f);

    public final float duration;
    public final float[] buffs;

    EffectType(float duration, float... buffs) {
        this.duration = duration;
        if(buffs.length <= BuffType.values().length) {
            this.buffs = buffs;
        } else {
            this.buffs = new float[BuffType.values().length];
            java.lang.System.arraycopy(buffs,0, this.buffs, 0, this.buffs.length);
        }
    }

    public enum BuffType {
        DOT,DAMAGE,SPEED,
    }
}

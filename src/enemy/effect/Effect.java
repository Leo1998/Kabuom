package enemy.effect;

import utility.Constants;

/**
 * Created by Daniel on 30-Dec-17.
 */
public class Effect{
    private float duration;
    public final EffectType effectType;

    public Effect(EffectType effectType) {
        this.effectType = effectType;
        this.duration = effectType.duration;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void addDuration(float duration) {
        this.duration += duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Effect effect = (Effect) o;

        return effectType == effect.effectType;

    }

    public boolean breaks(EffectType effectType){
        if(Constants.fireBreaksSlow) {
            switch (this.effectType) {
                case BURNING:
                    return effectType == EffectType.SLOW;
                case SLOW:
                    return effectType == EffectType.BURNING;
                default:
                    return false;
            }
        }else {
            return false;
        }
    }
}

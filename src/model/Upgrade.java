package model;

import java.util.function.Function;

public class Upgrade {
    public final static Upgrade DEFAULTENTITY = new Upgrade(new float[]{4,4,4,4,4,4,4}, new float[]{0.1f,0.1f,0.1f,0.1f,0.1f,0.1f,0.1f});
    public final static Upgrade DEFAULTPROJECTILE = new Upgrade(new float[]{4,4,4,4}, new float[]{0.1f,0.1f,0.1f,0.1f});

    private final Function<Integer, Float>[] functions;

    public Upgrade(float[] max, float[] growth){
        if(max.length == growth.length){
            functions = new Function[max.length];

            for(int i = 0; i < functions.length; i++){
                float m = max[i];
                float g = growth[i];
                functions[i] = (Integer x) -> (1-m)/((float)Math.sqrt(1 + x*g)) + m;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Upgrade(Function<Integer, Float>... functions){
        this.functions = functions;
    }

    public float getStrength(int level, int index){
        if(index < functions.length) {
            return functions[index].apply(level);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

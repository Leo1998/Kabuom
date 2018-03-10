package model;

import java.util.function.Function;

public class Upgrade {
    //Entity: Hp Speed Frequency Range Accuracy Attack Cost
    //Projectile: Hp Speed Damage Range

    public final static Upgrade DEFAULTENTITY = new Upgrade(x -> x*0.1f+1,
                                                            x -> def(x,0.1f, 4f),
                                                            x -> def(x,0.1f, 4f),
                                                            x -> def(x,0.1f, 4f),
                                                            x -> def(x,0.1f, 4f),
                                                            x -> def(x,0.1f, 4f),
                                                            x -> def(x,0.1f, 4f));
    public final static Upgrade DEFAULTPROJECTILE = new Upgrade(new float[]{2,2,2,2}, new float[]{0.08f,0.08f,0.08f,0.08f});
    public final static Upgrade RANGEDENTITY = new Upgrade( x -> x*0.1f+1,
                                                            x -> def(x,0.1f, 4f),
                                                            x -> def(x,0.025f, 2f),
                                                            x -> def(x,0.025f, 2f),
                                                            x -> def(x,0.001f, 1.5f),
                                                            x -> def(x,0.025f, 2f),
                                                            x -> def(x,0.1f, 4f));
    public final static Upgrade EXPLOSIONPROJECTILE = new Upgrade(new float[]{3,1,3,1}, new float[]{0.1f,0,0.1f,0});


    private final Function<Integer, Float>[] functions;

    private static float def(float x, float g, float m){
        return (1-m)/((float)Math.sqrt(1+g*x))+m;
    }

    public Upgrade(float[] max, float[] growth){
        if(max.length == growth.length){
            functions = new Function[max.length];

            for(int i = 0; i < functions.length; i++){
                float m = max[i];
                float g = growth[i];
                functions[i] = (Integer x) -> def(x,g,m);
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

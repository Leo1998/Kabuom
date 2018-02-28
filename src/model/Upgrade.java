package model;

public class Upgrade {
    public final static Upgrade DEFAULTENTITY = new Upgrade(new float[][]{{0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f},{3, 3, 3, 3, 3, 3, 3}});
    public final static Upgrade DEFAULTPROJECTILE = new Upgrade(new float[][]{{0.2f, 0.2f, 0.2f, 0.2f},{3, 3, 3, 3}});


    private float[][] upgrades;

    public Upgrade(float[]... upgrades){
        this.upgrades = upgrades;
    }

    public float getStrength(int level, int index){
        float strength = 1;
        int length = upgrades.length - 1;
        int full = level / length;
        int remainder = level % length;

        for(int i = 0; i < length; i++){
            strength += upgrades[i][index] * full;
            if(remainder < i){
                strength += upgrades[i][index];
            }
        }

        float boundry = upgrades[length][index];

        strength = (float) Math.sqrt(strength);
        strength = -boundry/strength+boundry+1;

        return strength;
    }
}

package model;

public class UpgradeOrder {
    public final static UpgradeOrder DEFAULTENTITY = new UpgradeOrder(new float[]{0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f});
    public final static UpgradeOrder DEFAULTPROJECTILE = new UpgradeOrder(new float[]{0.2f, 0.2f, 0.2f, 0.2f});

    private float[][] upgrades;

    public UpgradeOrder(float[]... upgrades){
        this.upgrades = upgrades;
    }

    public float getStrength(int level, int index){
        float strength = 1;
        int full = level / upgrades.length;
        int remainder = level % upgrades.length;

        for(int i = 0; i < upgrades.length; i++){
            strength += upgrades[i][index] * full;
            if(remainder < i){
                strength += upgrades[i][index];
            }
        }

        return strength;
    }
}

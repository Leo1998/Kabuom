package tower;

import world.World;

import java.util.ArrayList;

import static utility.Utility.*;

public class TowerHandler {

    private World world;

    public TowerHandler(World world) {
        this.world = world;
    }

    public void handleTowers(float dt, ArrayList<Tower> towers) {
        for(int  i = 0; i < towers.size(); i++){
            Tower curTower = towers.get(i);
            if (curTower.getHp() <= 0) {
                world.removeTower(curTower);
                i--;
            } else {
                if (curTower.towerType.canShoot) {

                    if (curTower.getCooldown() <= 0) {
                        curTower.setCooldown(curTower.towerType.frequency * (random.nextFloat()*0.25f + 0.75f));
                        curTower.setTarget(shootEnemy(curTower,curTower.towerType,world));
                    }
                    if (curTower.getCooldown() >= 0) {
                        curTower.setCooldown(curTower.getCooldown() - dt);
                    }
                }
            }
        }
    }

    public void regenerateTowers(ArrayList<Tower> towers){
        for(Tower tower:towers){
            if(tower.getHp() < tower.towerType.getMaxHP()){
                tower.addHp(tower.towerType.getMaxHP()/10);
            }
        }
    }

    public void noWave(ArrayList<Tower> towers, float dt){
        for(Tower tower:towers){
            tower.setTarget(null);
            float cooldown = tower.getCooldown();
            if(cooldown > 0) {
                if (cooldown - dt < 0) {
                    tower.setCooldown(0);
                }else{
                    tower.setCooldown(cooldown-dt);
                }
            }
        }
    }

}
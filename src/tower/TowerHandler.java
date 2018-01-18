package tower;

import utility.Vector2;
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
                        shoot(curTower);
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

    private void shoot(Tower tower) {
        tower.setCooldown(tower.towerType.frequency * (random.nextFloat()*0.25f + 0.75f));
        tower.setTarget(getTargetEnemy(tower,tower.towerType.attackRadius,world));
        if (tower.getTarget() != null) {

            Vector2 aiming = aim(tower,tower.getTarget(),tower.getTarget().getMovement(),tower.towerType.projectileType.speed);

            for (int i = 0; i < tower.towerType.shots; i++) {
                Vector2 copy = aiming.clone();

                world.spawnProjectile(createProjectile(tower,copy,tower.towerType.accuracy,tower.towerType.projectileType));
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
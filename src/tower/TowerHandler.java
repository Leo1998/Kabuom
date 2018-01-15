package tower;

import enemy.Enemy;
import projectile.Projectile;
import utility.Utility;
import utility.Vector2;
import world.World;

import java.util.ArrayList;
import java.util.Random;

public class TowerHandler {

    private final static Random random = new Random();

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

    private Enemy getClosestEnemy(Tower tower) {
        Enemy closest = null;
        for (int i = Math.max(0,(int) (Math.floor(tower.getX()) - tower.towerType.attackRadius)); i < Math.min(world.getBlocks().length,Math.ceil(tower.getX()) + tower.towerType.attackRadius); i++) {
            for (int j = Math.max(0, (int) (Math.floor(tower.getY()) - tower.towerType.attackRadius)); j < Math.min(world.getBlocks()[i].length, Math.ceil(tower.getY()) + tower.towerType.attackRadius); j++) {
                for(Enemy enemy:world.getBlocks()[i][j]){
                    if(closest == null || getDist(tower,enemy) < getDist(tower,closest)){
                        closest = enemy;
                    }
                }
            }
        }

        if(closest != null && getDist(tower,closest) > tower.towerType.attackRadius){
            closest = null;
        }
        return closest;
    }

    private float getDist(Tower tower, Enemy enemy) {
        //return -enemy.getY();
        return (float) Math.sqrt(Math.pow(tower.getX() - enemy.getX(), 2) + Math.pow(tower.getY() - enemy.getY(), 2));
    }

    private void shoot(Tower tower) {
        tower.setCooldown(tower.towerType.frequency * (random.nextFloat()*0.25f + 0.75f));
        tower.setTarget(getClosestEnemy(tower));
        if (tower.getTarget() != null) {

            float towerX = tower.getX();
            float towerY = tower.getY();

            float ex = tower.getTarget().getX();
            float ey = tower.getTarget().getY();

            if (tower.towerType == TowerType.FLAMETHROWER) {
                ex = ex + random.nextFloat() * 2 - 1;
                ey = ey + random.nextFloat() * 2 - 1;
            }

            float emx = tower.getTarget().getMovement().getCoords()[0];
            float emy = tower.getTarget().getMovement().getCoords()[1];
            float s = tower.towerType.projectileType.speed;

            float a = emx * emx + emy * emy - s * s;
            float b = 2 * ((ex - towerX) * emx + (ey - towerY) * emy);
            float c = (ex - towerX) * (ex - towerX) + (ey - towerY) * (ey - towerY);
            float d = b * b - 4 * a * c;

            Vector2 vec;
            if (d >= 0) {
                float sqrtD = (float) Math.sqrt(d);
                float t1 = (-b + sqrtD) / (2 * a);
                float t2 = (-b - sqrtD) / (2 * a);

                float aimX = ex - towerX + Math.min(t1, t2) * emx;
                float aimY = ey - towerY + Math.min(t1, t2) * emy;

                vec = new Vector2(aimX, aimY);

            } else {
                vec = new Vector2(ex - towerX, ey - towerY);
            }

            vec.normalize();

            Projectile p = new Projectile(tower.towerType.projectileType, tower.getLevel(), towerX, towerY, vec);

            p.setX(p.getX() + p.getDir().getCoords()[0]*tower.towerType.radius);
            p.setY(p.getY() + p.getDir().getCoords()[1]*tower.towerType.radius);

            world.spawnProjectile(p);

            if (tower.towerType == TowerType.CYROGUN) {
                float alpha = (float) (Utility.calculateAngleBetweenTwoPoints(towerX, towerY, towerX + vec.getCoords()[0], towerY + vec.getCoords()[1]) + 1 / 32 * Math.PI);
                float beta = (float) (Utility.calculateAngleBetweenTwoPoints(towerX, towerY, towerX + vec.getCoords()[0], towerY + vec.getCoords()[1]) - 1 / 32 * Math.PI);
                Vector2 vec_A = new Vector2((float) (Math.cos(alpha) + vec.getCoords()[0]), (float) (Math.sin(alpha) + vec.getCoords()[1]));
                vec_A.normalize();
                Vector2 vec_B = new Vector2((float) (vec.getCoords()[0] - Math.cos(beta)), (float) (vec.getCoords()[1] - Math.sin(beta)));
                vec_B.normalize();
                Projectile p1 = new Projectile(tower.towerType.projectileType, tower.getLevel(), towerX, towerY, vec_A);
                Projectile p2 = new Projectile(tower.towerType.projectileType, tower.getLevel(), towerX, towerY, vec_B);

                world.spawnProjectile(p1);
                world.spawnProjectile(p2);

            }
        }
    }

    public void noWave(ArrayList<Tower> towers, float dt){
        for(Tower tower:towers){
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
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

    public void handleTowers(float dt, ArrayList<Tower> towers, ArrayList<Enemy> enemies) {
        for (Tower curTower : towers) {
            if (curTower.getHp() <= 0) {
                world.removeGameObject(curTower);
            } else {
                if (curTower.getType().canShoot()) {

                    if (curTower.getCooldown() <= 0) {
                        shoot(curTower, enemies);
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
            if(tower.getHp() < tower.getMaxHp()){
                tower.addHp(tower.getMaxHp()/10);
            }
        }
    }

    private Enemy getClosestEnemy(ArrayList<Enemy> enemies, Tower tower) {
        if (enemies.size() > 0) {
            Enemy closest = enemies.get(0);
            for (Enemy enemy : enemies) {
                if (getDist(tower, enemy) < getDist(tower, closest)) {
                    closest = enemy;
                }
            }
            if (getDist(tower, closest) > tower.getType().getAttackRadius()) {
                return null;
            } else {
                return closest;
            }
        } else {
            return null;
        }
    }

    private float getDist(Tower tower, Enemy enemy) {
        return (float) Math.sqrt(Math.pow(tower.getX() - enemy.getX(), 2) + Math.pow(tower.getY() - enemy.getY(), 2));
    }

    private void shoot(Tower tower, ArrayList<Enemy> enemies) {
        tower.setCooldown(tower.getFrequency());
        tower.setTarget(getClosestEnemy(enemies, tower));

        if (tower.getTarget() != null) {

            Vector2 vec = null;

            float towerX = tower.getX();
            float towerY = tower.getY();

            float ex = tower.getTarget().getX();
            float ey = tower.getTarget().getY();

            if (tower.getType() == TowerType.FLAMETHROWER) {
                ex = ex + random.nextFloat() * 2 - 1;
                ey = ey + random.nextFloat() * 2 - 1;
            }

            float emx = tower.getTarget().getMovement().getCoords()[0];
            float emy = tower.getTarget().getMovement().getCoords()[1];
            float s = tower.getProjectile().getSpeed();

            float a = emx * emx + emy * emy - s * s;
            float b = 2 * ((ex - towerX) * emx + (ey - towerY) * emy);
            float c = (ex - towerX) * (ex - towerX) + (ey - towerY) * (ey - towerY);
            float d = b * b - 4 * a * c;

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

            Projectile p = new Projectile(tower.getProjectile(), tower.getLevel(), towerX, towerY, vec);
            world.spawnProjectile(p);

            if (tower.getType() == TowerType.CYROGUN) {
                float alpha = (float) (Utility.calculateAngleBetweenTwoPoints(towerX, towerY, towerX + vec.getCoords()[0], towerY + vec.getCoords()[1]) + 1 / 32 * Math.PI);
                float beta = (float) (Utility.calculateAngleBetweenTwoPoints(towerX, towerY, towerX + vec.getCoords()[0], towerY + vec.getCoords()[1]) - 1 / 32 * Math.PI);
                Vector2 vec_A = new Vector2((float) (Math.cos(alpha) + vec.getCoords()[0]), (float) (Math.sin(alpha) + vec.getCoords()[1]));
                vec_A.normalize();
                Vector2 vec_B = new Vector2((float) (vec.getCoords()[0] - Math.cos(beta)), (float) (vec.getCoords()[1] - Math.sin(beta)));
                vec_B.normalize();
                Projectile p1 = new Projectile(tower.getProjectile(), tower.getLevel(), towerX, towerY, vec_A);
                Projectile p2 = new Projectile(tower.getProjectile(), tower.getLevel(), towerX, towerY, vec_B);

                world.spawnProjectile(p1);
                world.spawnProjectile(p2);

            }
        }

        /*if (tower.getType() == TowerType.MGTURRET){
            //main idea
            //     angle of the slope of a straight through P1(enemyX|enemyY) and P2(turretX|turretY) = tan^-1 of ((distance of enemyY and towerY)/(distance of enemyX and towerX)
            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            //                                                              x coordinate on circle of radius of tower,y coordinate on circle of radius of tower
            new Projectile(ProjectileType.BULLET, tower.getLevel(),(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY,enemyX,enemyY);

        }else  if (tower.getType() == TowerType.SNIPER) {

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new Projectile(ProjectileType.PIERCINGBULLET,tower.getLevel(),(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY, enemyX,enemyY);

        }else  if (tower.getType() == TowerType.POISONTOWER) {

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new Projectile(ProjectileType.POISON,tower.getLevel(),(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY, enemyX,enemyY);

        }else if (tower.getType() == TowerType.MISSILELAUNCHER) {

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new Projectile(ProjectileType.MISSILE,tower.getLevel(),(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY, enemyX,enemyY);

        }else if (tower.getType() == TowerType.TESLACOIL){
            //does not need the formula shit, lightning starts in middle of tower XD
            new Projectile(ProjectileType.LIGHTNING,tower.getLevel(),towerX,towerY,enemyX,enemyY);


        }else if (tower.getType() == TowerType.CYROGUN) {

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new Projectile(ProjectileType.ICE,tower.getLevel(),(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY,enemyX,enemyY);


        }else if (tower.getType() == TowerType.FLAMETHROWER){

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new Projectile(ProjectileType.FLAME, tower.getLevel(),(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY,enemyX,enemyY);
            //does not only shoot one fire bullet, shoots like a hopper
            for (int i=1;i<=10;i++){
                double betha=alpha+i;
                double gamma=alpha-i;
                new Projectile(ProjectileType.FLAME,tower.getLevel(),(float) Math.cos(betha)*towerEnemyDistanceX+towerX,(float) Math.sin(betha)*towerEnemyDistanceY+towerY,enemyX,enemyY);
                new Projectile(ProjectileType.FLAME,tower.getLevel(),(float) Math.cos(gamma)*towerEnemyDistanceX+towerX,(float) Math.sin(gamma)*towerEnemyDistanceY+towerY,enemyX,enemyY);
            }
        }*/
    }

}
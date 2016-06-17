package tower;

import enemy.Enemy;
import graph.List;
import projectile.ProjectileType;

/**
 * Created by Daniel on 09.06.2016.
 */
public class TowerHandler {
    public void handleTowers(float dt, List<Tower> towers, List<Enemy> enemies) {
        Tower curTower= towers.getContent();


    }

    public void aim(Tower tower) {

        //hier ultrageile Formel einfuegen... Mach ich spaeter XD
    }


/*
    public void shoot(Tower tower) {
        float enemyX = tower.getTarget().getX();
        float enemyY = tower.getTarget().getY();
        float towerX = tower.getX();
        float towerY = tower.getY();
        float towerEnemyDistanceX = enemyX - towerX;
        float towerEnemyDistanceY = enemyY - towerY;

        if (tower.getType() == TowerType.MGTURRET){
            //main idea
            //     angle of the slope of a straight through P1(enemyX|enemyY) and P2(turretX|turretY) = tan^-1 of ((distance of enemyY and towerY)/(distance of enemyX and towerX)
            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            //                                                              x coordinate on circle of radius of tower,y coordinate on circle of radius of tower
            new projectile.Projectile(1, tower.getLevel(), "Bullet",(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY, 300, ProjectileType.BULLET,enemyX,enemyY);

        }else  if (tower.getType() == TowerType.SNIPER) {

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new projectile.Projectile(1, tower.getLevel(), "PiercingBullet",(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY, 300, ProjectileType.PIERCINGBULLET,enemyX,enemyY);

        }else  if (tower.getType() == TowerType.POISONTOWER) {

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new projectile.Projectile(1, tower.getLevel(), "Missile",(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY, 300, ProjectileType.POISON,enemyX,enemyY);

        }else if (tower.getType() == TowerType.MISSILELAUNCHER) {

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new projectile.Projectile(1, tower.getLevel(), "Missile",(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY, 300, ProjectileType.MISSILE,enemyX,enemyY);

        }else if (tower.getType() == TowerType.TESLACOIL){
            //does not need the formula shit, lightning starts in middle of tower XD
            new projectile.Projectile(1,tower.getLevel(),"Lightning",towerX,towerY,150,ProjectileType.LIGHTNING,enemyX,enemyY);


        }else if (tower.getType() == TowerType.CYROGUN) {

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new projectile.Projectile(1,tower.getLevel(),"Ice",(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY,200,ProjectileType.ICE,enemyX,enemyY);


        }else if (tower.getType() == TowerType.FLAMETHROWER){

            double alpha = Math.atan((towerEnemyDistanceY) / (towerEnemyDistanceX));
            new projectile.Projectile(1,tower.getLevel(),"Flame",(float) Math.cos(alpha)*tower.getRadius()+towerX,(float) Math.sin(alpha)*tower.getRadius()+towerY,200,ProjectileType.FLAME,enemyX,enemyY);
            //does not only shoot one fire bullet, shoots like a hopper
            for (int i=1;i<=10;i++){
                double betha=alpha+i;
                double gamma=alpha-i;
                new projectile.Projectile(1,tower.getLevel(),"Flame",(float) Math.cos(betha)*towerEnemyDistanceX+towerX,(float) Math.sin(betha)*towerEnemyDistanceY+towerY,200,ProjectileType.FLAME,enemyX,enemyY);
                new projectile.Projectile(1,tower.getLevel(),"Flame",(float) Math.cos(gamma)*towerEnemyDistanceX+towerX,(float) Math.sin(gamma)*towerEnemyDistanceY+towerY,200,ProjectileType.FLAME,enemyX,enemyY);
            }

        }
    }*/
}
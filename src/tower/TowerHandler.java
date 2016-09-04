package tower;

import enemy.Enemy;
import projectile.Projectile;
import projectile.ProjectileType;
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
        for (int i = 0; i < towers.size(); i++) {
            Tower curTower = towers.get(i);
            if(curTower.getHp() <= 0){
                world.removeGameObject(curTower);
            }else {
                if (curTower.getType().canShoot()) {
                    aim(curTower, enemies);

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

    public void aim(Tower tower, ArrayList<Enemy> enemies) {
        ArrayList<Enemy> targetList = new ArrayList<>();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy curEnemy = enemies.get(i);

            if (enemyInRange(curEnemy, tower)){
                targetList.add(curEnemy);
            }
        }

        Enemy target = null;

        float shortestWay = Float.MAX_VALUE;
        int highestBounty = 0;
        for (int j = 0; j < targetList.size(); j++) {
            if(targetList.get(j).getInDanger()>highestBounty){
                highestBounty=targetList.get(j).getInDanger();
            }
        }

        for (int j = 0; j < targetList.size(); j++) {
            if (new Vector2(tower.getX() - targetList.get(j).getX(), tower.getY() - targetList.get(j).getY()).getLength() < shortestWay) {
                if (targetList.get(j).getInDanger()+1!=highestBounty) {
                    target = targetList.get(j);
                    shortestWay = new Vector2(tower.getX() - targetList.get(j).getX(), tower.getY() - targetList.get(j).getY()).getLength();
                }
            }
        }
        if (tower.getTarget()==null&&target!=null){//tower hat noch kein target
            tower.setTarget(target);
            target.setInDanger(target.getInDanger() + 1);
        }else if (target!=null&&tower.getTarget()!=null&&!target.equals(tower.getTarget())) {//tower hat ein neues target
            tower.getTarget().setInDanger(tower.getTarget().getInDanger() - 1);
            tower.setTarget(target);
            target.setInDanger(target.getInDanger() + 1);
        }else if (target==null){//there is no spoon
            tower.setTarget(null);
        }

    }

    public void shoot(Tower tower) {
        tower.setCooldown(tower.getFrequency());

        if (tower.getTarget() == null) {
            return;
        }

        float enemyX = tower.getTarget().getX();
        float enemyY = tower.getTarget().getY();
        float towerX = tower.getX();
        float towerY = tower.getY();

        ProjectileType projectile = tower.getProjectile();

        if (tower.getType() == TowerType.FLAMETHROWER) {
            enemyX = enemyX + random.nextFloat() * 2 - 1;
            enemyY = enemyY + random.nextFloat() * 2 - 1;
        }



        Vector2 vec = new Vector2(enemyX - towerX, enemyY - towerY);
        vec.normalize();

        Projectile p = new Projectile(projectile, tower.getLevel(),towerX,towerY,vec);
        world.spawnProjectile(p);

        if(tower.getType()== TowerType.CYROGUN){
            float alpha = (float)(Utility.calculateAngleBetweenTwoPoints(towerX,towerY,enemyX,enemyY) + 1/32 * Math.PI);
            float beta = (float)(Utility.calculateAngleBetweenTwoPoints(towerX,towerY,enemyX,enemyY) - 1/32 * Math.PI);
            Vector2 vec_A = new Vector2((float)(Math.cos(alpha)+vec.getCoords()[0]),(float)(Math.sin(alpha)+vec.getCoords()[1]));
            vec_A.normalize();
            Vector2 vec_B = new Vector2((float)(vec.getCoords()[0]-Math.cos(beta)),(float)(vec.getCoords()[1]-Math.sin(beta)));
            vec_B.normalize();
            Projectile a = new Projectile(projectile, tower.getLevel(), towerX,towerY,vec_A);
            Projectile b = new Projectile(projectile, tower.getLevel(), towerX,towerY,vec_B);

            world.spawnProjectile(a);
            world.spawnProjectile(b);

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
    public boolean enemyInRange(Enemy e1, Tower t1){
        if(e1!= null && t1 != null){
            if(new Vector2(e1.getX() - t1.getX(), e1.getY() - t1.getY()).getLength() <= e1.getRadius()+t1.getAttackRadius()) {
                return true;
            }
        }
        return false;
    }
}
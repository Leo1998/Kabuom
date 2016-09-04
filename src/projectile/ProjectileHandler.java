package projectile;

import enemy.Enemy;
import tower.TowerType;
import utility.*;
import world.World;

import java.util.ArrayList;
import java.util.Random;

public class ProjectileHandler {

    private World world;

    public ProjectileHandler(World world) {
        this.world = world;
    }

    // Projektile bewegen & Kollisionen mit Gegner überprüfen -> schaden
    public void handleProjectiles(float dt, ArrayList<Projectile> projectiles, ArrayList<Enemy> enemies) {

        for(int i = 0;i < projectiles.size();i++) {
            Projectile p = projectiles.get(i);

            //System.out.println(p.getX() + "  " + p.getY());
            //weite die das Projektil geflogen ist wird aktualisiert
            if(p.getDistance() < p.getRange()){
                p.setDistance(p.getDistance() + p.getSpeed() * dt);
            }
            //wenn die distanz größer als die reichweite ist wird das projektil entfernt
            if(p.getDistance() >= p.getRange()) {
                world.removeGameObject(p);
            }

            //projektil fliegt in richtung des zieles
            p.setX(p.getX() + p.getDir().getCoords()[0] * p.getSpeed() * dt);
            p.setY(p.getY() + p.getDir().getCoords()[1] * p.getSpeed() * dt);
        }

        for(int i = 0;i < projectiles.size();i++){
            for(int j = 0;j < enemies.size();j++){
                //falls projektil mit gegner kollidiert und der gegner noch nicht getroffen ist
                if(Utility.gameObjectIsCollidingWithGameObject(projectiles.get(i),enemies.get(j)) && !projectiles.get(i).getHitEnemies().contains(enemies.get(j))){
                    //verringere die hp des gegners, verringere die hp des projektils, füge gegner zu getroffenen hinzu
                    enemies.get(j).setHp(enemies.get(j).getHp() - projectiles.get(i).getImpactDamage());
                    projectiles.get(i).setHp(projectiles.get(i).getHp()-1);
                    projectiles.get(i).addToHitEnemies(enemies.get(j));
                    //falls hp des projektils == 0 ist
                    if(projectiles.get(i).getHp()==0){
                        //lösche das projektil
                        //wenn projektil gift, dann fette giftwolke
                        if(projectiles.get(i).getProjectileType() == ProjectileType.POISON){
                            Random random = new Random();
                            for(int w = 0; w < 15 ; w++){
                                float alpha = random.nextFloat()*(float)(Math.PI * 2);
                                float distance = random.nextFloat()*1.5f;
                                float y = projectiles.get(i).getY()-(float)(Math.sin(alpha) * distance);
                                float x = projectiles.get(i).getX()-(float)(Math.cos(alpha) * distance);
                                world.spawnProjectile(new Projectile(ProjectileType.POISONTRAIL,projectiles.get(i).getLevel(),x,y,projectiles.get(i).getDir()));
                            }
                        }
                        world.removeGameObject(projectiles.get(i));

                    }
                }
            }
        }


        // Projektile werden bewegt.
        /*
        for(int i = 0;i < projectiles.size();i++) {
            if (projectiles.get(i).getTargetX() - projectiles.get(i).getX() != 0 && projectiles.get(i).getTargetY() - projectiles.get(i).getY() != 0) {
                float temp = (float)Math.sqrt((Math.pow(projectiles.get(i).getTargetX() - projectiles.get(i).getX(),2)+Math.pow(projectiles.get(i).getTargetY() - projectiles.get(i).getY(),2)))/projectiles.get(i).getSpeed();
                projectiles.get(i).setX(projectiles.get(i).getX() + (projectiles.get(i).getTargetX() - projectiles.get(i).getX()) / temp);
                projectiles.get(i).setY(projectiles.get(i).getY() + (projectiles.get(i).getTargetY() - projectiles.get(i).getY()) / temp);

                   // AB HIER WIRD GEPRÜFT OB DER PROJECTILE SICH NUR IN X ODER Y RICHTUNG BEWEGEN MUSS!

            }else if(projectiles.get(i).getTargetX() - projectiles.get(i).getX() != 0 && projectiles.get(i).getTargetY() - projectiles.get(i).getY() == 0){
                if(projectiles.get(i).getX() < projectiles.get(i).getTargetX()) {
                    projectiles.get(i).setX(projectiles.get(i).getX() + projectiles.get(i).getSpeed());
                }else{
                    projectiles.get(i).setX(projectiles.get(i).getX() - projectiles.get(i).getSpeed());
                }
            }else if(projectiles.get(i).getTargetX() - projectiles.get(i).getX() == 0 && projectiles.get(i).getTargetY() - projectiles.get(i).getY() != 0){
                if(projectiles.get(i).getY() < projectiles.get(i).getTargetY()) {
                    projectiles.get(i).setY(projectiles.get(i).getY() + projectiles.get(i).getSpeed());
                }else{
                    projectiles.get(i).setY(projectiles.get(i).getY() - projectiles.get(i).getSpeed());
                }
            }
            //geflogene distanz des projektils wird vergrößert
            if(projectiles.get(i).getDistance() < projectiles.get(i).getRange()){
                projectiles.get(i).setDistance(projectiles.get(i).getDistance() + projectiles.get(i).getSpeed());
            }
            //wenn die distanz größer als die reichweite ist wird das projektil entfernt
            if(projectiles.get(i).getDistance() >= projectiles.get(i).getRange()) {
                projectiles.remove(i);
            }
        }*/

        /*// Kollisionen werden überprüft.
        for(int i = 0;i < projectiles.size();i++){
            for(int j = 0;j < enemies.size();i++){
                //falls projektil mit gegner kollidiert und der gegner noch nicht getroffen ist
                if(utility.gameObjectIsCollidingWithGameObject(projectiles.get(i),enemies.get(j)) && !projectiles.get(i).getHitEnemies().contains(enemies.get(j))){
                    //verringere die hp des gegners, verringere die hp des projektils, füge gegner zu getroffenen hinzu
                    enemies.get(j).setHp(enemies.get(j).getHp() - projectiles.get(i).getImpactDamage());
                    projectiles.get(i).setHp(projectiles.get(i).getHp()-1);
                    projectiles.get(i).addToHitEnemies(enemies.get(j));
                    //falls hp des projektils == 0 ist
                    if(projectiles.get(i).getHp()==0){
                        //lösche das projektil
                        projectiles.remove(i);
                    }
                }
            }
        }*/
    }

}


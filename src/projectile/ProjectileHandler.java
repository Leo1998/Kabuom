package projectile;

import enemy.Enemy;
import utility.*;

import java.util.ArrayList;

public class ProjectileHandler {

    // Projektile bewegen & Kollisionen mit Gegner überprüfen -> schaden
    public void handleProjectiles(float dt, ArrayList<Projectile> projectiles, ArrayList<Enemy> enemies) {

        Utility utility = new Utility();

        // Projektile werden bewegt.
        for(int i = 0;i < projectiles.size();i++) {
            if (projectiles.get(i).getTargetX() - projectiles.get(i).getX() != 0 && projectiles.get(i).getTargetY() - projectiles.get(i).getY() != 0) {
                float temp = (float)Math.sqrt((Math.pow(projectiles.get(i).getTargetX() - projectiles.get(i).getX(),2)+Math.pow(projectiles.get(i).getTargetY() - projectiles.get(i).getY(),2)))/projectiles.get(i).getSpeed();
                projectiles.get(i).setX(projectiles.get(i).getX() + (projectiles.get(i).getTargetX() - projectiles.get(i).getX()) / temp);
                projectiles.get(i).setY(projectiles.get(i).getY() + (projectiles.get(i).getTargetY() - projectiles.get(i).getY()) / temp);
                /*
                    AB HIER WIRD GEPRÜFT OB DER PROJECTILE SICH NUR IN X ODER Y RICHTUNG BEWEGEN MUSS!
                 */
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
        }

        // Kollisionen werden überprüft.
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
        }
    }

}


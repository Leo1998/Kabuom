package projectile;

import enemy.Enemy;
import graph.List;
import utility.Vector2;

/**
 * Created by Daniel on 09.06.2016.
 */
public class ProjectileHandler {
    public void handleProjectiles(float dt,List<Projectile> projectiles,List<Enemy> enemies){



    }

    public boolean isColliding(Projectile p, Enemy e){
        if(new Vector2(p.getX(),p.getY(),e.getX(),e.getY()).getLength() <= ){

        }
    }
}

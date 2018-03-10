package entity.utility;
import model.Position;

/**
 * Objects which have a Position and belong to either the Player or Enemies
 */
public interface Partisan extends Position {
    boolean isEnemy();

    default boolean allyOf(Partisan partisan){
        return partisan == null || partisan.isEnemy() == isEnemy();
    }
}

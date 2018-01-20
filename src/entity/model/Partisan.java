package entity.model;
import model.Position;

/**
 * Objects which have a Position and belong to either the Player or Enemies
 */
public interface Partisan extends Position {
    boolean isEnemy();

    boolean allyOf(Partisan partisan);
}

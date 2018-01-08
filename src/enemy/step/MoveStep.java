package enemy.step;

/**
 * Created by Daniel on 29-Dec-17.
 */
public class MoveStep extends Step {

    public final int x, y;

    public MoveStep(int x, int y) {
        super(StepType.Move);
        this.x = x;
        this.y = y;
    }

    @Override
    public MoveStep clone() throws CloneNotSupportedException {
        return new MoveStep(x, y);
    }
}

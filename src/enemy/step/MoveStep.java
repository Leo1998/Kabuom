package enemy.step;

/**
 * Created by Daniel on 29-Dec-17.
 */
public class MoveStep extends Step {

    public final int x,y;

    public MoveStep(StepType stepType, int x, int y) {
        super(stepType);
        this.x = x;
        this.y = y;
    }

    @Override
    public MoveStep clone() throws CloneNotSupportedException {
        return new MoveStep(stepType,x,y);
    }
}

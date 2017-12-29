package enemy.step;

/**
 * Created by Daniel on 29-Dec-17.
 */
public class Step {
    public final StepType stepType;

    public Step(StepType stepType) {
        this.stepType = stepType;
    }

    @Override
    public Step clone() throws CloneNotSupportedException {
        return new Step(stepType);
    }
}

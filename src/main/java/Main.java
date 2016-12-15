import controller.FeatureSelectionController;
import controller.impl.BackwardFeatureSelectionController;
import controller.impl.old.RemovedFeatureSelectionController;
import controller.impl.MergingFeatureSelectionControllerImpl;



/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class Main {

    private static RunMode runMode = RunMode.BACKWARD;

    private enum RunMode {MERGING, BACKWARD, FORWARD}

    private static FeatureSelectionController mergingFeatureSelectionController = new MergingFeatureSelectionControllerImpl();
    private static FeatureSelectionController backwardFeatureSelectionController = new BackwardFeatureSelectionController();
    private static FeatureSelectionController forwardFeatureSelectionController = new RemovedFeatureSelectionController();

    public static void main(String[] args) throws Exception {

        FeatureSelectionController controllerInstance;

        switch (runMode) {
            case MERGING:
                controllerInstance = mergingFeatureSelectionController;
                break;
            case BACKWARD:
                controllerInstance = backwardFeatureSelectionController;
                break;
            case FORWARD:
                controllerInstance = forwardFeatureSelectionController;
                break;
            default:
                return;
        }

        controllerInstance.start();
    }
}

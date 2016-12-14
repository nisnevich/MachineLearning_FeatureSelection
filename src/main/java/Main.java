import controller.FeatureSelectionController;
import controller.impl.FeatureSelectionCutController;
import controller.impl.FeatureSelectionFastController;
import controller.impl.FeatureSelectionLazyController;
import controller.impl.FeatureSelectionRemovalController;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class Main {

    private static RunMode runMode = RunMode.REMOVAL;

    private enum RunMode {FAST, LAZY, CUT, REMOVAL}

    private static FeatureSelectionLazyController featureSelectionLazyController = new FeatureSelectionLazyController();
    private static FeatureSelectionFastController featureSelectionFastController = new FeatureSelectionFastController();
    private static FeatureSelectionCutController featureSelectionCutController = new FeatureSelectionCutController();
    private static FeatureSelectionRemovalController featureSelectionRemovalController = new FeatureSelectionRemovalController();

    public static void main(String[] args) {

        FeatureSelectionController controllerInstance;

        switch (runMode) {
            case FAST:
                controllerInstance = featureSelectionFastController;
                break;
            case LAZY:
                controllerInstance = featureSelectionLazyController;
                break;
            case CUT:
                controllerInstance = featureSelectionCutController;
                break;
            case REMOVAL:
                controllerInstance = featureSelectionRemovalController;
                break;
            default:
                return;
        }

        try {
            controllerInstance.start();
        } catch (Exception e) {
            System.err.println(String.format("%s occurred: %s",
                    e.getClass().getSimpleName(), e.getMessage()));
            System.out.println("Dosvidanya, mir!");
        }
    }
}

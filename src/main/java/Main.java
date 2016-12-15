import controller.FeatureSelectionController;
import controller.impl.FeatureSelectionControllerImpl;
import controller.impl.old.FeatureSelectionCutController;
import controller.impl.old.FeatureSelectionFastController;
import controller.impl.old.FeatureSelectionLazyController;
import controller.impl.old.FeatureSelectionRemovalController;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class Main {

    private static RunMode runMode = RunMode.NORMAL;

    private enum RunMode {NORMAL, FAST, LAZY, CUT, REMOVAL}

    private static FeatureSelectionController featureSelectionNormalController = new FeatureSelectionControllerImpl();
    private static FeatureSelectionLazyController featureSelectionLazyController = new FeatureSelectionLazyController();
    private static FeatureSelectionFastController featureSelectionFastController = new FeatureSelectionFastController();
    private static FeatureSelectionCutController featureSelectionCutController = new FeatureSelectionCutController();
    private static FeatureSelectionRemovalController featureSelectionRemovalController = new FeatureSelectionRemovalController();

    public static void main(String[] args) {

        FeatureSelectionController controllerInstance;

        switch (runMode) {
            case NORMAL:
                controllerInstance = featureSelectionNormalController;
                break;
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

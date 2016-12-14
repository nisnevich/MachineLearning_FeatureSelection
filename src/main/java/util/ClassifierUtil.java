package util;

import weka.classifiers.AbstractClassifier;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class ClassifierUtil {
    public void setClassifierOptions(AbstractClassifier classifier, String options) throws Exception {
        String[] optionsArray;
        try {
            optionsArray = weka.core.Utils.splitOptions(options);
        } catch (Exception e) {
            System.out.println("Error while creation set of options:" + options);
            throw e;
        }
        try {
            classifier.setOptions(optionsArray);
        } catch (Exception e) {
            System.out.println(String.format(
                    "Error while setting options for j48 instance. Options: \"%s\"", options));
            throw e;
        }
    }
}

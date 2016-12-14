package controller.impl;

import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public abstract class FeatureSelectionAbstractController {

    private FileSystemUtil fileSystemUtil = new FileSystemUtil();
    private ClassifierUtil classifierUtil = new ClassifierUtil();

    void classify(List<AbstractClassifier> classifiers, Instances dataset) throws Exception {
        for (int i = 0; i < classifiers.size(); i++) {
            AbstractClassifier classifier = classifiers.get(i);
//                    classifier.buildClassifier(dataset);
            Evaluation eval = new Evaluation(dataset);
            System.out.println("******** Using classifier " +
                    classifier.getClass().getSimpleName() + " ********\n");
//            eval.crossValidateModel(classifier, dataset, 100, new Random(1));
            eval.evaluateModel(classifier, dataset);
            System.out.println(eval.toSummaryString(false));

            classifiers.set(i, classifier.getClass().newInstance());

//                    fileSystemUtil.printClassificationResults(classifier);
//                    eval.evaluateModel(classifier, dataset);
        }
    }
}

package controller.impl;

import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.List;
import java.util.Random;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public abstract class FeatureSelectionAbstractController {

    private FileSystemUtil fileSystemUtil = new FileSystemUtil();
    private ClassifierUtil classifierUtil = new ClassifierUtil();

    protected void classify(List<AbstractClassifier> classifiers, Instances dataset) throws Exception {
        for (int i = 0; i < classifiers.size(); i++) {
            AbstractClassifier classifier = classifiers.get(i);
            System.out.println("******** Using classifier " +
                    classifier.getClass().getSimpleName() + " ********\n");

            Evaluation eval = new Evaluation(dataset);
            eval.crossValidateModel(classifier, dataset, 10, new Random(1));
            eval.evaluateModel(classifier, dataset);
            System.out.println(eval.toSummaryString(false));
        }
    }

    protected void classify(List<Classifier> classifiers, Instances train, Instances test) throws Exception {
        for (int i = 0; i < classifiers.size(); i++) {
            Classifier classifier = classifiers.get(i);
            System.out.println("******** Using classifier " +
                    classifier.getClass().getSimpleName() + " ********\n");
            System.out.println("Training...");
            classifier.buildClassifier(train);

            Evaluation eval = new Evaluation(train);
            System.out.println("Testing...");
            eval.evaluateModel(classifier, test);
            System.out.println(eval.toSummaryString(false));

            classifiers.set(i, classifier.getClass().newInstance());
        }
    }
}

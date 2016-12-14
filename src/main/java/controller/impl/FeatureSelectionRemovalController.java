package controller.impl;

import controller.FeatureSelectionController;
import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class FeatureSelectionRemovalController extends FeatureSelectionAbstractController
        implements FeatureSelectionController {

    private FileSystemUtil fileSystemUtil = new FileSystemUtil();
    private ClassifierUtil classifierUtil = new ClassifierUtil();

    private static final String POSTFIX_FS_TYPE = "corel";
//    private static final String POSTFIX_FS_TYPE = "infogain";
    private static final String CUT_RATE = "0.5";

    public void start() throws Exception {
        // Reading dataset
//        String datasetPath = "C:\\Program Files\\Weka-3-8\\data\\iris.arff";
        String trainDatasetPath = "dataset\\arcene_train.cutten."+CUT_RATE+"."+POSTFIX_FS_TYPE+".arff";

        System.out.println("-------------- Learning on dataset train." + CUT_RATE +"."+ POSTFIX_FS_TYPE);

        Instances trainDataset = new Instances(fileSystemUtil.readDataSet(trainDatasetPath));
        trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

        // Creating instances of classifiers
        List<AbstractClassifier> classifiers = createClassifiers();

        classify(classifiers, trainDataset);
    }

    @Override
    void classify(List<AbstractClassifier> classifiers, Instances trainDataset) throws Exception {
        for (AbstractClassifier classifier : classifiers) {
            classifier.buildClassifier(trainDataset);
        }

        String testDatasetPath = "dataset\\arcene_valid.cutten."+CUT_RATE+"."+POSTFIX_FS_TYPE+".arff";

        Instances testDataset = new Instances(fileSystemUtil.readDataSet(testDatasetPath));
        testDataset.setClassIndex(trainDataset.numAttributes() - 1);

        System.out.println("-------------- Testing on dataset valid." + CUT_RATE +"."+ POSTFIX_FS_TYPE);
        super.classify(classifiers, testDataset);
    }

    private List<AbstractClassifier> createClassifiers() {
        List<AbstractClassifier> classifiers = new ArrayList<>();
        J48 j48 = new J48();
        NaiveBayes naiveBayes = new NaiveBayes();

        classifiers.add(j48);
        classifiers.add(naiveBayes);

        return classifiers;
    }
}

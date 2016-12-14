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
public class FeatureSelectionFastController extends FeatureSelectionAbstractController
        implements FeatureSelectionController {

    private FileSystemUtil fileSystemUtil = new FileSystemUtil();
    private ClassifierUtil classifierUtil = new ClassifierUtil();

    public void start() throws Exception {
        // Reading dataset
//        String datasetPath = "C:\\Program Files\\Weka-3-8\\data\\iris.arff";
        String datasetPath = "dataset\\arcene_train.arff";
        String rankedListPath = "output\\rankedLists_3attr.txt";

        Instances dataset = new Instances(fileSystemUtil.readDataSet(datasetPath));
        dataset.setClassIndex(dataset.numAttributes() - 1);

        List<Integer> rankedList = fileSystemUtil.readRankedList(rankedListPath,
                "WrapperAttributeFeatureSelectionModel");

        System.out.println("============================================================================");
        System.out.println("Feature selection was done using WrapperAttributeFeatureSelectionModel." +
                " Removing all attributes excluding selected.");
        System.out.println("============================================================================");

        // Creating instances of classifiers
        List<AbstractClassifier> classifiers = createClassifiers();

        System.out.println("------------------- No attributes removed -------------------");
        classify(classifiers, dataset);

        int attrToRemoveCount = dataset.numAttributes() - rankedList.size();
        int removedAttrCount = 0;
        for (int i = dataset.numAttributes() - 2; i >= 0; i--) {
            if (! rankedList.contains(i + 1)) {
                if (removedAttrCount != 0 && removedAttrCount % (attrToRemoveCount / 10) == 0) {
                    System.out.println(String.format(
                            "------------------- Removed %d0%% attributes -------------------",
                            removedAttrCount / (attrToRemoveCount / 10)));
                    classify(classifiers, dataset);
                }
                dataset.deleteAttributeAt(i);
                removedAttrCount++;
            }
        }
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

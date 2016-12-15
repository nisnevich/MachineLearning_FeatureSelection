package controller.impl.old;

import controller.FeatureSelectionController;
import controller.impl.FeatureSelectionAbstractController;
import model.fs.FeatureSelectionModel;
import model.fs.impl.CorellationFeatureSelectionModel;
import model.fs.impl.InfoGainFeatureSelectionModel;
import model.fs.impl.WrapperAttributeFeatureSelectionModel;
import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Iterates over ranked attributes lists provided by different FS methods.
 * Removes one attribute per iteration (=> very slow).
 * Results are written to console.
 *
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class FeatureSelectionLazyController extends FeatureSelectionAbstractController
        implements FeatureSelectionController {

    // Boundaries: [1, 0]
    public static final double WORST_ATTRIBUTES_NUMBER_PROPORTION = 0.3;
    // Boundaries: [1, 0], but must be smaller than @WORST_ATTRIBUTES_NUMBER_PROPORTION
    public static final double BEST_ATTRIBUTES_NUMBER_PROPORTION = 0;

    private FileSystemUtil fileSystemUtil = new FileSystemUtil();
    private ClassifierUtil classifierUtil = new ClassifierUtil();

    public void start() throws Exception {
        // Reading dataset
//        String datasetPath = "C:\\Program Files\\Weka-3-8\\data\\iris.arff";
        String datasetPath = "dataset\\arcene_train.arff";

        Instances dataset = new Instances(fileSystemUtil.readDataSet(datasetPath));
        dataset.setClassIndex(dataset.numAttributes() - 1);

        // Creating instances of feature selection models
        List<FeatureSelectionModel> featureSelectionModels = new ArrayList<>();
        featureSelectionModels.add(new CorellationFeatureSelectionModel());
        featureSelectionModels.add(new InfoGainFeatureSelectionModel());
        featureSelectionModels.add(new WrapperAttributeFeatureSelectionModel());

        List<List<Integer>> listOfRankedLists = calculateRankedLists(dataset, featureSelectionModels);

        // Creating instances of classifiers
        List<AbstractClassifier> classifiers = createClassifiers();
        Instances datasetBackup = new Instances(dataset);

        int modelCounter = 0;
        for (FeatureSelectionModel model : featureSelectionModels) {
            System.out.println("============================================================================");
            System.out.println(String.format("Classification, step #%d. %s.", ++modelCounter, model));
            System.out.println("============================================================================");

            // Ranking attributes
            List<Integer> attrRankList = listOfRankedLists.get(modelCounter - 1);

            for (int attrIndex = (int)(attrRankList.size() * WORST_ATTRIBUTES_NUMBER_PROPORTION - 1) + 1, removedCounter = 0;
                    attrIndex >= attrRankList.size() * BEST_ATTRIBUTES_NUMBER_PROPORTION;
                    attrIndex--, removedCounter++) {
                if (datasetBackup.numAttributes() == attrRankList.size()
                        && attrIndex == 0) {
                    continue;
                }
//            for (int attrIndex = attrRankList.size() * BEST_ATTRIBUTES_NUMBER_PROPORTION, removedCounter = 0;
//                    attrIndex <= (attrRankList.size() * WORST_ATTRIBUTES_NUMBER_PROPORTION - 1);
//                    attrIndex++, removedCounter++) {
//                if (datasetBackup.numAttributes() == attrRankList.size()
//                        && attrIndex == attrRankList.size() - 1) {
//                    continue;
//                }
                dataset = new Instances(datasetBackup);
                // Do not remove for first iteration
                if (removedCounter > 0) {
                    // Selecting features (removing attributes one-by-one starting from worst)
                    List<Integer> attrToRemoveList = attrRankList.subList(attrIndex,
                            (int)(attrRankList.size() * WORST_ATTRIBUTES_NUMBER_PROPORTION));
                    Collections.sort(attrToRemoveList);
                    for (int i = attrToRemoveList.size() - 1; i >= 0; i--) {
                        dataset.deleteAttributeAt(attrToRemoveList.get(i));
                    }
//                    for (Integer attr : attrToRemoveList) {
//                        dataset.deleteAttributeAt(attr);
//                    }
                    System.out.println("\n\n-------------- " + (removedCounter) + " attribute(s) removed (of " +
                            (datasetBackup.numAttributes() - 1) + ") --------------\n");
                } else {
                    System.out.println("\n\n-------------- First iteration - without feature selection --------------\n");
                }

                classify(classifiers, dataset);
            }
        }
    }

    private List<List<Integer>> calculateRankedLists(Instances dataset,
                                                     List<FeatureSelectionModel> featureSelectionModels)
            throws Exception {
        List<List<Integer>> listOfRankedLists = new ArrayList<>();

        int modelCounter = 0;
        for (FeatureSelectionModel model : featureSelectionModels) {
            System.out.println("============================================================================");
            System.out.println(String.format("Feature selection, step #%d. %s.", ++modelCounter, model));
            System.out.println("============================================================================");
            List<Integer> attrRankList = model.rankAttributes(dataset);
            listOfRankedLists.add(attrRankList);
            System.out.println("Done.\n");
        }

        fileSystemUtil.printRankedLists(featureSelectionModels, listOfRankedLists);

        return listOfRankedLists;
    }

    private List<AbstractClassifier> createClassifiers() {
        List<AbstractClassifier> classifiers = new ArrayList<>();
        J48 j48 = new J48();
        NaiveBayes naiveBayes = new NaiveBayes();

        //classifiers.add(j48);
        classifiers.add(naiveBayes);

        return classifiers;
    }
}

package controller.impl.old;

import controller.FeatureSelectionController;
import controller.impl.FeatureSelectionAbstractController;
import model.fs.FeatureSelectionModel;
import model.fs.impl.CorellationFeatureSelectionModel;
import model.fs.impl.InfoGainFeatureSelectionModel;
import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
@SuppressWarnings("Duplicates")
public class RemovedFeatureSelectionController extends FeatureSelectionAbstractController
        implements FeatureSelectionController {

    // Boundaries: [1, 0]
    public static final double WORST_ATTRIBUTES_NUMBER_PROPORTION = 0.3;
    // Boundaries: [1, 0], but must be smaller than @WORST_ATTRIBUTES_NUMBER_PROPORTION
    public static final double BEST_ATTRIBUTES_NUMBER_PROPORTION = 0;

    private FileSystemUtil fileSystemUtil = new FileSystemUtil();
    private ClassifierUtil classifierUtil = new ClassifierUtil();

    public void start() throws Exception {
        // Reading dataset
        String trainDatasetPath = "dataset\\arcene_train.arff";
        String testDatasetPath = "dataset\\arcene_valid.arff";
        Instances trainDataset = new Instances(fileSystemUtil.readDataSet(trainDatasetPath));
        trainDataset.setClassIndex(trainDataset.numAttributes() - 1);
        Instances testDataset = new Instances(fileSystemUtil.readDataSet(testDatasetPath));
        testDataset.setClassIndex(testDataset.numAttributes() - 1);

        // Creating instances of feature selection models
        List<FeatureSelectionModel> featureSelectionModels = new LinkedList<>();
        featureSelectionModels.add(new CorellationFeatureSelectionModel());
        featureSelectionModels.add(new InfoGainFeatureSelectionModel());
//        featureSelectionModels.add(new WrapperAttributeFeatureSelectionModel());

        List<LinkedList<Integer>> listOfRankedLists = calculateRankedLists(trainDataset, featureSelectionModels);
        List<Classifier> classifiers = createClassifiers();

        Instances trainDatasetBackup = new Instances(trainDataset);
        Instances testDatasetBackup = new Instances(testDataset);

        int modelCounter = 0;
        for (FeatureSelectionModel model : featureSelectionModels) {
            System.out.println(String.format("Classification, step #%d. %s.", ++modelCounter, model));

            // Ranking attributes
            LinkedList<Integer> rankedList = listOfRankedLists.get(modelCounter - 1);

            List<Integer> tmpRankedList = (LinkedList) rankedList.clone();
            Collections.sort(tmpRankedList);
            System.out.println("Preparing dataset...");
            for (int i = tmpRankedList.size() - 1; i >= 0; i--) {
                trainDataset.deleteAttributeAt(tmpRankedList.get(i));
                testDataset.deleteAttributeAt(tmpRankedList.get(i));
            }

            for (int attrIndex = 0; attrIndex < rankedList.size(); attrIndex++) {
                // Selecting features (adding attributes one-by-one starting from best)
                Attribute trainAttr = trainDatasetBackup.attribute(rankedList.get(attrIndex));
                Attribute testAttr = testDatasetBackup.attribute(rankedList.get(attrIndex));

                List<String> trainAttrValuesList = new ArrayList<>();
                List<String> testAttrValuesList = new ArrayList<>();
                for (int i = 0; i < trainDatasetBackup.size(); i++) {
                    trainAttrValuesList.add(String.valueOf(trainDatasetBackup.get(i).value(attrIndex)));
                    testAttrValuesList.add(String.valueOf(testDatasetBackup.get(i).value(attrIndex)));
                }
                trainDataset.insertAttributeAt(new Attribute(trainAttr.name(), trainAttrValuesList), attrIndex);
                testDataset.insertAttributeAt(new Attribute(testAttr.name(), testAttrValuesList), attrIndex);
//                testDataset.insertAttributeAt((Attribute) testDataset.attribute(rankedList.get(attrIndex)).copy(), attrIndex);
                System.out.println("\n\n-------------- " + (attrIndex) + " attribute(s) included (of " +
                        (trainDatasetBackup.numAttributes() - 1) + ") --------------\n");
//                classify(classifiers, trainDataset, testDataset);
            }
        }
    }

    private List<LinkedList<Integer>> calculateRankedLists(Instances dataset,
                                                     List<FeatureSelectionModel> featureSelectionModels)
            throws Exception {
        List<LinkedList<Integer>> listOfRankedLists = new LinkedList<>();

        int modelCounter = 0;
        for (FeatureSelectionModel model : featureSelectionModels) {
            System.out.println(String.format("Feature selection, step #%d. %s.", ++modelCounter, model));
            LinkedList<Integer> attrRankList = (LinkedList<Integer>) model.rankAttributes(dataset);
            listOfRankedLists.add(attrRankList);
            System.out.println("Done.\n");
        }

//        fileSystemUtil.printRankedLists(featureSelectionModels, listOfRankedLists);

        return listOfRankedLists;
    }

    private List<Classifier> createClassifiers() {
        List<Classifier> classifiers = new LinkedList<>();
        SMO svm = new SMO();

        classifiers.add(svm);

        return classifiers;
    }
}

package controller.impl;

import controller.FeatureSelectionController;
import model.fs.FeatureSelectionModel;
import model.fs.impl.CorellationFeatureSelectionModel;
import model.fs.impl.InfoGainFeatureSelectionModel;
import model.fs.impl.SymmetricalUncertSelectionModel;
import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Iterates over ranked attributes lists provided by different FS methods and
 * removes one attribute per iteration (=> very slow). Results are written to console.
 *
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class BackwardFeatureSelectionController extends FeatureSelectionAbstractController
        implements FeatureSelectionController {

    // Boundaries: [1, 0]
    public static final double WORST_ATTRIBUTES_NUMBER_PROPORTION = 1;
    // Boundaries: [1, 0], but must be smaller than @WORST_ATTRIBUTES_NUMBER_PROPORTION
    public static final double BEST_ATTRIBUTES_NUMBER_PROPORTION = 0;

    private static final int ATTR_COUNT_TO_REMOVE_BY_STEP = 100;

    private FileSystemUtil fileSystemUtil = new FileSystemUtil();
    private ClassifierUtil classifierUtil = new ClassifierUtil();

    List<Instances> bestTrainList = new ArrayList<>();
    List<Instances> bestTestList = new ArrayList<>();

    private Instances bestTrain;
    private Instances bestTest;
    private String bestInfo;
    private double bestResult = 0;

    public void start() throws Exception {
        // Reading dataset
        String trainDatasetPath = "dataset\\arcene_train.arff";
        String testDatasetPath = "dataset\\arcene_valid.arff";
        Instances trainDataset = new Instances(fileSystemUtil.readDataSet(trainDatasetPath));
        trainDataset.setClassIndex(trainDataset.numAttributes() - 1);
        Instances testDataset = new Instances(fileSystemUtil.readDataSet(testDatasetPath));
        testDataset.setClassIndex(testDataset.numAttributes() - 1);

        // Creating instances of feature selection models
        List<FeatureSelectionModel> featureSelectionModels = new ArrayList<>();
        featureSelectionModels.add(new CorellationFeatureSelectionModel());
        featureSelectionModels.add(new InfoGainFeatureSelectionModel());
        featureSelectionModels.add(new SymmetricalUncertSelectionModel());
//        featureSelectionModels.add(new WrapperAttributeFeatureSelectionModel());

        List<List<Integer>> listOfRankedLists = calculateRankedLists(trainDataset, featureSelectionModels);
        List<Classifier> classifiers = createClassifiers();

        Instances trainDatasetBackup = new Instances(trainDataset);
        Instances testDatasetBackup = new Instances(testDataset);

        int modelCounter = 0;
        for (FeatureSelectionModel model : featureSelectionModels) {
            System.out.println(String.format("Classification, step #%d. %s.", ++modelCounter, model));

            // Ranking attributes
            List<Integer> attrRankList = listOfRankedLists.get(modelCounter - 1);
            int totalCountToRemove = ((int)(attrRankList.size() * WORST_ATTRIBUTES_NUMBER_PROPORTION - 1) + 1 -
                    (int) (attrRankList.size() * BEST_ATTRIBUTES_NUMBER_PROPORTION));

//            List<Integer> attrToRemoveList = attrRankList.subList(
//                    (int)(attrRankList.size() * WORST_ATTRIBUTES_NUMBER_PROPORTION - 1) + 1,
//                    attrRankList.size());
//            Collections.sort(attrToRemoveList);
//            for (int i = attrToRemoveList.size() - 1; i >= 0; i--) {
//                trainDataset.deleteAttributeAt(attrToRemoveList.get(i));
//                testDataset.deleteAttributeAt(attrToRemoveList.get(i));
//            }
            bestResult = 0;

            for (int attrIndex = (int)(attrRankList.size() * WORST_ATTRIBUTES_NUMBER_PROPORTION - modelCounter),
                        removedCounter = 0;
                    attrIndex >= attrRankList.size() * BEST_ATTRIBUTES_NUMBER_PROPORTION;
                    attrIndex -= ATTR_COUNT_TO_REMOVE_BY_STEP, removedCounter += ATTR_COUNT_TO_REMOVE_BY_STEP) {
                if (attrIndex == 0) {
                    continue;
                }
//                if (removedCounter != 0 && removedCounter % (totalCountToRemove / 10) == 0) {
//                    System.out.println(String.format(
//                            "------------------- Removed %d0%% attributes -------------------",
//                            10 * (removedCounter % (totalCountToRemove / 10))));
//                }
                if (removedCounter % (totalCountToRemove / 10) == 0) {
                    System.out.println(String.format(
                            "------------------- Removed %d%% attributes -------------------",
                            removedCounter / (totalCountToRemove / 10) * 10));
                }
                
                trainDataset = new Instances(trainDatasetBackup);
                testDataset = new Instances(testDatasetBackup);
                // Do not remove for first iteration
                if (removedCounter > 0) {
                    // Selecting features (removing attributes one-by-one starting from worst)
                    int fromIndex = attrIndex - ATTR_COUNT_TO_REMOVE_BY_STEP;
                    List<Integer> attrToRemoveList = attrRankList.subList(fromIndex < 0 ? 0 : fromIndex, totalCountToRemove);
                    Collections.sort(attrToRemoveList);
                    for (int i = attrToRemoveList.size() - 1; i >= 0; i--) {
                        trainDataset.deleteAttributeAt(attrToRemoveList.get(i));
                        testDataset.deleteAttributeAt(attrToRemoveList.get(i));
                    }
//                    System.out.println("\n\n-------------- " + (removedCounter) + " attribute(s) removed (of " +
//                            totalCountToRemove + ") --------------\n");
                } else {
//                    System.out.println("\n\n-------------- First iteration - without selecting features --------------\n");
                }

                classify(classifiers, trainDataset, testDataset);
            }

            bestTrainList.add(bestTrain);
            bestTestList.add(bestTest);

//            System.out.println();
//            System.out.println(bestInfo);
        }

        for (int i = 0; i < bestTrainList.size(); i++) {
            saveDatasets(bestTrainList.get(i), bestTestList.get(i), String.valueOf(i));
        }

        Instances trainInstance = mergeInstances(bestTrainList);
        Instances testInstance = mergeInstances(bestTestList);

        bestResult = 0;
        classify(classifiers, trainInstance, testInstance);
        System.out.println(bestInfo);
    }

    private Instances mergeInstances(List<Instances> instancesList) {
        Instances result = null;
//        for (int i = 0; i < instancesList.size() - 1; i++) {
            Instances first = instancesList.get(0);
            Instances second = instancesList.get(1);

            result = new Instances(first);

            for (int j = second.size() - 1; j >= 0; j--) {
                Instance instance = second.get(j);
                if (!result.contains(instance)) {
                    result.deleteAttributeAt(j);
                }
            }
//        }
        return result;
    }

    private void saveDatasets(Instances trainDataset, Instances testDataset, String postfix) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(trainDataset);
        saver.setFile(new File("./datasetbackup/arcene_train."+postfix+".arff"));
        saver.writeBatch();
        saver.setInstances(testDataset);
        saver.setFile(new File("./datasetbackup/arcene_test."+postfix+".arff"));
        saver.writeBatch();
    }

    private List<List<Integer>> calculateRankedLists(Instances dataset,
                                                     List<FeatureSelectionModel> featureSelectionModels)
            throws Exception {
        List<List<Integer>> listOfRankedLists = new ArrayList<>();

        int modelCounter = 0;
        for (FeatureSelectionModel model : featureSelectionModels) {
            System.out.println(String.format("Feature selection, step #%d. %s.", ++modelCounter, model));
            List<Integer> attrRankList = model.rankAttributes(dataset);
            listOfRankedLists.add(attrRankList);
            System.out.println("Done.\n");
        }

        fileSystemUtil.printRankedLists(featureSelectionModels, listOfRankedLists);

        return listOfRankedLists;
    }

    private List<Classifier> createClassifiers() {
        List<Classifier> classifiers = new ArrayList<>();
        SMO svm = new SMO();

        classifiers.add(svm);

        return classifiers;
    }

    protected void classify(List<Classifier> classifiers, Instances train, Instances test) throws Exception {
        for (int i = 0; i < classifiers.size(); i++) {
            Classifier classifier = classifiers.get(i);
            classifier.buildClassifier(train);

            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(classifier, test);
            if (eval.correct() > bestResult) {
                bestResult = eval.correct();
                bestTest = test;
                bestTrain = train;
                bestInfo = eval.toSummaryString(false);
            }

            classifiers.set(i, classifier.getClass().newInstance());
        }
    }
}

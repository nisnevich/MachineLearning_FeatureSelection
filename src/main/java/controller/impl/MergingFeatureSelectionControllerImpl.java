package controller.impl;

import model.fs.FeatureSelectionModel;
import model.fs.impl.CorellationFeatureSelectionModel;
import model.fs.impl.InfoGainFeatureSelectionModel;
import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class MergingFeatureSelectionControllerImpl extends FeatureSelectionAbstractController
        implements controller.FeatureSelectionController {
    // How many attributes should be removed per
    // How many best attributes of ranked lists should be selected
    private static final double ATTR_SELECT_PORTION = 0.6;

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

        List<FeatureSelectionModel> featureSelectionModels = createFeatureSelectionModelsList();

        List<List<Integer>> listOfRankedLists = calculateRankedLists(trainDataset, featureSelectionModels);
        merge(trainDataset, testDataset, listOfRankedLists);
    }

    public void merge(Instances trainDataset, Instances testDataset, List<List<Integer>> listOfRankedLists) throws Exception {

        List<Classifier> classifiers = createClassifiers();
        // Removing portion of attributes
        {
            int attrCountToSelect = (int) (trainDataset.numAttributes() * ATTR_SELECT_PORTION);
            for (int i = 0; i < listOfRankedLists.size(); i++) {
                List<Integer> rankedList = listOfRankedLists.get(i);
                if (rankedList.size() > attrCountToSelect) {
                    listOfRankedLists.set(i, rankedList.subList(0, attrCountToSelect));
                }
            }
        }

        List<Integer> rankedList = listOfRankedLists.get(0);
        // Merging attributes
        {
            System.out.println("Merging attributes after FS...");
            Iterator<Integer> currentListIterator = rankedList.listIterator();
            while (currentListIterator.hasNext()) {
                Integer currentListValue = currentListIterator.next();

                for (int j = 1; j < listOfRankedLists.size(); j++) {
                    boolean matchFound = false;
                    for (Integer value : listOfRankedLists.get(j)) {
                        if (value.equals(currentListValue)) {
                            matchFound = true;
                            break;
                        }
                    }
                    if (!matchFound) {
                        currentListIterator.remove();
                        break;
                    }
                }
            }
        }

        // Removing attributes
        {
            System.out.println("Removing attributes from dataset...");
            int removedAttrCount = 0;
            for (int attrIndex = trainDataset.numAttributes() - 2; attrIndex >= 0; attrIndex--) {
                if (!rankedList.contains(1 + attrIndex)) {
                    trainDataset.deleteAttributeAt(attrIndex);
                    testDataset.deleteAttributeAt(attrIndex);
                    removedAttrCount++;
                }
            }
            System.out.println("Removed " + removedAttrCount + " attributes.");
        }

        classify(classifiers, trainDataset, testDataset);

        saveDatasets(trainDataset, testDataset);
    }

    private List<FeatureSelectionModel> createFeatureSelectionModelsList() {
        List<FeatureSelectionModel> featureSelectionModels = new ArrayList<>();
        featureSelectionModels.add(new CorellationFeatureSelectionModel());
        featureSelectionModels.add(new InfoGainFeatureSelectionModel());
//        featureSelectionModels.add(new SymmetricalUncertSelectionModel());
//        featureSelectionModels.add(new CfsSubsetFeatureSelectionModel());
//        featureSelectionModels.add(new ReliefFeatureSelectionModel());
//        featureSelectionModels.add(new OneRFeatureSelectionModel());
//        featureSelectionModels.add(new PrincipalComponentsFeatureSelectionModel());
//        featureSelectionModels.add(new WrapperAttributeFeatureSelectionModel());
        return featureSelectionModels;
    }

    private List<Classifier> createClassifiers() {
        List<Classifier> classifiers = new ArrayList<>();

        SMO svm = new SMO();

        classifiers.add(svm);

        return classifiers;
    }

    private List<List<Integer>> calculateRankedLists(Instances dataset,
                                                     List<FeatureSelectionModel> featureSelectionModels)
            throws Exception {
        List<List<Integer>> listOfRankedLists = new ArrayList<>();

        int modelCounter = 0;
        for (FeatureSelectionModel model : featureSelectionModels) {
            System.out.println(String.format("Feature selection, step #%d. %s", ++modelCounter, model));
            List<Integer> rankedList = model.rankAttributes(dataset);
            listOfRankedLists.add(rankedList);
            System.out.println("Done.\n");
        }

        //fileSystemUtil.printRankedLists(featureSelectionModels, listOfRankedLists);
        return listOfRankedLists;
    }

    private void saveDatasets(Instances trainDataset, Instances testDataset) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(trainDataset);
        saver.setFile(new File("./datasetnew/arcene_train.cutten." + ATTR_SELECT_PORTION + ".arff"));
        saver.writeBatch();
        saver.setInstances(testDataset);
        saver.setFile(new File("./datasetnew/arcene_test.cutten." + ATTR_SELECT_PORTION + ".arff"));
        saver.writeBatch();
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

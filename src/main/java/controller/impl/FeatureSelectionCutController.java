package controller.impl;

import controller.FeatureSelectionController;
import model.fs.impl.CorellationFeatureSelectionModel;
import model.fs.impl.WrapperAttributeFeatureSelectionModel;
import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class FeatureSelectionCutController extends FeatureSelectionAbstractController
        implements FeatureSelectionController {

    private static final Double cutRate = 0.5;
    private static final String POSTFIX_DATASET_TYPE = "train";
//        private static final String POSTFIX_DATASET_TYPE = "valid";
    private static final String POSTFIX_FS_TYPE = "corel";
//        private static final String POSTFIX_FS_TYPE = "infogain";
    private static final String MODEL_TYPE = CorellationFeatureSelectionModel.class.getSimpleName();

    private FileSystemUtil fileSystemUtil = new FileSystemUtil();
    private ClassifierUtil classifierUtil = new ClassifierUtil();

    public void start() throws Exception {
        // Reading dataset
//        String datasetPath = "C:\\Program Files\\Weka-3-8\\data\\iris.arff";
        String datasetPath = "dataset\\arcene_" + POSTFIX_DATASET_TYPE + ".arff";
        String rankedListPath = "output\\rankedLists_3attr.txt";

        Instances dataset = new Instances(fileSystemUtil.readDataSet(datasetPath));
        dataset.setClassIndex(dataset.numAttributes() - 1);

        List<Integer> rankedList = fileSystemUtil.readRankedList(rankedListPath, MODEL_TYPE);

        System.out.println("============================================================================");
        System.out.println("Feature selection was done using " + MODEL_TYPE + ". Removing "
                + (MODEL_TYPE.equals(WrapperAttributeFeatureSelectionModel.class.getSimpleName()) ? cutRate : (1-cutRate))
                + " worst attributes of "+POSTFIX_DATASET_TYPE+" dataset.");
        System.out.println("============================================================================");


        int removedAttrCount = 0;
        if (MODEL_TYPE.equals(WrapperAttributeFeatureSelectionModel.class.getSimpleName())) {
            for (int i = dataset.numAttributes() - 2; i >= 0; i--) {
                if (! rankedList.contains(i + 1)) {
                    if (removedAttrCount < dataset.numAttributes() * cutRate) {
                        dataset.deleteAttributeAt(i);
                        removedAttrCount++;
                    }
                }
            }
        } else {
            List<Integer> attrToRemoveList = rankedList.subList((int)(rankedList.size() * cutRate), rankedList.size() - 1);
            Collections.sort(attrToRemoveList);
            for (int i = attrToRemoveList.size() - 1; i >= 0; i--) {
                dataset.deleteAttributeAt(attrToRemoveList.get(i));
                removedAttrCount++;
            }
        }

        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataset);
        saver.setFile(new File("./dataset/arcene_" + POSTFIX_DATASET_TYPE
                + ".cutten." + cutRate + "."+POSTFIX_FS_TYPE+".arff"));
        saver.writeBatch();
    }
}

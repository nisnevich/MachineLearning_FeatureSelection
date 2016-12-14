package controller.impl;

import controller.FeatureSelectionController;
import model.fs.impl.CorellationFeatureSelectionModel;
import model.fs.impl.WrapperAttributeFeatureSelectionModel;
import util.ClassifierUtil;
import util.FileSystemUtil;
import weka.core.Instances;

/**
 * Concats different FS results
 * @author Nisnevich Arseniy
 * @version 1.0 (08.12.2016)
 */
public class FeatureSelectionDoubleController extends FeatureSelectionAbstractController
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
        String corelDatasetPath = "dataset\\arcene_" + POSTFIX_DATASET_TYPE + ".cutten."+cutRate+".corel.arff";
        String infogainDatasetPath = "dataset\\arcene_" + POSTFIX_DATASET_TYPE + ".cutten."+cutRate+".infogain.arff";

        Instances corelDataset = new Instances(fileSystemUtil.readDataSet(corelDatasetPath));
        corelDataset.setClassIndex(corelDataset.numAttributes() - 1);
        Instances infoGainDataset = new Instances(fileSystemUtil.readDataSet(infogainDatasetPath));
        infoGainDataset.setClassIndex(infoGainDataset.numAttributes() - 1);

        System.out.println("============================================================================");
        System.out.println("Feature selection was done using " + MODEL_TYPE + ". Removing "
                + (MODEL_TYPE.equals(WrapperAttributeFeatureSelectionModel.class.getSimpleName()) ? cutRate : (1-cutRate))
                + " worst attributes of "+POSTFIX_DATASET_TYPE+" dataset.");
        System.out.println("============================================================================");
/*
        if (MODEL_TYPE.equals(WrapperAttributeFeatureSelectionModel.class.getSimpleName())) {
            for (int i = 0; i < corelDataset.numAttributes(); i++) {
                if (! rankedList.contains(i + 1)) {
                    if (removedAttrCount < dataset.numAttributes() * cutRate) {
                        dataset.deleteAttributeAt(i);
                        removedAttrCount++;
                    }
                }
            }
        } else {
            Instance instance = corelDataset.instance(0);
            for (int i = 0; i < corelDataset.instance(0).numAttributes(); i++) {
                if ()
            }
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
        saver.writeBatch();*/
    }
}

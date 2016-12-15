package model.fs.impl;

import model.fs.FeatureSelectionModel;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (07.12.2016)
 */
public class WrapperAttributeFeatureSelectionModel implements FeatureSelectionModel {

    private int foldsCount = DEFAULT_FOLDS_COUNT;

    @Override
    public List<Integer> rankAttributes(Instances data) throws Exception {
        J48 classifier = new J48();
        classifier.setConfidenceFactor(0.25f);
        classifier.setMinNumObj(2);

        WrapperSubsetEval evaluator = new WrapperSubsetEval();
        evaluator.setFolds(5);
        evaluator.setSeed(1);
        evaluator.setThreshold(0.01);
        evaluator.setClassifier(classifier);

        BestFirst searchMethod = new BestFirst();
        searchMethod.setLookupCacheSize(1);
        searchMethod.setSearchTermination(5);

        AttributeSelection selector = new AttributeSelection();
        selector.setFolds(foldsCount);
        selector.setEvaluator(evaluator);
        selector.setSearch(searchMethod);
        selector.SelectAttributes(data);

        int[] attrArray = selector.selectedAttributes();
        List<Integer> attrList = new LinkedList<>();
        // Convert to list
        for (int anAttrArray : attrArray) {
            attrList.add(anAttrArray - 1);
        }
        return attrList;
    }


    @Override
    public String toString() {
        return "Using WrapperSubsetEval";
    }

    @Override
    public void setFoldsCount(int foldsCount) {
        this.foldsCount = foldsCount;
    }
}

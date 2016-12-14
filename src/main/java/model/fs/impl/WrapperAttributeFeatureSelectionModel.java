package model.fs.impl;

import model.fs.FeatureSelectionModel;
import weka.attributeSelection.*;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (07.12.2016)
 */
public class WrapperAttributeFeatureSelectionModel implements FeatureSelectionModel {

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
        selector.setFolds(10);
        selector.setEvaluator(evaluator);
        selector.setSearch(searchMethod);
        selector.SelectAttributes(data);

        int[] attrArray = selector.selectedAttributes();
        List<Integer> attrList = new ArrayList<>();
        // Convert to list
        for (int anAttrArray : attrArray) {
            attrList.add(anAttrArray - 1);
        }
        return attrList;
    }


    @Override
    public String toString() {
        return "This feature selection model uses WrapperSubsetEval";
    }
}

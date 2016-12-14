package model.fs.impl;

import model.fs.FeatureSelectionModel;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (07.12.2016)
 */
public class CorellationFeatureSelectionModel implements FeatureSelectionModel {

    @Override
    public List<Integer> rankAttributes(Instances data) throws Exception {
        CorrelationAttributeEval evaluator = new CorrelationAttributeEval();
        evaluator.setDoNotCheckCapabilities(false);
        evaluator.setOutputDetailedInfo(false);

        Ranker searchMethod = new Ranker();
        searchMethod.setNumToSelect(-1);
        searchMethod.setGenerateRanking(true);

        AttributeSelection selector = new AttributeSelection();
        selector.setFolds(10);
        selector.setEvaluator(evaluator);
        selector.setSearch(searchMethod);
        selector.SelectAttributes(data);

        int[] attrArray = selector.selectedAttributes();
        List<Integer> attrList = new ArrayList<>();
        // Removes last element
        for (int i = 0; i < attrArray.length - 1; i++) {
            attrList.add(attrArray[i]);
        }
        return attrList;
    }

    @Override
    public String toString() {
        return "This feature selection model uses CorrelationAttributeEval";
    }
}

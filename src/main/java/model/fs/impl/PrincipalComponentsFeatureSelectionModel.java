package model.fs.impl;

import model.fs.FeatureSelectionModel;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.PrincipalComponents;
import weka.attributeSelection.Ranker;
import weka.core.Instances;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (07.12.2016)
 */
public class PrincipalComponentsFeatureSelectionModel implements FeatureSelectionModel {

    private int foldsCount = DEFAULT_FOLDS_COUNT;

    @Override
    public List<Integer> rankAttributes(Instances data) throws Exception {
        PrincipalComponents evaluator = new PrincipalComponents();

        Ranker searchMethod = new Ranker();
        searchMethod.setNumToSelect(-1);
        searchMethod.setGenerateRanking(true);

        AttributeSelection selector = new AttributeSelection();
        selector.setFolds(foldsCount);
        selector.setEvaluator(evaluator);
        selector.setSearch(searchMethod);
        selector.SelectAttributes(data);

        int[] attrArray = selector.selectedAttributes();
        List<Integer> attrList = new LinkedList<>();
        // Removes last element
        for (int i = 0; i < attrArray.length - 1; i++) {
            attrList.add(attrArray[i]);
        }
        return attrList;
    }

    @Override
    public String toString() {
        return "Using PrincipalComponents";
    }

    @Override
    public void setFoldsCount(int foldsCount) {
        this.foldsCount = foldsCount;
    }
}

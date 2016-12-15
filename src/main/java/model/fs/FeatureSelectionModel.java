package model.fs;

import weka.core.Instances;

import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (07.12.2016)
 */
public interface FeatureSelectionModel {

    int DEFAULT_FOLDS_COUNT = 10;

    List<Integer> rankAttributes(Instances data) throws Exception;

    void setFoldsCount(int foldsCount);
}

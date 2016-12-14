package model.fs;

import weka.core.Instances;

import java.util.List;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (07.12.2016)
 */
public interface FeatureSelectionModel {

    List<Integer> rankAttributes(Instances data) throws Exception;

    String toString();
}

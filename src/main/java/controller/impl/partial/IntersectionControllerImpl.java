package controller.impl.partial;

import util.FileSystemUtil;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (22.12.2016)
 */
public class IntersectionControllerImpl {

    private static final String INPUT_F1_NAME = "datasetbackup/arcene_%s.0.arff";
    private static final String INPUT_F2_NAME = "datasetbackup/arcene_%s.1.arff";
    private static final String INPUT_F3_NAME = "datasetbackup/arcene_%s.2.arff";

    private static final String OUTPUT_FILE_NAME = "datasetmerged/merged.%s.arff";

    private static FileSystemUtil fileSystemUtil = new FileSystemUtil();

    public static void main(String[] args) throws IOException {
        run("train");
        run("test");
    }

    private static void run(String trainOrTest) throws IOException {
        Instances f1 = new Instances(fileSystemUtil.readDataSet(String.format(INPUT_F1_NAME, trainOrTest)));
        f1.setClassIndex(f1.numAttributes() - 1);
        Instances f2 = new Instances(fileSystemUtil.readDataSet(String.format(INPUT_F2_NAME, trainOrTest)));
        f2.setClassIndex(f2.numAttributes() - 1);
        Instances f3 = new Instances(fileSystemUtil.readDataSet(String.format(INPUT_F3_NAME, trainOrTest)));
        f3.setClassIndex(f3.numAttributes() - 1);

        Instances i12 = mergeInstances(f1, f2);
        Instances i13 = mergeInstances(f1, f3);
        Instances i23 = mergeInstances(f2, f3);
        Instances i123 = mergeInstances(i12, f3);

        saveInstances(i12, trainOrTest, "1");
        saveInstances(i13, trainOrTest, "2");
        saveInstances(i23, trainOrTest, "3");
        saveInstances(i123, trainOrTest, "4");
    }

    private static void saveInstances(Instances instances, String trainOrTest, String postfix) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        saver.setFile(new File(String.format(OUTPUT_FILE_NAME, trainOrTest + "." + postfix)));
        saver.writeBatch();
    }

    private static Instances mergeInstances(Instances first, Instances second) {
        Instances result = new Instances(first);

        Enumeration<Attribute> attrs = first.enumerateAttributes();
        int i = 0;
        int removeIndex = 0;
        while (attrs.hasMoreElements()) {
            Attribute attribute = attrs.nextElement();
            // Ignoring class attribute
            if (attribute.isString()) {
                continue;
            }
            if (!containsAttribute(second, attribute)) {
                result.deleteAttributeAt(removeIndex);
            } else {
                removeIndex++;
            }
            i++;
        }

//        attrs = second.enumerateAttributes();
//        i = 0;
//        removeIndex = 0;
//        while (attrs.hasMoreElements()) {
//            Attribute attribute = attrs.nextElement();
//            // Ignoring class attribute
//            if (attribute.isString()) {
//                continue;
//            }
//            if (!containsAttribute(result, attribute)) {
//                result.deleteAttributeAt(removeIndex);
//            } else {
//                removeIndex++;
//            }
//            i++;
//        }
        return result;
    }

    private static boolean containsAttribute(Instances instances, Attribute attribute) {
        Enumeration<Attribute> attrs = instances.enumerateAttributes();
        while (attrs.hasMoreElements()) {
            if (attrs.nextElement().equals(attribute)) {
                return true;
            }
        }
        return false;
    }
}

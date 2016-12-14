package util;

import model.fs.FeatureSelectionModel;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Nisnevich Arseniy
 * @version 1.0 (30.11.2016)
 */
public class FileSystemUtil {

    private static final String OUTPUT_FOLDER_NAME = "output";
    private static final String OUTPUT_FILE_EXTENSION = ".txt";
    private DateFormat outputDateFormatter = new SimpleDateFormat("HHmmss");

    public void printRankedLists(List<FeatureSelectionModel> featureSelectionModels,
                                 List<List<Integer>> listOfRankedLists) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                OUTPUT_FOLDER_NAME + "/" + "rankedLists.txt")));
        for (int i = 0; i < featureSelectionModels.size(); i++) {
            FeatureSelectionModel model = featureSelectionModels.get(i);
            List<Integer> rankedList = listOfRankedLists.get(i);

            writer.println(model.getClass().getSimpleName());

            StringBuilder builder = new StringBuilder();
            for (Integer value : rankedList) {
                builder.append(value + 1).append(" ");
            }

            writer.println(builder.toString());
        }
        writer.close();
    }

    public List readRankedList(String path, String modelName) throws IOException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path)));
        } catch (FileNotFoundException e) {
            System.err.println("Error while reading ranked list from file system. Path: " + path);
            throw e;
        }
        List<Integer> result = new ArrayList<>();
        try {
            while (reader.ready()) {
                String currentName = reader.readLine();
                if (currentName.equals(modelName)) {
                    String[] currentStringValues = reader.readLine().split(" ");
                    int [] currentIntValues = Arrays.stream(currentStringValues).mapToInt(Integer::parseInt).toArray();
                    for (int i = 0; i < currentIntValues.length; i++) {
                        result.add(currentIntValues[i]);
                    }
                    return result;
                } else {
                    // Skip next string
                    reader.readLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error while creating instances from dataset.");
            throw e;
        }
        reader.close();
        return null;
    }

    public Instances readDataSet(String path) throws IOException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path)));
        } catch (FileNotFoundException e) {
            System.err.println("Error while reading dataset from file system. Path: " + path);
            throw e;
        }
        Instances instances;
        try {
            instances = new Instances(reader);
        } catch (IOException e) {
            System.err.println("Error while creating instances from dataset.");
            throw e;
        }
        reader.close();
        return instances;
    }

    public void printClassificationResults(AbstractClassifier classifier) throws IOException {
//        for (int i = 0; i < test.numInstances(); i++) {
//            double pred = fc.classifyInstance(test.instance(i));
//            System.out.print("ID: " + test.instance(i).value(0));
//            System.out.print(", actual: " + test.classAttribute().value((int) test.instance(i).classValue()));
//            System.out.println(", predicted: " + test.classAttribute().value((int) pred));
//        }

        try {
            String fileName = OUTPUT_FOLDER_NAME + "/" + classifier.getClass().getSimpleName().toLowerCase()
                    + "_" + outputDateFormatter.format(new Date()) + OUTPUT_FILE_EXTENSION;

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            objectOutputStream.writeObject(classifier);
        } catch (IOException e) {
            System.err.println("Error while printing classification results for classifier \""
                    + classifier.getClass().getSimpleName() + "\".");
            throw e;
        }
    }
}

package util;

import java.io.*;

/**
 * Converts dataset to Weka comma-separated format
 * @author Nisnevich Arseniy
 * @version 1.0 (07.12.2016)
 */
public class DatasetConvertionUtil {
    public static void main(String[] args) throws IOException {
        BufferedReader datasetReader = new BufferedReader(new InputStreamReader(new FileInputStream("dataset/arcene_valid.data")));
        BufferedReader labelsReader = new BufferedReader(new InputStreamReader(new FileInputStream("dataset/arcene_valid.labels")));

        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream("dataset/arcene_valid.arff")));
        printWriter.println("@RELATION Weka");
        for (int i = 0; i < 10000; i++) {
            printWriter.println("@ATTRIBUTE a" + i + " REAL");
        }
        printWriter.println("@ATTRIBUTE class {1,-1}");
        printWriter.println("@DATA");

        while (labelsReader.ready()) {
            String line = datasetReader.readLine() + labelsReader.readLine();
            printWriter.println(line.replaceAll(" ", ","));
        }
        printWriter.flush();
    }
}

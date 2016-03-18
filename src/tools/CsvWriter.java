/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control * 
 */
package tools;

import collections.PeptideCollection;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import objects.Peptide;

/**
 * Writes a csv file with peptide scan data.
 * @author vnijenhuis
 */
public class CsvWriter {
    /**
     * Generates the csv file and writes data to this file.
     * @param peptideMatrix set of peptide arrays.
     * @param outputPath output path and file name.
     * @param method name of the mass spec method (1D25, 1D50, 2DLC etc.).
     * @param samples set of sample names.
     * @param sampleSize amount of samples per sample type.
     * @throws IOException 
     */
    public void generateCsvFile(final HashSet<ArrayList<String>> peptideMatrix, final String outputPath,
            final String method, final ArrayList<String> samples, final Integer sampleSize) throws IOException {
        //Create a new FileWriter instance.
        try (FileWriter writer = new FileWriter(outputPath)) {
            String delimiter = ",";
            String lineEnding = "\n";
            System.out.println("Writing data to text file " + outputPath);
            //Writes values to the header, line separator="," and line ending="\n"
            String header = createCsvHeader(samples, method, delimiter, lineEnding, sampleSize);
            writer.append(header);
            //Create array list row.
            for (ArrayList<String> peptide: peptideMatrix) {
                String row = createPeptideRow(peptide, delimiter, lineEnding);
                writer.append(row);
            }
//            for (Peptide peptide : peptideMatrix.getPeptides()) {
//                String row = createPeptideRow(peptide, delimiter, lineEnding);
//                writer.append(row);
//            }
            writer.flush();
            writer.close();
            System.out.println("Finished writing to " + outputPath);
        }
    }

    /**
     * Creates a header for the csv file.
     * @param samples set of samples.
     * @param method name of the method.
     * @param delimiter line separator for csv file.
     * @param lineEnding line ending for csv file.
     * @param sampleSize amount of samples per sample type.
     * @return returns a header for the csv file.
     */
    public final String createCsvHeader(final ArrayList<String> samples, final String method,
            final String delimiter, final String lineEnding, final Integer sampleSize) {
        String header = "";
        header += "Sequence,";
        header += "Dataset,";
//        header += "Sample,";
//        header += "Uniprot Scan ID,";
//        header += method + " Scan ID,";
//        header += "Uniprot Score,";
//        header += method + " Score";
        for (String sample: samples) {
            for (int i = 1; i <= sampleSize; i++) {
                header += sample + i + " Scan ID" + delimiter;
            }
        }
        for (String sample: samples) {
            for (int i = 1; i <= sampleSize; i++) {
                if (sample.equals(samples.get(samples.size()-1)) && i == sampleSize) {
                    header += sample + i + " -10lgP" + lineEnding;
                } else {
                    header += sample + i + " -10lgP" + delimiter;
                }
            }
        }
        return header;
    }

    /**
     * Creates a row with peptide data.
     * @param peptide peptide array with peptide data.
     * @param separator line separator for csv file.
     * @param lineEnding line ending for csv file.
     * @return returns a row with peptide data.
     */
    private String createPeptideRow(ArrayList<String> peptide, String separator, String lineEnding) {
        String row = "";
        //Adds all data to the row.
        row += peptide.get(0) + separator;
        row += peptide.get(1) + separator;     
        for (int i = 2; i < peptide.size(); i++) {
            if (i == peptide.size() - 1) {
                row += peptide.get(i) + lineEnding;
            } else {
                row += peptide.get(i) + separator;
            }
        }
        return row;
    }
}

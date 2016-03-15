/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control * 
 */
package tools;

import collections.PeptideCollection;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
     * @param samples set of samples.
     * @param sampleSize sample size.
     * @throws IOException 
     */
    public void generateCsvFile(PeptideCollection peptideMatrix, final String outputPath,
            final ArrayList<String> samples, final Integer sampleSize) throws IOException {
        //Create a new FileWriter instance.
        try (FileWriter writer = new FileWriter(outputPath)) {
            String delimiter = ",";
            String lineEnding = "\n";
            System.out.println("Writing data to text file " + outputPath);
            //Writes values to the header, line separator="," and line ending="\n"
            String header = createCsvHeader(samples, sampleSize, delimiter, lineEnding);
            writer.append(header);
            //Create array list row.
            for (Peptide peptide : peptideMatrix.getPeptides()) {
                String row = createPeptideRow(peptide, delimiter, lineEnding);
                writer.append(row);
            }
            writer.flush();
            writer.close();
            System.out.println("Finished writing to " + outputPath);
        }
    }

    /**
     * Creates a header for the csv file.
     * @param samples set of samples.
     * @param sampleSize sample size.
     * @param delimiter line separator for csv file.
     * @param lineEnding line ending for csv file.
     * @return returns a header for the csv file.
     */
    public final String createCsvHeader(final ArrayList<String> samples, final Integer sampleSize,
            final String delimiter, final String lineEnding) {
        String header = "";
        header += "Sequence,";
        header += "Dataset,";
        header += "Sample,";
        header += "Scan ID,";
//        header += "Uniprot Scan ID,";
        header += "Score";
//        for (String sample: samples) {
//            for (int i = 1; i <= sampleSize; i++) {
//                if (sample.equals(samples.get(samples.size()-1)) && i == sampleSize) {
//                    header += sample + i;
//                } else {
//                    header += sample + i + delimiter;
//                }
//            }
//        }
        header += lineEnding;
        return header;
    }

    /**
     * Creates a row with peptide data.
     * @param peptide peptide array with peptide data.
     * @param separator line separator for csv file.
     * @param lineEnding line ending for csv file.
     * @return returns a row with peptide data.
     */
    private String createPeptideRow(Peptide peptide, String separator, String lineEnding) {
        String row = "";
        //Adds all data to the row.
        row += peptide.getSequence() + separator;
        row += peptide.getDataset()+ separator;
        row += peptide.getSample() + separator;
        row += peptide.getScan() + separator;
//        row += peptide.getMatchedScan() + separator;
        row += peptide.getScore() + lineEnding;       
//        for (int i = 0; i < peptide.size(); i++) {
//            if (i == peptide.size() - 1) {
//                row += peptide.get(i) + lineEnding;
//            } else {
//                row += peptide.get(i) + separator;
//            }
//        }
        return row;
    }
}

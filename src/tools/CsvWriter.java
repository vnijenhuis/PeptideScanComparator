/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control * 
 */
package tools;

import collections.ScanIDCollection;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import objects.ScanID;

/**
 * Writes a csv file with peptide scan data.
 * @author vnijenhuis
 */
public class CsvWriter {
    /**
     * Generates the csv file and writes data to this file.
     * @param finalScans set of peptide arrays.
     * @param outputPath output path and file name.
     * @param samples set of sample names.
     * @param sampleSize amount of samples per sample type.
     * @throws IOException 
     */
    public void generateCsvFile(final ScanIDCollection finalScans, final String outputPath,
            final ArrayList<String> samples, final Integer sampleSize) throws IOException {
        //Create a new FileWriter instance.
        try (FileWriter writer = new FileWriter(outputPath)) {
            String delimiter = ",";
            String lineEnding = "\n";
            System.out.println("Writing data to text file " + outputPath);
            //Writes values to the header, line separator="," and line ending="\n"
            String header = createCsvHeader(samples, delimiter, lineEnding, sampleSize);
            writer.append(header);
            //Create array list row.
            for (ScanID scanData: finalScans.getScanIDs()) {
                String row = createPeptideRow(scanData, delimiter, lineEnding);
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
     * @param delimiter line separator for csv file.
     * @param lineEnding line ending for csv file.
     * @param sampleSize amount of samples per sample type.
     * @return returns a header for the csv file.
     */
    public final String createCsvHeader(final ArrayList<String> samples, final String delimiter,
            final String lineEnding, final Integer sampleSize) {
        String header = "";
        header += "Scan ID" + delimiter;
        header += "Dataset" + delimiter;
        header += "Uniprot PSM sequences" + delimiter;
        header += "Combined PSM sequences" + delimiter;
        header += "Individual PSM sequences" + delimiter;
        header += "Uniprot PSM -10lgP" + delimiter;
        header += "Combined PSM -10lgP" + delimiter;
        header += "Individual PSM -10lgP" + lineEnding;
        return header;
    }

    /**
     * Creates a row with peptide data.
     * @param peptide peptide array with peptide data.
     * @param separator line separator for csv file.
     * @param lineEnding line ending for csv file.
     * @return returns a row with peptide data.
     */
    private String createPeptideRow(ScanID scanData, String separator, String lineEnding) {
        String row = "";
        //Adds all data to the row.  
        row += scanData.getScanID() + separator;
        row += scanData.getMethod() + separator;
        for (int i = 0; i < scanData.getUniprotSequences().size(); i++) {
            if (scanData.getUniprotSequences().isEmpty() == true) {
                row += "NA" + separator;
            } else  if (i == 0) {
                row += scanData.getUniprotSequences().get(i);
            } else if (i < scanData.getUniprotSequences().size()) {
                row += "|" + scanData.getUniprotSequences().get(i);
            }
        }
        row += separator;
        for (int i = 0; i < scanData.getCombinedSequences().size(); i++) {
            if (scanData.getCombinedSequences().isEmpty() == true) {
                row += "NA" + separator;
            } else if (i == 0) {
                row += scanData.getCombinedSequences().get(i);
            } else if (i < scanData.getCombinedSequences().size()) {
                row += "|" + scanData.getCombinedSequences().get(i);
            }
        }
        row += separator;
        for (int i = 0; i < scanData.getIndividualSequences().size(); i++) {
            if (scanData.getIndividualSequences().isEmpty() == true) {
                row += "NA" + separator;
            } else if (i == 0) {
                row += scanData.getIndividualSequences().get(i);
            } else if (i < scanData.getIndividualSequences().size()) {
                row += "|" + scanData.getIndividualSequences().get(i);
            }
        }
        row += separator;
        for (int i = 0; i < scanData.getUniprotScores().size(); i++) {
            if (scanData.getUniprotSequences().isEmpty() == true) {
                row += "0.0" + separator;
            } else if (i == 0) {
                row += scanData.getUniprotScores().get(i);
            } else if (i < scanData.getUniprotScores().size()) {
                row += "|" + scanData.getUniprotScores().get(i);
            }
        }
        row += separator;
        for (int i = 0; i < scanData.getCombinedScores().size(); i++) {
            if (scanData.getCombinedSequences().isEmpty() == true) {
                row += "0.0" + separator;
            } else if (i == 0) {
                row += scanData.getCombinedScores().get(i);
            } else if (i < scanData.getCombinedScores().size()) {
                row += "|" + scanData.getCombinedScores().get(i);
            }
        }
        row += separator;
        for (int i = 0; i < scanData.getIndividualScores().size(); i++) {
            if (scanData.getIndividualSequences().isEmpty() == true) {
                row += "0.0" + separator;
            } else if (i == 0) {
                row += scanData.getIndividualScores().get(i);
            } else if (i < scanData.getIndividualScores().size()) {
                row += "|" + scanData.getIndividualScores().get(i);
            }
        }
        row += lineEnding;
        return row;
    }
}

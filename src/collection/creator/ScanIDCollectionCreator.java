/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package collection.creator;

import collections.ScanIDCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import objects.ScanID;

/**
 * Read the text file(s) and save entries inside a scan ID collection.
 *
 * @author vnijenhuis
 */
public class ScanIDCollectionCreator {

    /**
     * Creates a collection of scan ID objects.
     *
     * @param peptideFiles a list of peptide data files.
     * @param dataset name of the dataset.
     * @param method name of the ms method that was used.
     * @param datasets list of all dataset names.
     * @param sampleList list of sample names
     * @return Collection of peptide objects.
     * @throws FileNotFoundException could not find the specified file.
     * @throws IOException could not find/open the specified file.
     */
    public final ScanIDCollection createScanCollection(final ArrayList<String> peptideFiles, final String dataset,
            final String method, final ArrayList<String> datasets, final ArrayList<String> sampleList) throws FileNotFoundException, IOException {
        // Read the file
        ScanIDCollection scanCollection = new ScanIDCollection();
        for (String file : peptideFiles.subList(0, 3)) {
            //Pattern to split the path into folders.
            String pattern = Pattern.quote(File.separator);
            String[] folders = file.split(pattern);
            String sample = "";
            //Creates the dataset and sample names.
            for (String folder : folders) {
                //Match sample names.
                String expression = "^(" + sampleList.get(0).toLowerCase() + "|" + sampleList.get(1).toLowerCase() + ")_?\\d{1,}$";
                if (folder.toLowerCase().matches(expression)) {
                    sample = folder;
                }
            }
            System.out.println("Collecting peptides from " + sample + " " + method + " " + dataset + "...");
            FileReader fr = new FileReader(file);
            BufferedReader bffFr = new BufferedReader(fr);
            String line;
            int accessionIndex = 0;
            int sequenceIndex = 0;
            int scanIndex = 0;
            int scoreIndex = 0;
            boolean firstLine = true;
            int count = 0;
            //Reads each line in the given file.
            while ((line = bffFr.readLine()) != null) {
                count++;
                if (firstLine) {
                    String[] data = line.split(",");
                    for (int i = 0; i < data.length; i++) {
                        //Peptide sequence index.
                        if (data[i].toLowerCase().equals("peptide")) {
                            sequenceIndex = i;
                        } //Accession ID index.
                        else if (data[i].toLowerCase().contains("accession")) {
                            accessionIndex = i;
                        } //Scan ID index.
                        else if (data[i].toLowerCase().contains("scan")) {
                            scanIndex = i;
                        } //Score index.
                        else if (data[i].toLowerCase().contains("-10lgp")) {
                            scoreIndex = i;
                        }
                    }
                    firstLine = false;
                    line = bffFr.readLine();
                }
                //Splits the data of each line on comma.
                String[] data = line.split(",");
                String scan = data[scanIndex];
                String score = data[scoreIndex];
                ArrayList<String> accessions = new ArrayList<>();
                //Checks if accession index is possible to grab. (Analysis can provide empty accession ID's for some reason)
                if (data.length > accessionIndex) {
                    String accessionData = data[accessionIndex];
                    //Splits the accessions names if possible.
                    if (accessionData.contains(":")) {
                        accessions.addAll(Arrays.asList(accessionData.split(":")));
                    } else {
                        accessions.add(accessionData);
                    }
                }
                for (String accession : accessions) {
                    //Skip decoy sequences.
                    if (!accession.toUpperCase().contains("DECOY")) {
                        String sequence = data[sequenceIndex];
                        //Can remove (+15.99) and similar matches from a peptide sequence.
                        //                sequence = sequence.replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
                        Boolean newScan = true;
                        ScanID peptide = new ScanID(method, scan, sequence, score, sample, dataset, datasets);
                        //Create new ScanID objects.
                        if (!scanCollection.getScanIDs().isEmpty()) {
                            for (ScanID entry : scanCollection.getScanIDs()) {
                                if (entry.getScanID().equals(scan)) {
                                    newScan = false;
                                    //Add sequences to uniprot list.
                                    if (dataset.equals(datasets.get(0)) && !entry.getUniprotSequences().contains(sequence)) {
                                        entry.addUniprotSequence(sequence);
                                        entry.addUniprotScore(score);
                                    //Add sequences to combined list.
                                    } else if (dataset.equals(datasets.get(1)) && !entry.getCombinedSequences().contains(sequence)) {
                                        entry.addCombinedSequence(sequence);
                                        entry.addCombinedScore(score);
                                    //Add sequences to individual list.
                                    } else if (dataset.equals(datasets.get(2)) && !entry.getIndividualSequences().contains(sequence)) {
                                        entry.addIndividualSequence(sequence);
                                        entry.addIndividualScore(score);
                                    }
                                    break;
                                }
                            }
                            //Add new ScanIDs.
                            if (newScan) {
                                scanCollection.addScanID(peptide);
                            }
                        } else {
                            scanCollection.addScanID(peptide);
                        }
                    }
                }
                if (count % 1000 == 0) {
                    System.out.println("Collected data from " + count + " scan IDs!");
                }
            }
            //Returns the collection of Scan ID objects.
            System.out.println("Collected " + count + " scan IDs from "
                    + sample + " " + method + " " + dataset + "!");
        }
        return scanCollection;
    }
}

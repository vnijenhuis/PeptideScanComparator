/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package collection.creator;

import collections.PeptideCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import objects.Peptide;

/**
 * Read the text file(s) and save them inside a peptide collection.
 * @author vnijenhuis
 */
public class PeptideCollectionCreator {
    /**
     * Creates a collection of peptide objects.
     * @param file loads a DB search psm.csv file.
     * @param dataset
     * @param sampleList
     * @return Collection of peptide objects.
     * @throws FileNotFoundException could not find the specified file.
     * @throws IOException could not find/open the specified file.
     */
    public final PeptideCollection createPeptideCollection(final String file, final String dataset,
            final ArrayList<String> sampleList) throws FileNotFoundException, IOException {
        // Read the file
        PeptideCollection peptides = new PeptideCollection();
        String pattern = Pattern.quote(File.separator);
        String[] path = file.split(pattern);
        String sample = "";
        //Creates the dataset and sample names.
        for (String folder : path) {
            //Match sample names.
            String expression = "^(" + sampleList.get(0).toLowerCase() + "|" + sampleList.get(1).toLowerCase() + ")_?\\d{1,}$";
            if (folder.toLowerCase().matches(expression)) {
                sample = folder;
            }
        }
        System.out.println("Collecting peptides from " + sample + " " + dataset + "...");
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        int accessionIndex = 0;
        int sequenceIndex = 0;
        int scanIndex = 0;
        int scoreIndex = 0;
        boolean firstLine = true;
        //Reads each line in the given file.
        while ((line = bffFr.readLine()) != null) {
            if (firstLine) {
                String[] data = line.split(",");
                for (int i = 0; i < data.length; i++) {
                    //Peptide sequence index.
                    if (data[i].toLowerCase().equals("peptide")) {
                        sequenceIndex = i;
                    } 
                    //Accession ID index.
                    else if (data[i].toLowerCase().contains("accession")) {
                        accessionIndex = i; 
                    } 
                    //Scan ID index.
                    else if (data[i].toLowerCase().contains("scan")) {
                        scanIndex = i;
                    }
                    //Score index.
                    else if (data[i].toLowerCase().contains("-10lgp")) {
                        scoreIndex = i;
                    }
                }
                firstLine = false;
                line = bffFr.readLine();
            }
            String[] data = line.split(",");
            String scan = data[scanIndex];
            String accessionData = "";
            String score = data[scoreIndex];
            ArrayList<String> accessions = new ArrayList<>();
            //Checks if accession index is possible to grab. (Analysis can provide empty accession ID's for some reason)
            if (data.length >  accessionIndex) {
                accessionData = data[accessionIndex];
                //Splits the accessions names if possible.
                if (accessionData.contains(":")) {
                    accessions.addAll(Arrays.asList(accessionData.split(":")));
                } else {
                    accessions.add(accessionData);
                }
            }
            for (String accession: accessions) {
                //Skip decoy sequences.
                if (!accession.toUpperCase().contains("DECOY")) {
                    String sequence = data[sequenceIndex];
                    //Can remove (+15.99) and similar matches from a peptide sequence.
    //                sequence = sequence.replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
                    Peptide peptide = new Peptide(sequence, scan, score, dataset, sample);
                    Boolean newPeptide = true;
                    //Create new peptide objects.
                    if (!peptides.getPeptides().isEmpty()) {
                        for (Peptide p: peptides.getPeptides()) {
                            if (p.getSequence().equals(sequence)) {
                                newPeptide = false;
                                p.addScan(scan);
                                p.addScore(score);
                                break;
                            }
                        }
                        if (newPeptide) {
                            peptides.addPeptide(peptide);
                        }
                    } else {
                        peptides.addPeptide(peptide);
                    }
                }
            }
        }
        //Returns the collection of peptide sequences.
        System.out.println("Collected " + peptides.getPeptides().size() + " unique peptide sequences from " 
                + sample + " " + dataset + "!");
        return peptides;
    }
}

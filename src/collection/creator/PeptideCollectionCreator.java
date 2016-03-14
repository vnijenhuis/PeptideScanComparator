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
     * @return Collection of peptide objects.
     * @throws FileNotFoundException could not find the specified file.
     * @throws IOException could not find/open the specified file.
     */
    public final PeptideCollection createCollection(final String file) throws FileNotFoundException, IOException {
        // Read the file
        PeptideCollection peptides = new PeptideCollection();
        String pattern = Pattern.quote(File.separator);
        String[] path = file.split(pattern);
        String sample = "";
        String dataset = "";
        //Creates the dataset and sample names.
        for (String folder : path) {
            //Match sample names.
            if (folder.toLowerCase().matches("^(copd|healthy|control)_?\\d{1,}$")) {
                sample = folder;
                //Match dataset names.
            } else if (folder.toUpperCase().matches("^(1D25CM|1D50CM|2DLCMSMS)$")) {
                dataset = folder;
            }
        }
        System.out.println("Collecting peptides from " + sample + " " + dataset + "...");
        FileReader fr = new FileReader(file);
        BufferedReader bffFr = new BufferedReader(fr);
        String line;
        int accessionIndex = 0;
        int peptideIndex = 0;
        int scanIndex = 0;
        int scoreIndex = 0;
        boolean firstLine = true;
        //Reads each line in the given file.
        while ((line = bffFr.readLine()) != null) {
            if (firstLine) {
                String[] data = line.split(",");
                for (int i = 0; i < data.length; i++) {
                    if (data[i].toLowerCase().equals("peptide")) {
                        peptideIndex = i;
                    } 
                    else if (data[i].toLowerCase().contains("accession")) {
                        accessionIndex = i; 
                    } 
                    else if (data[i].toLowerCase().contains("scan")) {
                        scanIndex = i;
                    }
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
            //Checks if accession index is possible to grab. (empty columns reduce data.length)
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
                if (!accession.toUpperCase().matches("^ENST[0-9]+$") && !accession.toUpperCase().contains("DECOY")) {
                    String sequence = data[peptideIndex];
                    //Can remove (+15.99) and similar matches from a peptide sequence.
    //                sequence = sequence.replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
                    Peptide peptide = new Peptide(sequence, scan, score, dataset, sample);
                    Boolean newPeptide = true;
                    //Create new peptide objects.
                    if (!peptides.getPeptides().isEmpty()) {
                        for (Peptide p: peptides.getPeptides()) {
                            if (p.getSequence().equals(sequence)) {
                                newPeptide = false;
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
        System.out.println("Collected " + peptides.getPeptides().size() + " unique peptides from " + sample + " " + dataset + "!");
        return peptides;
    }
}

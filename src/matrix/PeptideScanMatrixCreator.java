/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control * 
 */
package matrix;

import collections.PeptideCollection;
import java.util.ArrayList;
import java.util.HashSet;
import objects.Peptide;

/**
 * Creates a matrix of peptide values.
 * @author vnijenhuis
 */
public class PeptideScanMatrixCreator {
    /**
     * Creates a HashSet which contains arrays of peptide data.
     * @param peptides collection of peptide objects.
     * @param sampleList list of sample names.
     * @param sampleSize total amount of samples.
     * @return HashSet with arrays containing peptide sequence, dataset name and indices for each sample.
     */
    public HashSet<ArrayList<String>> createScanMatrix(final PeptideCollection peptides,
            final ArrayList<String> sampleList, final Integer sampleSize) {
        System.out.println("Creating lists to store protein-peptide data...");
        ArrayList<String> newEntry;
        HashSet<ArrayList<String>> peptideMatrix = new HashSet<>();
        //Creates a index for each sample which is added to the newEntry array list.
        //Doing this step once saves a lot of processing time.
        ArrayList<String> sampleEntries = new ArrayList<>();
        for (String sample: sampleList) {
            for (int i = 0; i < sampleSize; i++) {
                sampleEntries.add("-");
            }
        }
        //Create list for each unique peptide sequence.
        for (Peptide peptide: peptides.getPeptides()) {
            newEntry = new ArrayList<>();
            newEntry = createNewEntry(newEntry, peptide, sampleEntries);
            boolean newArray = true;
            if (!peptideMatrix.isEmpty()) {
                //Check if entry is new.
                for (ArrayList<String> entry: peptideMatrix) {
                    String sequence = entry.get(0);
                    if (peptide.getSequence().equals(sequence)) {
                        newArray = false;
                    }
                }
                //Add new entry to the hashset.
                if (newArray) {
                    peptideMatrix.add(newEntry);
                }
                //Add first array to the hashset.
            } else {
                peptideMatrix.add(newEntry);
            }
        }
        return peptideMatrix;
    }

    /**
     * Creates a new array entry with peptide data.
     * @param newEntry empty array entry.
     * @param peptide peptide object with peptide data.
     * @param samples list of sample names.
     * @param sampleSize size of the sample set.
     * @return 
     */
    private ArrayList<String> createNewEntry(ArrayList<String> newEntry, final Peptide peptide,
            final ArrayList<String> sampleEntries) {
        //Adds peptide sequence to the entry.
        newEntry.add(peptide.getSequence());
        //Adds the first dataset name for this peptide sequence
        newEntry.add(peptide.getDataset());
        //Adds indices for each sample to the array.
        newEntry.addAll(sampleEntries);
        //Returns the new Array.
        return newEntry;
    }
}

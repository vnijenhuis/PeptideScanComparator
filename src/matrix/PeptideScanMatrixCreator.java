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
     * 
     * @param peptides 
     * @param samples 
     * @param sampleSize 
     * @return  
     */
    public HashSet<ArrayList<String>> createScanMatrix(final PeptideCollection peptides, final ArrayList<String> samples, 
            final Integer sampleSize) {
        System.out.println("Creating lists to store protein-peptide data...");
        ArrayList<String> newEntry;
        HashSet<ArrayList<String>> peptideMatrix = new HashSet<>();
        for (Peptide peptide: peptides.getPeptides()) {
            newEntry = new ArrayList<>();
            newEntry = createNewEntry(newEntry, peptide, samples, sampleSize);
            boolean newArray = true;
            if (!peptideMatrix.isEmpty()) {
                for (ArrayList<String> entry: peptideMatrix) {
                    String sequence = entry.get(0);
                    if (peptide.getSequence().equals(sequence)) {
                        newArray = false;
                    }
                }
                //Add new array to the hashset.
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
            final ArrayList<String> samples, final Integer sampleSize) {
        newEntry.add(peptide.getSequence());
        newEntry.add(peptide.getDataset());
        for (String sample: samples) {
            for (int i = 0; i < sampleSize; i++) {
                newEntry.add("-");
            }
        }
        return newEntry;
    }
}

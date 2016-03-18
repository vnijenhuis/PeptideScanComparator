/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control * 
 */
package matrix;

import collections.PeptideCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import objects.Peptide;

/**
 * Adds scan values to the matrix.
 * @author vnijenhuis
 */
public class ScanValueSetter {
    /**
     * Adds values to the array.
     * @param peptides collection of peptide objects.
     * @param peptideMatrix set of peptide arrays where the data will be added to.
     * @param sampleList list of the sample names.
     * @param sampleSize amount of indices for each sample type.
     * @return  
     */
    public HashSet<ArrayList<String>> addArrayValues(final PeptideCollection peptides,
            HashSet<ArrayList<String>> peptideMatrix, final ArrayList<String> sampleList, final Integer sampleSize) {
        int count = 0;
        System.out.println("Adding " + peptideMatrix.size() + " peptides to a matrix. This may take a few minutes...");
        for (ArrayList<String> array: peptideMatrix) {
            count +=1;
            //Set integers. Dataset size can vary and is used to deterime parameter positions.
            String sequence = array.get(0);
            for (Peptide peptide: peptides.getPeptides()) {
                if (peptide.getSequence().equals(sequence)) {
                    array = setScanValues(array, peptide, sampleSize, sampleList);
                    array = setScoreValues(array, peptide, sampleSize, sampleList);
                    array = setDatasetValues(array, peptide);
                }
            }
            if (count % 1000 == 0) {
                System.out.println("Processed " + count + " peptides");
            }
        }
        return peptideMatrix;
    }

    /**
     * Adds Scan parameter values to the array.
     * @param array array with peptide data.
     * @param peptide peptide object.
     * @param sampleList list of samples with index 0 as control, index 1 as target.
     * @return array with added scan values.
     */
    private ArrayList<String> setScanValues(ArrayList<String> array, final Peptide peptide, final Integer sampleSize,
            final ArrayList<String> sampleList) {
        Integer scanIndex = 0;
        Integer startIndex = 1;
        String sample = peptide.getSample();
        if (sample.contains(sampleList.get(0))) {
            scanIndex = Integer.parseInt(sample.substring(sampleList.get(0).length())) + startIndex;
        } else if (sample.contains(sampleList.get(1))) {
            scanIndex = Integer.parseInt(sample.substring(sampleList.get(1).length())) + startIndex + sampleSize;
        }
        //Sets count values to the count index.
        array.set(scanIndex, peptide.getNonUniprotScan());
        return array;
    }

    /**
     * Sets the dataset value to the array.
     * @param array array with peptide data.
     * @param peptide peptide object.
     * @return array with added dataset name.
     */
    private ArrayList<String> setDatasetValues(ArrayList<String> array, Peptide peptide) {
        if (!array.get(1).contains(peptide.getDataset())) {
            array.set(1, array.get(1) + "|" + peptide.getDataset());
        }
        return array;
    }

    /**
     * Adds score values to the matrix.
     * @param array array with peptide data.
     * @param peptide peptide that has a matching sequence with the array.
     * @param sampleList list of samples with index 0 as control, index 1 as target.
     * @param sampleSize amount of indices for each sample type.
     * @return array with added score values.
     */
    private ArrayList<String> setScoreValues(ArrayList<String> array, Peptide peptide, final Integer sampleSize,
            final ArrayList<String> sampleList) {
        Integer scoreIndex = 0;
        Integer startIndex = 1;
        String sample = peptide.getSample();
        if (sample.contains(sampleList.get(0))) {
            scoreIndex = Integer.parseInt(sample.substring(sampleList.get(0).length())) + startIndex + (sampleSize * 2);
        } else if (sample.contains(sampleList.get(1))) {
            scoreIndex = Integer.parseInt(sample.substring(sampleList.get(1).length())) + startIndex + (sampleSize * 3);
        }
        //Sets count values to the count index.
        array.set(scoreIndex, peptide.getNonUniprotScore());
        return array;
    }
}

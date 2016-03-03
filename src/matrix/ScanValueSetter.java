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
     * @param peptides 
     * @param peptideMatrix 
     * @param sampleList 
     * @param datasetNumbers 
     * @param sampleSize 
     * @return  
     */
    public HashSet<ArrayList<String>> addArrayValues(final PeptideCollection peptides,
            HashSet<ArrayList<String>> peptideMatrix, final ArrayList<String> sampleList,
            final HashMap<String, Integer> datasetNumbers, final Integer sampleSize) {
        int count = 0;
        System.out.println("Adding " + peptideMatrix.size() + " peptides to a matrix. This can take up to serveral hours...");
        for (ArrayList<String> array: peptideMatrix) {
            count +=1;
            //Set integers. Dataset size can vary and is used to deterime parameter positions.
            String sequence = array.get(0);
            for (Peptide peptide: peptides.getPeptides()) {
                if (peptide.getSequence().equals(sequence)) {
                    array = setScanValues(array, peptide, datasetNumbers, sampleSize, sampleList);
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
     * @param datasetNumbers HashMap containing datasetname to number conversion.
     * @param sampleList list of samples with index 0 as control, index 1 as target.
     * @return array with added scan values.
     */
    private ArrayList<String> setScanValues(ArrayList<String> array, final Peptide peptide,
            final HashMap<String, Integer> datasetNumbers, final Integer sampleSize, final ArrayList<String> sampleList) {
        Integer scanIndex = 0;
        Integer datasetIndex = 1;
        String sample = peptide.getSample();
        if (sample.contains(sampleList.get(1))) {
            scanIndex = Integer.parseInt(sample.substring(4)) + datasetIndex + sampleSize;
        } else if (sample.contains(sampleList.get(0))) {
            scanIndex = Integer.parseInt(sample.substring(7)) + datasetIndex;
        }
        for (Map.Entry<String, Integer> entry: datasetNumbers.entrySet()) {
            if (entry.getKey().equals(peptide.getDataset())) {
                //Sets count values to the count index.
                if (array.get(scanIndex).equals("-")) {
                    array.set(scanIndex, entry.getValue() + ";" + peptide.getScan());
                } else {
                    array.set(scanIndex, array.get(scanIndex) + " " + entry.getValue() + ";" + peptide.getScan());
                }
            }
        }
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
}

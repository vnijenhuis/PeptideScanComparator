/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control * 
 */
package matrix;

import collections.PeptideCollection;
import java.util.ArrayList;
import java.util.Arrays;
import objects.Peptide;

/**
 * Compares Scan ID's between two peptide collections.
 * @author vnijenhuis
 */
public class ScanIDComparator {
    /**
     * Compares scan ID's between two peptide object collections.
     * @param targetPeptides peptide collection one.
     * @param peptides peptide collection two.
     * @return peptides
     */
    public final PeptideCollection compareScanIDs(final PeptideCollection targetPeptides, final PeptideCollection peptides) {
        int count = 0;
        for (Peptide targetPeptide: targetPeptides.getPeptides()) {
            for (Peptide peptide: peptides.getPeptides()) {
                String nonMatchedScans = "";
                String matchedScans = "";
                ArrayList<String> scanList = new ArrayList<>();
                String targetScan = targetPeptide.getScan();
                String scan = peptide.getScan();
                if (scan.contains("|")) {
                    String[] split = scan.split("\\|");
                    scanList.addAll(Arrays.asList(split));
                }
                for (String scanID: scanList) {
                    if (!targetScan.contains(scanID)) {
                        if (nonMatchedScans.isEmpty()) {
                            nonMatchedScans += scanID;
                        } else {
                            nonMatchedScans += "|" + scanID;
                        } 
                    } else if (matchedScans.isEmpty()) {
                        matchedScans += scanID;
                    } else {
                        matchedScans += "|" + scanID;
                    }
                }
            }
        }
        System.out.println("Found " + count + " peptide scans that were present in ");
        return peptides;
    }
}

/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control * 
 */
package matrix;

import collections.PeptideCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import objects.Peptide;

/**
 * Compares Scan ID's between two peptide collections.
 * @author vnijenhuis
 */
public class ScanIDComparator implements Callable{
    /**
     * Collection of peptide objects.
     */
    private final PeptideCollection peptides1;

    /**
     * Collection of peptide objects.
     */
    private final PeptideCollection peptides2;
    
    /**
     * Multithreaded peptide collection matcher.
     * @param collection1 first collection  of peptide objects.
     * @param collection2 second collection of peptide objects.
     */
    public ScanIDComparator(final PeptideCollection collection1, final PeptideCollection collection2) {
        this.peptides1 = collection1;
        this.peptides2 = collection2;
    }
    
    /**
     * Call function which matches the protein and peptide collections.
     * @return returns a peptide collection with peptides that did NOT match to the protein database. 
     */
    @Override
    public final Object call() {
        PeptideCollection peptides = new PeptideCollection();
        for (Peptide peptide2: peptides2.getPeptides()) {
            for (Peptide peptide1: peptides1.getPeptides()) {
                if (peptide2.getSequence().equals(peptide1.getSequence())) {
                    String nonMatchedScans = "";
                    String nonMatchedScores = "";
                    String matchedScans = "";
                    String matchedScores = "";
                    ArrayList<String> scanList = new ArrayList<>();
                    ArrayList<String> scoreList = new ArrayList<>();
                    String targetScans = peptide1.getScanID();
                    String scan = peptide2.getScanID();
                    String score = peptide2.getScore();
                    if (scan.contains("|")) {
                        String[] split = scan.split("\\|");
                        scanList.addAll(Arrays.asList(split));
                    } else {
                        scanList.add(scan);
                    }
                    if (score.contains("|")) {
                        String[] split = score.split("\\|");
                        scoreList.addAll(Arrays.asList(split));
                    } else {
                        scoreList.add(scan);
                    }
                    for (int i = 0; i < scanList.size(); i++) {
                        String scanID = scanList.get(i);
                        String scoreValue = scoreList.get(i);
                        if (!targetScans.contains(scanID)) {
                            if (nonMatchedScans.isEmpty()) {
                                nonMatchedScans += scanID;
                                nonMatchedScores += scoreValue;
                            } else {
                                if (!nonMatchedScans.contains(scanID)) {
                                     nonMatchedScans += "|" + scanID;
                                 }
                                 if (!nonMatchedScores.contains(scoreValue)) {
                                     nonMatchedScores += "|" + scoreValue;
                                 }
                            } 
                        } else if (matchedScans.isEmpty()) {
                            matchedScans += scanID;
                            matchedScores += scoreValue;
                        } else {
                            if (!matchedScans.contains(scanID)) {
                                matchedScans += "|" + scanID;
                            }
                            if (!matchedScores.contains(scoreValue)) {
                                matchedScores += "|" + scoreValue;
                            }
                        }
//                        System.out.println("MATCHS: " + scanID + "\t" + nonMatchedScans);
//                        break;
                    }
                    if (!peptide2.getSequence().equals("")) {
                        peptides.addPeptide(peptide2);
                    }
                    break;
                }
            }
        }
        System.out.println("Collected " + peptides.getPeptides().size() + " non-matched peptides!");
        return peptides;
    }
    
    public final PeptideCollection matchPeptideScanIDs(final PeptideCollection collection1,
        final PeptideCollection collection2, final Integer threads)
        throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //Executes the call function of MultiThreadDatabaseMatcher.
        Callable<PeptideCollection> callable = new ScanIDComparator(collection1, collection2);
        //Collects the output from the call function
        Future<PeptideCollection> future = pool.submit(callable);
        PeptideCollection peptides = future.get();
        pool.shutdown();
        return peptides;
    }
}

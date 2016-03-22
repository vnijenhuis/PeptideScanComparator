/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control * 
 */
package matrix;

import collections.ScanIDCollection;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import objects.ScanID;

/**
 * Compares Scan ID's between two peptide collections.
 * @author vnijenhuis
 */
public class ScanIDComparator implements Callable{
    /**
     * Collection of ScanID objects.
     */
    private final ScanIDCollection uniprotScans;

    /**
     * Collection of ScanID objects.
     */
    private final ScanIDCollection sampleScans;

    /**
     * Name of the dataset.
     */
    private final String dataset;

    /**
     * List of all datasets.
     */
    private final ArrayList<String> datasets;
    
    /**
     * Multithreaded peptide collection matcher.
     * @param uniprot first collection  of peptide objects.
     * @param collection second collection of peptide objects.
     * @param dataset name of the dataset.
     * @param datasets list of all datasets.
     */
    public ScanIDComparator(final ScanIDCollection uniprot, final ScanIDCollection collection, final String dataset,
            final ArrayList<String> datasets) {
        this.uniprotScans = uniprot;
        this.sampleScans = collection;
        this.dataset = dataset;
        this.datasets = datasets;
    }
    
    /**
     * Call function which matches the protein and peptide collections.
     * @return returns a peptide collection with peptides that did NOT match to the protein database. 
     */
    @Override
    public final Object call() {
        int count = 0;
        for (ScanID uniprotScan: uniprotScans.getScanIDs()) {
            count++;
            for (ScanID sampleScan: sampleScans.getScanIDs()) {
                if (uniprotScan.getScanID().equals(sampleScan.getScanID())) {
                    ArrayList<String> matchedSequences = new ArrayList<>();
                    ArrayList<String> matchedScores = new ArrayList<>();
                    ArrayList<String> targetSequences = uniprotScan.getUniprotSequences();
                    ArrayList<String> sequences = new ArrayList<>();
                    ArrayList<String> scores = new ArrayList<>();
                    if (dataset.equals(datasets.get(1))) {
                        sequences = sampleScan.getCombinedSequences();
                        scores = sampleScan.getCombinedScores();
                        sampleScan.getCombinedScores();
                        for (int i =0; i <sequences.size(); i++) {
                            if (targetSequences.contains(sequences.get(i))) {
                                matchedSequences.add(sequences.get(i));
                                matchedScores.add(scores.get(i));
                            }
                        }
                        uniprotScan.addAllCombinedSequences(matchedSequences);
                        uniprotScan.addAllCombinedScores(matchedScores);
                    } else if (dataset.equals(datasets.get(2))) {
                        sequences = sampleScan.getIndividualSequences();
                        scores = sampleScan.getIndividualScores();
                        sampleScan.getIndividualScores();
                        for (int i =0; i <sequences.size(); i++) {
                            if (targetSequences.contains(sequences.get(i))) {
                                matchedSequences.add(sequences.get(i));
                                matchedScores.add(scores.get(i));
                            }
                        }
                        uniprotScan.addAllIndividualSequences(matchedSequences);
                        uniprotScan.addAllIndividualScores(matchedScores);
                    }
                }
            }
            if (count % 1000 == 0) {
                System.out.println("Compared " + count + " scan IDs.");
            }
        }
        System.out.println("Finished comparing " + count + " scan IDs!");
        return uniprotScans;
    }
    
    public final ScanIDCollection matchPeptideScanIDs(final ScanIDCollection collection1,
        final ScanIDCollection collection2, final Integer threads, final String name, final ArrayList<String> datasets)
        throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //Executes the call function of MultiThreadDatabaseMatcher.
        Callable<ScanIDCollection> callable = new ScanIDComparator(collection1, collection2, name, datasets);
        //Collects the output from the call function
        Future<ScanIDCollection> future = pool.submit(callable);
        ScanIDCollection finalScans = future.get();
        pool.shutdown();
        return finalScans;
    }
}

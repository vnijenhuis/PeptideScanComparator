/*
 *  @author Vikthor Nijenhuis
 *  @project peptide fragmentation control *
 */
package matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import objects.ScanID;

/**
 * Compares Scan ID's between two peptide HashMaps.
 *
 * @author vnijenhuis
 */
public class ScanIDComparator implements Callable {

    /**
     * HashMap of ScanID objects.
     */
    private final HashMap<String, ArrayList<ScanID>> uniprotScans;

    /**
     * HashMap of ScanID objects.
     */
    private final HashMap<String, ArrayList<ScanID>> sampleScans;

    /**
     * Name of the dataset.
     */
    private final String dataset;

    /**
     * List of all datasets.
     */
    private final ArrayList<String> datasets;

    /**
     * Multithreaded peptide HashMap matcher.
     *
     * @param uniprot first HashMap of peptide objects.
     * @param collection second HashMap of peptide objects.
     * @param dataset name of the dataset.
     * @param datasets list of all datasets.
     */
    public ScanIDComparator(final HashMap<String, ArrayList<ScanID>> uniprot, final HashMap<String, ArrayList<ScanID>> collection, final String dataset,
            final ArrayList<String> datasets) {
        this.uniprotScans = uniprot;
        this.sampleScans = collection;
        this.dataset = dataset;
        this.datasets = datasets;
    }

    /**
     * Call function which matches two HashMaps with eachother.
     *
     * @return returns a HashMap with matched Scan IDs.
     */
    @Override
    public final Object call() {
        int count = 0;
        //Compare scan IDs for each scan ID in both HashMaps.
       for (Map.Entry<String, ArrayList<ScanID>> uniprotEntry : uniprotScans.entrySet()) {
            for (ScanID uniprotScanObject : uniprotEntry.getValue()) {
                count++;
                for (Map.Entry<String, ArrayList<ScanID>> sampleEntry: sampleScans.entrySet()) {
                    //If keys match, grab scan ID data.
                    if (uniprotEntry.getKey().equals(sampleEntry.getKey())) {
                        for (ScanID sampleScanObject: sampleEntry.getValue()) {
                            //If scan IDs match get the score and sequence data for that scan ID.
                            if (uniprotScanObject.getScanID().equals(sampleScanObject.getScanID())) {
                                ArrayList<String> matchedSequences = new ArrayList<>();
                                ArrayList<String> matchedScores = new ArrayList<>();
                                //Gather uniprot sequences for each scan ID.
                                ArrayList<String> targetSequences = uniprotScanObject.getUniprotSequences();
                                //Match dataset names to put scan data in the right variables.
                                if (dataset.equals(datasets.get(1))) {
                                    ArrayList<String> sequences = sampleScanObject.getCombinedSequences();
                                    ArrayList<String> scores = sampleScanObject.getCombinedScores();
                                    sampleScanObject.getCombinedScores();
                                    for (int i = 0; i < sequences.size(); i++) {
                                        //Add matched sequences and scores to a new list.
                                        if (targetSequences.contains(sequences.get(i))) {
                                            matchedSequences.add(sequences.get(i));
                                            matchedScores.add(scores.get(i));
                                        }
                                    }
                                    //Add matched sequences and scores to the uniprotScan object.
                                    uniprotScanObject.addAllCombinedSequences(matchedSequences);
                                    uniprotScanObject.addAllCombinedScores(matchedScores);
                                } else if (dataset.equals(datasets.get(2))) {
                                    ArrayList<String> sequences = sampleScanObject.getIndividualSequences();
                                    ArrayList<String> scores = sampleScanObject.getIndividualScores();
                                    sampleScanObject.getIndividualScores();
                                    for (int i = 0; i < sequences.size(); i++) {
                                        //Add matched sequences and scores to a new list.
                                        if (targetSequences.contains(sequences.get(i))) {
                                            matchedSequences.add(sequences.get(i));
                                            matchedScores.add(scores.get(i));
                                        }
                                    }
                                    //Add matched sequences and scores to the uniprotScan object.
                                    uniprotScanObject.addAllIndividualSequences(matchedSequences);
                                    uniprotScanObject.addAllIndividualScores(matchedScores);
                                }
                            }
                        }
                    }
                }
                //Report the count for every 5000 scan IDs that were matched.
                if (count % 5000 == 0) {
                    System.out.println("Compared " + count + " scan IDs.");
                }
            }
        }
        System.out.println("Finished comparing " + count + " scan IDs!");
        //Returns the uniprot ScanID collection.
        return uniprotScans;
    }

    /**
     * Matched two HashMaps with peptide Objects against each other to gather combined data of those HashMaps.
     * @param collection1 HashMap one.
     * @param collection2 HashMap two.
     * @param threads amount of threads allocated to the threadpool.
     * @param name name of the current dataset.
     * @param datasets list of all dataset names.
     * @return Matched HashMap with combined data of both HashMaps.
     * @throws InterruptedException program was interrupted by another process.
     * @throws ExecutionException  error encountered in the execution of the program.
     */
    public final HashMap<String, ArrayList<ScanID>> matchPeptideScanIDs(final HashMap<String, ArrayList<ScanID>> collection1,
            final HashMap<String, ArrayList<ScanID>> collection2, final Integer threads, final String name, final ArrayList<String> datasets)
            throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //Executes the call function of MultiThreadDatabaseMatcher.
        Callable<HashMap<String, ArrayList<ScanID>>> callable = new ScanIDComparator(collection1, collection2, name, datasets);
        //Collects the output from the call function
        Future<HashMap<String, ArrayList<ScanID>>> future = pool.submit(callable);
        //Retrieves the returned data from call();
        HashMap<String, ArrayList<ScanID>> finalScans = future.get();
        //Shutdown for the threadpool: prevents endless looping.
        pool.shutdown();
        return finalScans;
    }
}

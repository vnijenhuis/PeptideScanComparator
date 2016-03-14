/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  * 
 */
package matcher;

import collections.PeptideCollection;
import collections.ProteinCollection;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import objects.Peptide;
import objects.Protein;

/**
 * Uses multi-threading to allow for a faster collection matching.
 * @author vnijenhuis
 */
public class MultiThreadDatabaseMatcher implements Callable {
    /**
     * Collection of peptides.
     */
    private final PeptideCollection peptides;

    /**
     * Collection of proteins.
     */
    private final ProteinCollection proteins;

    /**
     * Contains the matched peptides.
     */
    private PeptideCollection matchedPeptideCollection;

    /**
     * Contains non-matched peptides.
     */
    private final PeptideCollection nonMatchedPeptideCollection;

    /**
     * Multithreaded database matcher.
     * @param peptides collection of peptide objects.
     * @param proteins collection of protein objects.
     */
    public MultiThreadDatabaseMatcher(final PeptideCollection peptides, final ProteinCollection proteins) {
        this.peptides = peptides;
        this.proteins = proteins;
        this.matchedPeptideCollection = new PeptideCollection();
        this.nonMatchedPeptideCollection = new PeptideCollection();
    }

    /**
     * Call function which matches the protein and peptide collections.
     * @return returns a peptide collection with peptides that did NOT match to the protein database. 
     */
    @Override
    public final Object call() {
        matchedPeptideCollection = new PeptideCollection();
        //Matches peptides to the protein database.
        int count = 0;
        for (Peptide peptide: peptides.getPeptides().subList(0, 1000)) {
            count += 1;
            Boolean noMatch = true;
            for (Protein protein : proteins.getProteins()) {
                //Checks if peptide sequence is present in the given database(s).
                String sequence = peptide.getSequence().replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
                if (protein.getSequence().contains(sequence)) {
                    matchedPeptideCollection.addPeptide(peptide);
                        noMatch = false;
                        break;
                    }
                }
                if (noMatch) {
                    nonMatchedPeptideCollection.addPeptide(peptide);
                }
            if (count % 1000 == 0) {
                System.out.println("Matched " + count + " peptide sequences to the protein database.");
            }
        }
        ArrayList<PeptideCollection> peptideList = new ArrayList<>();
        peptideList.add(matchedPeptideCollection);
        peptideList.add(nonMatchedPeptideCollection);
        //Returns two peptide collections as an array list.
        return peptideList;
    }

    /**
     * Collects matched peptides and returns these peptides in a new collection.
     * @param peptides collection of peptides.
     * @param proteins collection of proteins.
     * @param threads amount of threads used.
     * @return collection of matched peptides.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    public ArrayList<PeptideCollection> getMatchedPeptides(final PeptideCollection peptides, final ProteinCollection proteins, 
            final Integer threads) throws InterruptedException, ExecutionException {
        //Creates a new execution service and sets the amount of threads to use. (if available)
        System.out.println("Using " + threads + " threads to match peptides to the protein database.");
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //Executes the call function of MultiThreadDatabaseMatcher.
        Callable<ArrayList<PeptideCollection>> callable = new MultiThreadDatabaseMatcher(peptides, proteins);
        //Collects the output from the call function
        Future<ArrayList<PeptideCollection>> future = pool.submit(callable);
        //Adds peptide collections to a list.
        ArrayList<PeptideCollection> peptideList = future.get();
        System.out.println(peptideList.get(0).getPeptides().size() + " peptides matched to the protein database");
        System.out.println(peptideList.get(1).getPeptides().size() + " peptides did not match to the protein database.");
        //Shutdown command for the pool to prevent the script from running infinitely.
        pool.shutdown();
        return peptideList;
    }
}

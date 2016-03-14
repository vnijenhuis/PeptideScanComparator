/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package matcher;

import collections.ProteinCollection;
import collections.PeptideCollection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import objects.Protein;
import objects.Peptide;

/**
 * Matches the peptideMatrix sequences to databases.
 * For example: uniprot, ensemble, all individual proteins.
 * @author vnijenhuis
 */
public class SampleDatabaseMatcher implements Callable {
    /**
     * Matches peptide sequences to a combined database of individual protein sequences.
     * @param peptides collection of Peptide objects.
     * @param proteins collection of Protein objects.
     * @return ProteinPeptideCollection with adjusted values.
     */
    /**
     * Collection of peptides.
     */
    private final PeptideCollection peptides;

    /**
     * Collection of proteins.
     */
    private final ProteinCollection proteins;

    /**
     * Name for the database. (Combined/Individual).
     */
    private final String database;

    /**
     * Multithreaded database matcher.
     * @param peptides collection of peptide objects.
     * @param proteins collection of protein objects.
     * @param database database name.
     */
    public SampleDatabaseMatcher(final PeptideCollection peptides, final ProteinCollection proteins, final String database) {
        this.peptides = peptides;
        this.proteins = proteins;
        this.database = database;
    }

    /**
     * Call function which matches peptide sequences to a protein database,
     * @return returns a peptide collection with peptides that matched to the protein database.
     */
    @Override
    public final Object call() {
        PeptideCollection matchedPeptideCollection = new PeptideCollection();
        //Matches peptides to the protein database.
        int count = 0;
        for (Peptide peptide: peptides.getPeptides()) {
            count += 1;
            for (Protein protein : proteins.getProteins()) {
                //Checks if peptide sequence is present in the given database(s).
                String sequence = peptide.getSequence().replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
                if (protein.getSequence().contains(sequence)) {
                    matchedPeptideCollection.addPeptide(peptide);
                        break;
                    }
                }
            if (count % 1000 == 0) {
                System.out.println("Matched " + count + " peptide sequences to the " + database + "protein database.");
            }
        }
        //Returns two peptide collections as an array list.
        return matchedPeptideCollection;
    }

    /**
     * Collects matched peptides and returns these peptides in a new collection.
     * @param nonMatchedPeptides collection of peptides.
     * @param proteinDatabase collection of proteins.
     * @param threads amount of threads used.
     * @param databaseName name of the database.
     * @return collection of matched peptides.
     * @throws InterruptedException process was interrupted.
     * @throws ExecutionException could not execute the call function.
     */
    public PeptideCollection getMatchedPeptides(final PeptideCollection nonMatchedPeptides,
            final ProteinCollection proteinDatabase, final Integer threads, final String databaseName)
            throws InterruptedException, ExecutionException {
        //Creates a new execution service and sets the amount of threads to use. (if available)
        System.out.println("Using " + threads + " threads to match peptides to the " + databaseName + " protein database.");
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //Executes the call function of MultiThreadDatabaseMatcher.
        Callable<PeptideCollection> callable = new SampleDatabaseMatcher(nonMatchedPeptides, proteinDatabase, databaseName);
        //Collects the output from the call function
        Future<PeptideCollection> future = pool.submit(callable);
        //Returns the matched peptide objects.
        PeptideCollection matchedPeptides = future.get();
        System.out.println(matchedPeptides.getPeptides().size() + " peptides matched to the " + databaseName + " protein database");
        //Shutdown command for the pool to prevent the script from running infinitely.
        pool.shutdown();
        return matchedPeptides;
    }
}

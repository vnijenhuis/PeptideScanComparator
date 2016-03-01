/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package collections;

import java.util.ArrayList;
import objects.Peptide;

/**
 * A collection of peptide objects.
 * @author vnijenhuis
 */
public class PeptideCollection {
    /**
     * Creates a HashSet for peptide objects.
     */
    private final ArrayList<Peptide>  peptides;

    /**
     * Creates a new HashSet.
     */
    public PeptideCollection() {
        peptides = new ArrayList<>();
    }

    /**
     * Adds peptide objects to the HashSet.
     * @param peptide peptide object.
     */
    public final void addPeptide(final Peptide peptide) {
        peptides.add(peptide);
    }
    
    /**
     * Removes peptide values from the HashSet.
     * @param peptide peptide object.
     */
    public final void removePeptide(final Peptide peptide) {
        peptides.remove(peptide);
    }
    /**
     * Returns the HashSet.
     * @return HashSet of peptides.
     */
    public final ArrayList<Peptide> getPeptides() {
        return peptides;
    }
}

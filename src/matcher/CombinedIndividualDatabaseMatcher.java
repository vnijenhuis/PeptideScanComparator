/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package matcher;

import collections.ProteinCollection;
import collections.PeptideCollection;
import objects.Protein;
import objects.Peptide;

/**
 * Matches the peptideMatrix sequences to databases.
 * For example: uniprot, ensemble, all individual proteins.
 * @author vnijenhuis
 */
public class CombinedIndividualDatabaseMatcher {
    /**
     * Matches peptide sequences to a combined database of individual protein sequences.
     * @param peptides collection of Peptide objects.
     * @param proteins collection of Protein objects.
     * @return ProteinPeptideCollection with adjusted values.
     */
    public final PeptideCollection matchToCombined(PeptideCollection peptides,
            ProteinCollection proteins) {
        System.out.println("Matching sequences to combined database.");
        PeptideCollection individualPeptides = new PeptideCollection();
        int count = 0;
        for (Peptide peptide: peptides.getPeptides()) {
            String sequence = peptide.getSequence().replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
            count += 1;
            //Test if a protein sequence contains the peptide sequence.
            for (Protein protein: proteins.getProteins()) {
                if (protein.getSequence().contains(sequence)) {
                    individualPeptides.addPeptide(peptide);
                    break;
                }
            }
            if (count %1000 == 0) {
                System.out.println("Matched " + count + " peptides to the combined database.");
            }
        }
        System.out.println("Matched " + peptides.getPeptides().size()
                + " peptides to the combined database.");
        //Return proteinPeptide collection with adjusted uniqueness values.
        return individualPeptides;
    }
}

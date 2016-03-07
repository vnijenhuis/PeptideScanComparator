/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package matrix;

import collections.ProteinCollection;
import collections.PeptideCollection;
import objects.Protein;
import objects.Peptide;

/**
 * Matches the peptideMatrix sequences to databases.
 * For example: uniprot, ensemble, all individual proteins.
 * @author vnijenhuis
 */
public class IndividualDatabaseMatcher {
    /**
     * Matches peptide sequences to a combined database of individual protein sequences.
     * @param peptides collection of Peptide objects.
     * @param proteins collection of Protein objects.
     * @return ProteinPeptideCollection with adjusted values.
     */
    public final PeptideCollection matchToIndividual(PeptideCollection peptides, ProteinCollection proteins) {
        System.out.println("Matching sequences to individual database.");
        int count = 0;
        for (Peptide peptide: peptides.getPeptides()) {
            String sequence = peptide.getSequence().replaceAll("\\(\\+[0-9]+\\.[0-9]+\\)", "");
            count += 1;
            Integer oneMatch = 0;
            //Get accession names.
            String[] accessions = peptide.getScan().split("\\|");
            String positions = "";
            Boolean newPosition = false;
            //Test if a protein sequence contains the peptide sequence.
            for (String accession: accessions) {
                String position = "0";
                for (Protein protein: proteins.getProteins()) {
                    if (protein.getSequence().contains(sequence)) {
                        //Check if accessions match to gather start and end positions of the peptide sequence.
                        if (accession.equals(protein.getAccession())) {
                            Integer start = protein.getSequence().indexOf(sequence) + 1;
                            Integer end = (start + sequence.length());
                            position = start + "_" + end;
                            newPosition = true;
                            break;
                        }
                    }
                    oneMatch += 1;
                }
                //Determines the position value.
                if (newPosition && positions.isEmpty()) {
                    positions += position;
                } else if (newPosition) {
                    positions += "|" + position;
                } else if (positions.isEmpty()) {
                    positions += position;
                } else {
                    positions += "|0";
                }
            }
            //Adds position values to the proteinPeptide object.
            
            if (count %1000 == 0) {
                System.out.println("Matched " + count + " peptides to the individual database.");
            }
        }
        System.out.println("Matched " + peptides.getPeptides().size()
                + " peptides to the individual database.");
        return peptides;
    }
}

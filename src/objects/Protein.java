/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package objects;

/**
 * Defines a protein object.
 * @author vnijenhuis
 */
public class Protein {
    /**
     * Amino acid sequence of the protein.
     */
    private final String aminoAcidSequence;
    
    /**
     * Protein Accession ID.
     */
    private final String accession;

    /**
     * Creates a protein object with a sequence.
     * @param proteinAminoAcidSequence contains the amino acid sequence.
     * @param proteinAcccession protein accession id.
     */
    public Protein(final String proteinAminoAcidSequence, final String proteinAcccession) {
        this.aminoAcidSequence = proteinAminoAcidSequence;
        this.accession = proteinAcccession;
    }

    /**
     * Returns the amino acid sequence.
     * @return amino acid sequence as String.
     */
    public final String getSequence() {
        return this.aminoAcidSequence;
    }
    
    /**
     * Returns the protein accession ID.
     * @return protein accession ID as String.
     */
    public final String getAccession() {
        return this.accession;
    }
    
    /**
     * To string function.
     * @return protein object as String.
     */
    @Override
    public final String toString() {
        return "Protein{Sequence; " + this.aminoAcidSequence + ", Accession; " + this.accession +"}";
    }
}

/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package objects;

/**
 * Creates a Peptide object.
 * @author vnijenhuis
 */
public class Peptide {
    /**
     * Amino acid sequence of the peptide.
     */
    private final String peptideSequence;

    /**
     * Data from the Scan parameter.
     */
    private String scan;

    /**
     * ID of the Scan parameter.
     */
    private String dataset;

    /**
     * Sample to which this peptide belongs to.
     */
    private final String sample;

    /**
     * Coverage value.
     */
    private String coverage;

    /**
     * Creates a Peptide object.
     * @param peptideSequence contains the peptide amino acid sequence.
     * @param scanData data of the Scan parameter from DB search psm.csv.
     * @param peptideCoverage contains the peptide coverage value.
     * @param peptideDataset ID of the Scan parameter.
     * @param sampleName sample name that this peptide belongs to.
     */
    public Peptide(final String peptideSequence, final String scanData,
            final String peptideCoverage ,final String peptideDataset,
            final String sampleName) {
        this.peptideSequence = peptideSequence;
        this.dataset = peptideDataset;
        this.scan = scanData;
        this.coverage = peptideCoverage;
        this.sample = sampleName;
    }

    /**
     * Returns the data base PSM sequence.
     * @return peptide amino acid sequence as String.
     */
    public final String getSequence() {
        return this.peptideSequence;
    }

    /**
     * Returns the name of the dataset.
     * @return dataset name as String.
     */
    public final String getDataset() {
        return this.dataset;
    }

     /**
     * Adds an additional dataset name.
     * @param newDataset dataset name.
     */
    public final void addDataset(final String newDataset) {
        this.dataset = this.dataset + newDataset;
    }

    /**
     * Returns the Scan data.
     * @return Scan data as String.
     */
    public final String getScan() {
        return this.scan;
    }

    /**
     * Adds Scan data to this peptide.
     * @param scanID Scan File:ID as String.
     */
    public final void addScan(final String scanID) {
        this.scan = this.scan + "|" + scanID;
    }

    /**
     * Returns the sample id.
     * @return accession id as String.
     */
    public final String getSample() {
        return this.sample;
    }

    /**
     * Gets the coverage value of the peptide.
     * @return coverage value of the peptide.
     */
    public final String getCoverage() {
        return this.coverage;
    }

    /**
     * adds a coverage value to the sample.
     * @param peptideCoverage new coverage value that should be added.
     */
    public final void addCoverage(final String peptideCoverage) {
       this.coverage += "|" + peptideCoverage;
    }

    /**
     * To string function.
     * @return data base PSM object as String.
     */
    @Override
    public final String toString() {
        return "Peptide{Sequence; " + this.peptideSequence + ", dataset; "
                + this.dataset + ", Sample; " + this.sample + ", Scan; "
                + this.scan + ", Coverage; " + this.coverage + "}";
    }
}

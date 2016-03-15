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
    private String score;

    /**
     * Field for matched scan IDs.
     */
    private String uniprotScan;

    /**
     * Field for non-matched scan IDs.
     */
    private String nonUniprotScan;
    private String nonUniprotScore;
    private String uniprotScore;

    /**
     * Creates a Peptide object.
     * @param peptideSequence contains the peptide amino acid sequence.
     * @param scanData data of the Scan parameter from DB search psm.csv.
     * @param peptideScore contains the peptide score value.
     * @param peptideDataset ID of the Scan parameter.
     * @param sampleName sample name that this peptide belongs to.
     */
    public Peptide(final String peptideSequence, final String scanData,
            final String peptideScore ,final String peptideDataset,
            final String sampleName) {
        this.peptideSequence = peptideSequence;
        this.dataset = peptideDataset;
        this.scan = scanData;
        this.score = peptideScore;
        this.sample = sampleName;
        this.uniprotScan = "";
        this.nonUniprotScan = "";
        this.uniprotScore = "";
        this.nonUniprotScore = "";
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
     * Overwrites current scan data with new scan data.
     * @param scans contains 1 or more scan IDs as String.
     */
    public final void setScan(final String scans) {
        this.scan = scans;
    }

     /**
     * Returns the Scan data.
     * @return Scan data as String.
     */
    public final String getUniprotScan() {
        return this.uniprotScan;
    }   

    /**
     * Adds Scan data to this peptide.
     * @param scanID Scan File:ID as String.
     */
    public final void setUniprotScan(final String scanID) {
        this.uniprotScan = scanID;
    }

     /**
     * Returns the Scan data.
     * @return Scan data as String.
     */
    public final String getNonUniprotScan() {
        return this.nonUniprotScan;
    }   

    /**
     * Adds Scan data to this peptide.
     * @param scanID Scan File:ID as String.
     */
    public final void setNonUniprotScan(final String scanID) {
        this.nonUniprotScan = scanID;
    }
    /**
     * Returns the sample id.
     * @return accession id as String.
     */
    public final String getSample() {
        return this.sample;
    }

    /**
     * Gets the score value (-10lgp) of the peptide.
     * @return score value of the peptide.
     */
    public final String getScore() {
        return this.score;
    }

    /**
     * adds a score value to the sample.
     * @param peptideScore new score value that should be added.
     */
    public final void addScore(final String peptideScore) {
       this.score += "|" + peptideScore;
    }
    
    /**
     * Gets the score value (-10lgp) of the peptide.
     * @return score value of the peptide.
     */
    public final String getUniprotScore() {
        return this.uniprotScore;
    }

    /**
     * adds a score value to the sample.
     * @param peptideScore new score value that should be added.
     */
    public final void setUniprotScore(final String peptideScore) {
       this.uniprotScore =  peptideScore;
    }
    
    /**
     * Gets the score value (-10lgp) of the peptide.
     * @return score value of the peptide.
     */
    public final String getNonUniprotScore() {
        return this.nonUniprotScore;
    }

    /**
     * adds a score value to the sample.
     * @param peptideScore new score value that should be added.
     */
    public final void setNonUniprotScore(final String peptideScore) {
       this.nonUniprotScore =  peptideScore;
    }

    /**
     * To string function.
     * @return data base PSM object as String.
     */
    @Override
    public final String toString() {
        return "Peptide{Sequence; " + this.peptideSequence + ", dataset; " + this.dataset + ", Sample; " + this.sample
                + ", Scan; " + this.scan + ", Uniprot scan; " + this.uniprotScan + ", non-uniprot scan "
                + this.nonUniprotScan + ", Score; " + this.score + ", Uniprot score; " + this.uniprotScore
                + ", non-uniprot score " + this.nonUniprotScore + "}";
    }
}

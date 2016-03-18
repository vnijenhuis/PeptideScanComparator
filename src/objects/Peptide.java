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
    private String peptideSequence;

    /**
     * Data from the Scan parameter.
     */
    private String scan;

    /**
     * Mass spectrometry method used.
     */
    private String method;

    /**
     * Coverage value.
     */
    private String score;

    /**
     * Creates a Peptide object.
     * @param method name of the ms method that was used.
     * @param peptideSequence contains the peptide amino acid sequence.
     * @param scanData data of the Scan parameter from DB search psm.csv.
     * @param peptideScore contains the peptide score value.
     */
    public Peptide(final String method, final String scanData, final String peptideSequence, final String peptideScore) {
        this.peptideSequence = peptideSequence;
        this.method = method;
        this.scan = scanData;
        this.score = peptideScore;
    }

    /**
     * Returns the data base PSM sequence.
     * @return peptide amino acid sequence as String.
     */
    public final String getSequence() {
        return this.peptideSequence;
    }
    
    /**
     * Adds a peptide sequence to this scan ID.
     * @param sequence sequence to add to the database.
     */
    public final void addSequence(final String sequence) {
        this.peptideSequence = this.peptideSequence + "|" + sequence;
    }

    /**
     * Returns the name of the dataset.
     * @return dataset name as String.
     */
    public final String getMethods() {
        return this.method;
    }

     /**
     * Adds an additional ms method name.
     * @param method method name.
     */
    public final void addMethod(final String method) {
        this.method = this.method + method;
    }

    /**
     * Returns the Scan data.
     * @return Scan data as String.
     */
    public final String getScanID() {
        return this.scan;
    }


    /**
     * Overwrites current scan data with new scan data.
     * @param scan new scan ID.
     */
    public final void setScanID(final String scan) {
        this.scan = scan;
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
     * To string function.
     * @return data base PSM object as String.
     */
    @Override
    public final String toString() {
        return "Peptide{Sequence; " + this.peptideSequence + ", Method; " + this.method + ", Scan; " + this.scan
                + ", Score; " + this.score + "}";
    }
}

/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package objects;

import java.util.ArrayList;

/**
 * Creates a ScanID object.
 *
 * @author vnijenhuis
 */
public class ScanID {

    /**
     * Contains the file number and scan ID.
     */
    private final String scanID;

    /**
     * Contains the name of the mass spectrometry method.
     */
    private final String method;

    /**
     * Contains all combinedmRNASeq sequences for this ScanID.
     */
    private final ArrayList<String> combinedSequences;

    /**
     * Contains all Uniprot sequences for this ScanID.
     */
    private final ArrayList<String> uniprotSequences;

    /**
     * Contains all individualmRNASeq sequences for this ScanID.
     */
    private final ArrayList<String> individualSequences;

    /**
     * Contains all uniprot scores for this ScanID.
     */
    private final ArrayList<String> uniprotScores;

    /**
     * Contains all combinedmRNASeq scores for this ScanID.
     */
    private final ArrayList<String> combinedScores;

    /**
     * Contains all individualmRNASeq scores for this ScanID.
     */
    private final ArrayList<String> individualScores;

    /**
     * Creates a ScanID object.
     *
     * @param method name of the ms method that was used.
     * @param peptideSequence contains the peptide amino acid sequence.
     * @param scanData data of the Scan parameter from DB search psm.csv.
     * @param score contains the -10lgP value of the peptide sequence.
     * @param sample name of the sample of this scan ID and sequence.
     * @param dataset name of the dataset.
     * @param datasets list of all dataset names.
     */
    public ScanID(final String method, final String scanData, final String peptideSequence, final String score,
            final String sample, final String dataset, final ArrayList<String> datasets) {
        this.scanID = scanData;
        this.method = method;
        this.uniprotSequences = new ArrayList<>();
        this.combinedSequences = new ArrayList<>();
        this.individualSequences = new ArrayList<>();
        this.uniprotScores = new ArrayList<>();
        this.combinedScores = new ArrayList<>();
        this.individualScores = new ArrayList<>();
        //Add peptideSequence to the sequence list of the corresponding dataset.
        if (dataset.contains(datasets.get(0))) {
            this.uniprotSequences.add(peptideSequence);
            this.uniprotScores.add(score);
        } else if (dataset.contains(datasets.get(1))) {
            this.combinedSequences.add(peptideSequence);
            this.combinedScores.add(score);
        } else if (dataset.contains(datasets.get(2))) {
            this.individualSequences.add(peptideSequence);
            this.individualScores.add(score);
        }
    }

    /**
     * Provides the value of the ScanID.
     * @return scan ID as String.
     */
    public final String getScanID() {
        return this.scanID;
    }

    /**
     * Provides the name of the mass spectrometry method.
     * @return method name as String.
     */
    public final String getMethod() {
        return this.method;
    }

    /**
     * Provides a list of uniprot peptide sequences.
     * @return list of peptide sequences.
     */
    public final ArrayList<String> getUniprotSequences() {
        return this.uniprotSequences;
    }

    /**
     * Adds a single peptide sequence to the list of uniprot peptide sequences.
     * @param sequence single peptide sequence.
     */
    public final void addUniprotSequence(final String sequence) {
        this.uniprotSequences.add(sequence);
    }

    /**
     * Adds multiple peptide sequences to the list of uniprot peptide sequences.
     * @param sequenceList list of sequences.
     */
    public final void addAllUniprotSequences(final ArrayList<String> sequenceList) {
        this.uniprotSequences.addAll(sequenceList);
    }

    /**
     * Provides a list of all uniprot score values.
     * @return list of score values.
     */
    public final ArrayList<String> getUniprotScores() {
        return this.uniprotScores;
    }

    /**
     * Adds a single score value to the list of uniprot score balues.
     * @param score single score value.
     */
    public final void addUniprotScore(final String score) {
        this.uniprotScores.add(score);
    }

    /**
     * Adds multiple score values to the list of uniprot score values.
     * @param scoreList list of score values.
     */
    public final void addAllUniprotScores(final ArrayList<String> scoreList) {
        this.uniprotScores.addAll(scoreList);
    }

    /**
     * List of combinedmRNASeq peptide sequences.
     * @return list of peptide sequences.
     */
    public final ArrayList<String> getCombinedSequences() {
        return this.combinedSequences;
    }

    /**
     * Adds a single peptide sequence to the list of combinedmRNASeq peptide sequences.
     * @param sequence single peptide sequence.
     */
    public final void addCombinedSequence(final String sequence) {
        this.combinedSequences.add(sequence);
    }

    /**
     * Adds multiple peptide sequences to the list of combinedmRNASeq peptide sequences.
     * @param sequenceList list of peptide sequences.
     */
    public final void addAllCombinedSequences(final ArrayList<String> sequenceList) {
        this.combinedSequences.addAll(sequenceList);
    }

    /**
     * List of combinedmRNASeq score values.
     * @return list of score values.
     */
    public final ArrayList<String> getCombinedScores() {
        return this.combinedScores;
    }

    /**
     * Adds a single score value to the list of combinedmRNASeq score values.
     * @param score single score value.
     */
    public final void addCombinedScore(final String score) {
        this.combinedScores.add(score);
    }

    /**
     * Adds multiple scores to the list of combinedmRNASeq score values.
     * @param scoreList list of score values.
     */
    public final void addAllCombinedScores(final ArrayList<String> scoreList) {
        this.combinedScores.addAll(scoreList);
    }

    /**
     * Provides a list of all individualmRNASeq sequences.
     * @return list of peptide sequences.
     */
    public final ArrayList<String> getIndividualSequences() {
        return this.individualSequences;
    }

    /**
     * Adds a single sequence to the list of individualmRNASeq sequences.
     * @param sequence single peptide sequence.
     */
    public final void addIndividualSequence(final String sequence) {
        this.individualSequences.add(sequence);
    }

    /**
     * Adds multiple sequences to the list of individualmRNASeq sequences.
     * @param sequenceList list of peptide sequences.
     */
    public final void addAllIndividualSequences(final ArrayList<String> sequenceList) {
        this.individualSequences.addAll(sequenceList);
    }

    /**
     * List of all individualmRNASeq scores.
     * @return list of score values.
     */
    public final ArrayList<String> getIndividualScores() {
        return this.individualScores;
    }

    /**
     * Adds a single score to the list of individualmRNASeq score values.
     * @param score single score value.
     */
    public final void addIndividualScore(final String score) {
        this.individualScores.add(score);
    }

    /**
     * Adds multiple scores to the list of individualmRNASeq score values.
     * @param scoreList list of score values.
     */
    public final void addAllIndividualScores(final ArrayList<String> scoreList) {
        this.individualScores.addAll(scoreList);
    }

    /**
     * Overrides the normal toString() function to display all values for the ScanID object.
     * @return return ScanID string values.
     */
    @Override
    public final String toString() {
        return "PSM{Scan ID; " + this.scanID + ", Method; " + this.method + ", Uniprot Sequences; "
                + this.uniprotSequences.toString() + ", Uniprot Scores; " + this.uniprotScores.toString()
                + ", Combined Sequences; " + this.combinedSequences.toString() + ", Combined Scores; "
                + this.combinedScores.toString() + ", Individual Sequences; " + this.individualSequences.toString()
                + ", Individual Scores; " + this.individualScores.toString() + "}";
    }
}

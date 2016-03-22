/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package objects;

import java.util.ArrayList;

/**
 * Creates a ScanID object.
 * @author vnijenhuis
 */
public class ScanID {
    private final String scanID;
    private final String method;
    private final ArrayList<String> combinedSequences;
    private final ArrayList<String> uniprotSequences;
    private final ArrayList<String> individualSequences;
    private final ArrayList<String> uniprotScores;
    private final ArrayList<String> combinedScores;
    private final ArrayList<String> individualScores;

    /**
     * Creates a ScanID object.
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

   public final String getScanID() {
        return this.scanID;
    }

    public final String getMethod() {
        return this.method;
    }

    public final ArrayList<String> getUniprotSequences() {
        return this.uniprotSequences;
    }

    public final void addUniprotSequence(final String sequence) {
        this.uniprotSequences.add(sequence);
    }

    public final void addAllUniprotSequences(final ArrayList<String> sequenceList) {
        this.uniprotSequences.addAll(sequenceList);
    }

    public final ArrayList<String> getUniprotScores() {
        return this.uniprotScores;
    }

    public final void addUniprotScore(final String score) {
        this.uniprotScores.add(score);
    }

    public final void addAllUniprotScores(final ArrayList<String> scoreList) {
        this.uniprotScores.addAll(scoreList);
    }

    public final ArrayList<String> getCombinedSequences() {
        return this.combinedSequences;
    }

    public final void addCombinedSequence(final String sequence) {
        this.combinedSequences.add(sequence);
    }

    public final void addAllCombinedSequences(final ArrayList<String> sequenceList) {
        this.combinedSequences.addAll(sequenceList);
    }

    public final ArrayList<String> getCombinedScores() {
        return this.combinedScores;
    }

    public final void addCombinedScore(final String score) {
        this.combinedScores.add(score);
    }

    public final void addAllCombinedScores(final ArrayList<String> scoreList) {
        this.combinedScores.addAll(scoreList);
    }

    public final ArrayList<String> getIndividualSequences() {
        return this.individualSequences;
    }

    public final void addIndividualSequence(final String sequence) {
        this.individualSequences.add(sequence);
    }

    public final void addAllIndividualSequences(final ArrayList<String> sequenceList) {
        this.individualSequences.addAll(sequenceList);
    }

    public final ArrayList<String> getIndividualScores() {
        return this.individualScores;
    }

    public final void addIndividualScore(final String score) {
        this.individualScores.add(score);
    }

    public final void addAllIndividualScores(final ArrayList<String> scoreList) {
        this.individualScores.addAll(scoreList);
    }

    @Override
    public final String toString() {
        return "PSM{Scan ID; " + this.scanID + ", Method; " + this.method + ", Uniprot Sequences; "
                + this.uniprotSequences.toString()  + ", Uniprot Scores; " + this.uniprotScores.toString()
                + ", Combined Sequences; " + this.combinedSequences.toString()  + ", Combined Scores; "
                + this.combinedScores.toString() + ", Individual Sequences; " + this.individualSequences.toString()
                + ", Individual Scores; " + this.individualScores.toString() +  "}";
    }
}

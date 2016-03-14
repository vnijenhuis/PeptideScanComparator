/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package peptide.scan.collector;

import collections.PeptideCollection;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import collection.creator.PeptideCollectionCreator;
import collection.creator.ProteinCollectionCreator;
import collections.ProteinCollection;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import matcher.SampleDatabaseMatcher;
import matcher.MultiThreadDatabaseMatcher;
import matrix.PeptideScanMatrixCreator;
import matrix.ScanValueSetter;
import tools.CsvWriter;
import tools.SampleSizeGenerator;
import tools.ValidFileChecker;

/**
 * Gathers peptide fragmentation data such as file number and scan ID.
 * @author vnijenhuis
 */
public class PeptideScanCollector {

    /**
     * @param args the command line arguments
     * @throws org.apache.commons.cli.ParseException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws ParseException, IOException, InterruptedException, ExecutionException {
        PeptideScanCollector peptideFragmentation = new PeptideScanCollector();
        peptideFragmentation.start(args);
    }
    /**
     * Options for the commandline.
     */
    private final Options options;

    /**
     * Checks if given files exist.
     */
    private final ValidFileChecker fileChecker;

    /**
     * List of psm files.
     */
    private ArrayList<String> psmFiles;

    /**
     * Creates a collection of peptide objects.
     */
    private final PeptideCollectionCreator peptideCollection;

    /**
     * Collection of peptide objects.
     */
    private PeptideCollection peptides;

    /**
     * Csv file writer.
     */
    private final CsvWriter csvWriter;

    /**
     * Sets scan values to the peptide array.
     */
    private final ScanValueSetter setValues;

    /**
     * Creates an array with appropriate indices for each peptide sequence.
     */
    private final PeptideScanMatrixCreator scanMatrixCreator;

    /**
     * list of sample names.
     */
    private ArrayList<String> sampleList;

    /**
     * Creates a protein collection.
     */
    private final ProteinCollectionCreator createProteins;

    /**
     * Collection of Protein Objects.
     */
    private ProteinCollection proteins;

    /**
     * Matched peptides to a protein database.
     */
    private MultiThreadDatabaseMatcher proteinMatcher;

    /**
     * Matches peptides to the combined and individual database
     */
    private final SampleDatabaseMatcher sampleProteinMatcher;

    /**
     * List of database fasta files.
     */
    private ArrayList<String> fastaFiles;

    /**
     * List of sample files.
     */
    private ArrayList<String> sampleFiles;

    /**
     * Combined protein object collection.
     */
    private ProteinCollection combinedProteins;

    /**
     * String containing the path to the database name.
     */
    private String database;

    /**
     * Amount of threads used.
     */
    private Integer threads;

    /**
     * String containing the path to the combined database.
     */
    private String combinedDatabase;

    /**
     * Collection of individual protein objects.
     */
    private ProteinCollection individualProteins;

    /**
     * Private constructor.
     */
    private PeptideScanCollector() {
        //Help parameter
        options = new Options();
        Option help = Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Help function to display all commandline options.")
                .optionalArg(true)
                .build();
        options.addOption(help);
        //Input parameter for dataset folders.
        Option input = Option.builder("in")
                .hasArgs()
                .desc("Path to the dataset. (/home/name/1D25/commonRNAseq/).")
                .build();
        options.addOption(input);
        //Psm file parameter for the psm file name.
        Option psm = Option.builder("psm")
                .hasArg()
                .desc("Name of the psm file. Use double quotes if name contains a space. (DB seach psm.csv).")
                .build();
        options.addOption(psm);
        //Database path parameter
        Option dbPath = Option.builder("db")
                .hasArg()
                .desc("Path to the database folder (/home/name/Databases/uniprot.fasta)")
                .build();
        options.addOption(dbPath);
        //Combined database file parameter.
        Option cdbPath = Option.builder("cdb")
                .hasArg()
                .desc("Path to the combined database folder (/home/name/Databases/COPD19-database.fastas)")
                .build();
        options.addOption(cdbPath);
        //Path to the individual database files.
        Option idbPath = Option.builder("idb")
                .hasArg()
                .desc("Path to the combined database folder (/home/name/Databases/IndividualDatabases/)")
                .build();
        options.addOption(idbPath);
        //Path and name of the output file.
        Option output = Option.builder("out")
                .hasArg()
                .desc("Path to write output file to.  (/home/name/Combined/Matrix/).")
                .build();
        options.addOption(output);
        //Name of the target sample: will most likely be COPD
        Option target = Option.builder("target")
                .hasArg()
                .desc("Give the name of the target sample. (example: COPD) (CASE SENSITIVE!)")
                .build();
        options.addOption(target);
        //Name of the Control sample: will most likely be Control
        Option control = Option.builder("control")
                .hasArg()
                .desc("Give the name of the control sample. (example: Control) (CASE SENSITIVE!)")
                .build();
        options.addOption(control);
        Option thread = Option.builder("threads")
                .hasArg()
                .optionalArg(true)
                .desc("Amount of threads to use for this execution. (DEFAULTL: 2 threads)")
                .build();
        options.addOption(thread);
        //Checks the input files.
        fileChecker = new ValidFileChecker();
        //Creates peptide object collections.
        peptideCollection = new PeptideCollectionCreator();
        //Creates the matrix.
        scanMatrixCreator = new PeptideScanMatrixCreator();
        //Writes data to a csv file.
        csvWriter = new CsvWriter();
        //Sets values to the peptide array.
        setValues = new ScanValueSetter();
        //Creates protein object collections.
        createProteins = new ProteinCollectionCreator();
        //Matches to the combined/individual database.
        sampleProteinMatcher = new SampleDatabaseMatcher();
    }

    /**
     * Starts checking the input and starts the fragmentation control.
     * @param args command line arguments.
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    private void start(final String[] args) throws ParseException, IOException, InterruptedException, ExecutionException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        psmFiles = new ArrayList<>();
        sampleList = new ArrayList<>();
        Integer targetSampleSize = 0;
        Integer controlSampleSize = 0;
        if (Arrays.toString(args).toLowerCase().contains("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Peptide scan collector", options );
            System.exit(0);
        } else {
            //Allocate command line input to variables.
            String[] path = cmd.getOptionValues("in");
            String psmFile = cmd.getOptionValue("psm");
            String output = cmd.getOptionValue("out");
            database = cmd.getOptionValue("db");
            combinedDatabase = cmd.getOptionValue("cdb");
            String individualDatabases = cmd.getOptionValue("idb");
            String controlSample = cmd.getOptionValue("control");
            String targetSample = cmd.getOptionValue("target");
            String thread = cmd.getOptionValue("threads");
            if (thread.isEmpty()) {
                threads = 2;
            } else {
                threads = Integer.parseInt(thread);
            }
            if (controlSample.isEmpty() || targetSample.isEmpty()) {
                throw new IllegalArgumentException("You forgot to add a target or control sample."
                        + "Please check the -target and -control input.");
            }
            //Check file and folder validity.
            fileChecker.isCsv(psmFile);
            fileChecker.isFasta(database);
            fileChecker.isFasta(combinedDatabase);
            fileChecker.isDirectory(output);
            fileChecker.isDirectory(individualDatabases);
            //Control is added first.
            sampleList.add(controlSample);
            //Target is added second.
            sampleList.add(targetSample);
            //Get individual database files.
            fastaFiles = new ArrayList<>();
            fastaFiles = fileChecker.getFastaDatabaseFiles(individualDatabases, fastaFiles, sampleList);
            //Detect sample size and add all files to a list.
            for (String folder: path) {
                fileChecker.isDirectory(folder);
                SampleSizeGenerator sizeGenerator = new SampleSizeGenerator();
                ArrayList<Integer> sampleSize = sizeGenerator.getSamples(folder, sampleList);
                //Creates a list of peptide psm files.
                psmFiles = fileChecker.checkFileValidity(folder, psmFile, psmFiles);
                //Creates a list of protein-peptide files.
                //Gets highest healthy sample size
                if (sampleSize.get(0) > controlSampleSize) {
                    controlSampleSize = sampleSize.get(0);
                }
                //Gets the highest copd sample size.
                if (sampleSize.get(1) > targetSampleSize) {
                    targetSampleSize = sampleSize.get(1);
                }
            }
         fragmentationControl(output, targetSampleSize, controlSampleSize);
        }
    }

    /**
     * Processes the matrix and peptide files and creates an output file.
     * @param psmFiles PSM files from RNASeq dataset.
     * @param matrixFile matrix created by PeptideIdentificationQualityControl.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    private void fragmentationControl(final String output, final Integer copdSampleSize,
            final Integer healthySampleSize) throws IOException, InterruptedException, ExecutionException {
        Integer datasetCount = 0;
        String pattern = Pattern.quote(File.separator);
        sampleFiles = new ArrayList<>();
        HashMap<String, Integer> datasetNumbers = new HashMap<>();
        PeptideCollection matchedPeptides = new PeptideCollection();
        PeptideCollection nonMatchedPeptides = new PeptideCollection();
        PeptideCollection individualPeptides = new PeptideCollection();
        PeptideCollection combinedPeptides = new PeptideCollection();
        proteins = new ProteinCollection();
        proteins = createProteins.createCollection(database, proteins);
        for (String psmFile : psmFiles) {
            //Split file name to check for sample names.
            String[] path = psmFile.split(pattern);
            Boolean newDataset = true;
            String dataset = path[path.length - 4];
            if (!datasetNumbers.isEmpty()) {
                //Create hashmap with dataset names and numbers.
                for (Map.Entry set : datasetNumbers.entrySet()) {
                    if (set.getKey().equals(dataset)) {
                        newDataset = false;
                    }
                }
                if (newDataset) {
                    datasetCount += 1;
                    datasetNumbers.put(dataset, datasetCount);
                }
            } else {
                datasetCount += 1;
                datasetNumbers.put(dataset, datasetCount);
            }
            for (String folder : path) {
                //Gathers sample names to match to the individual database.fasta files.
                if (folder.matches("(" + sampleList.get(1) + ")_?\\d{1,}")) {
                    sampleFiles.add(folder);
                    sampleFiles.add(folder.subSequence(0, sampleList.get(1).length()) + "_" + folder.substring(sampleList.get(1).length()));
                } else if (folder.matches("(" + sampleList.get(0) + ")_?\\d{1,}")) {
                    sampleFiles.add(folder);
                    sampleFiles.add(folder.subSequence(0, sampleList.get(0).length()) + "_" + folder.substring(sampleList.get(0).length()));
                }
            }
            //Gathers individual protein database file.
            String sampleFile = matchSample(sampleFiles);
            //Creates a new collection of peptide objects.
            peptides = new PeptideCollection();
            peptides = peptideCollection.createCollection(psmFile);
            //Matches the peptides to a protein collection.
            proteinMatcher = new MultiThreadDatabaseMatcher(peptides, proteins);
            ArrayList<PeptideCollection> peptidesList = proteinMatcher.getMatchedPeptides(peptides, proteins, threads);
            //Add peptides that matched to this collection.
            matchedPeptides.getPeptides().addAll(peptidesList.get(0).getPeptides());
            //Add peptides that did not match to this collection.
            nonMatchedPeptides.getPeptides().addAll(peptidesList.get(1).getPeptides());
            //Creates the individual protein collection.
            individualProteins = new ProteinCollection();
            individualProteins = createProteins.createCollection(sampleFile, individualProteins);
            //Matches the non matched peptides tot the individual database.
            PeptideCollection nonMatchedIndividuals = sampleProteinMatcher.matchToProteins(nonMatchedPeptides, individualProteins);
            //Add the individuals that did not match to this collection.
            individualPeptides.getPeptides().addAll(nonMatchedIndividuals.getPeptides());
        }
        //Creates a combined protein collection.
        combinedProteins = new ProteinCollection();
        combinedProteins = createProteins.createCollection(combinedDatabase, combinedProteins);
        //Matches the combined protein collection to
        combinedPeptides = sampleProteinMatcher.matchToProteins(nonMatchedPeptides, combinedProteins);
        //List of peptide collections.
        ArrayList<PeptideCollection> finalPeptides = new ArrayList<>();
        ArrayList<String> rnaSeq  = new ArrayList<>();
        finalPeptides.add(matchedPeptides);     //Database peptides
        finalPeptides.add(combinedPeptides);    //Combined peptides
        finalPeptides.add(individualPeptides);  //Individual peptides
        //Currently only hardcoded code in here.
        rnaSeq.add("Uniprot");
        rnaSeq.add("Combined");
        rnaSeq.add("Individual");
        //Determines the sample size for sample indices.
        Integer sampleSize = 0;
        if (copdSampleSize > healthySampleSize) {
            sampleSize = copdSampleSize;
        } else {
            sampleSize = healthySampleSize;
        }
        //Creates output file at the specified output path.
        for (int i = 0; i < finalPeptides.size(); i++) {
            //Create output file in the given output path
            String outputPath = output + rnaSeq.get(i) +  "_scan_data.csv";
//            HashSet<ArrayList<String>> peptideMatrix = scanMatrixCreator.createScanMatrix(finalPeptides.get(i), sampleList, sampleSize);
//            peptideMatrix = setValues.addArrayValues(finalPeptides.get(i), peptideMatrix, sampleList ,datasetNumbers, sampleSize);
            csvWriter.generateCsvFile(finalPeptides.get(i), outputPath, sampleList, sampleSize);
        }
    }

        /**
     * Matches the sample name to the database fasta files.
     * @param sampleType name of the sample: COPD1/COPD_1/Control/Control_1.
     * @return matched database file.
     */
    private String matchSample(ArrayList<String> sampleType) {
        //Used to match sample and sample database.
        String data = "";
        Boolean isFasta = false;
        //List of individual database fasta files is matched to sample names.
        //If a name matched the peptides can matched to the database file
        for (String fasta: fastaFiles) {
            for (String sample: sampleType) {
                if (fasta.contains(sample)) {
                    data = fasta;
                    isFasta = true;
                    break;
                }
            }
        } if (!isFasta) {
            System.out.println("WARNING: the sample(s) in list " + sampleType + " has/have no matching fasta file.");
        }
        //If a name matched the peptides can matched to the database file
        //Otherwise a warning is displayed.
        return data;
    }
}

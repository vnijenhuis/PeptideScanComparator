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
import matcher.CombinedIndividualDatabaseMatcher;
import matcher.IndividualDatabaseMatcher;
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
    private final CombinedIndividualDatabaseMatcher combinedMatcher;
    private final IndividualDatabaseMatcher individualMatcher;
    private PeptideCollection matchedPeptides;
    private PeptideCollection nonMatchedPeptides;
    private PeptideCollection individualPeptides;
    private PeptideCollection combinedPeptides;
    private PeptideCollection nonMatchedIndividuals;
    private ArrayList<String> fastaFiles;
    private ArrayList<String> sampleFiles;
    private ProteinCollection combinedProteins;
    private String database;
    private Integer threads;

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
        //Matches to the combined database.
        combinedMatcher = new CombinedIndividualDatabaseMatcher();
        //Matches to the individual database.
        individualMatcher = new IndividualDatabaseMatcher();
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
            String combinedDatabase = cmd.getOptionValue("cdb");
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
        for (String psmFile : psmFiles) {
            String[] path = psmFile.split(pattern);
            Boolean newDataset = true;
            String dataset = path[path.length - 4];
            if (!datasetNumbers.isEmpty()) {
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
                    sampleFiles.add(folder.subSequence(0, 4) + "_" + folder.substring(sampleList.get(1).length()));
                } else if (folder.matches("(" + sampleList.get(0) + ")_?\\d{1,}")) {
                    sampleFiles.add(folder);
                    sampleFiles.add(folder.subSequence(0, 7) + "_" + folder.substring(sampleList.get(0).length()));
                }
            }
        }
        Integer sampleSize = 0;
        if (copdSampleSize > healthySampleSize) {
            sampleSize = copdSampleSize;
        } else {
            sampleSize = healthySampleSize;
        }
        matchedPeptides = new PeptideCollection();
        nonMatchedPeptides = new PeptideCollection();
        individualPeptides = new PeptideCollection();
        combinedPeptides = new PeptideCollection();
        proteins = new ProteinCollection();
        proteins = createProteins.createCollection(database, proteins);
        for (String psmFile : psmFiles) {
            peptides = new PeptideCollection();
            peptides = peptideCollection.createCollection(psmFile);
            ArrayList<PeptideCollection> peptidesList = new ArrayList<>();
            proteinMatcher = new MultiThreadDatabaseMatcher(peptides, proteins);
            peptidesList = proteinMatcher.getMatchedPeptides(peptides, proteins, threads);
            matchedPeptides.getPeptides().addAll(peptidesList.get(0).getPeptides());
            nonMatchedPeptides.getPeptides().addAll(peptidesList.get(1).getPeptides());
            nonMatchedIndividuals = individualMatcher.matchToIndividual(nonMatchedPeptides, proteins);
            individualPeptides.getPeptides().addAll(nonMatchedIndividuals.getPeptides());
        }
        combinedProteins = new ProteinCollection();
        combinedProteins = createProteins.createCollection(database, combinedProteins);
        combinedPeptides = combinedMatcher.matchToCombined(nonMatchedPeptides, proteins);
        ArrayList<PeptideCollection> finalPeptides = new ArrayList<>();
        ArrayList<String> rnaSeq  = new ArrayList<>();
        finalPeptides.add(matchedPeptides);     //Database peptides
        finalPeptides.add(combinedPeptides);    //Combined peptides
        finalPeptides.add(individualPeptides);  //Individual peptides
        rnaSeq.add("Uniprot");
        rnaSeq.add("Combined");
        rnaSeq.add("Individual");
        //Creates output file at the specified output path.
        for (int i = 0; i < finalPeptides.size(); i++) {
            HashSet<ArrayList<String>> peptideMatrix = new HashSet<>();
            String outputPath = output + rnaSeq.get(i) +  "_scan_data.csv";
            peptideMatrix = scanMatrixCreator.createScanMatrix(finalPeptides.get(i), sampleList, sampleSize);
            peptideMatrix = setValues.addArrayValues(finalPeptides.get(i), peptideMatrix, sampleList ,datasetNumbers, sampleSize);
            csvWriter.generateCsvFile(peptideMatrix, outputPath, sampleList, sampleSize);
        }
    }
}

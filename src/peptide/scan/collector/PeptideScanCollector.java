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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import matrix.PeptideScanMatrixCreator;
import matrix.ScanIDComparator;
import matrix.ScanValueSetter;
import objects.Peptide;
import tools.CsvWriter;
import tools.SampleSizeGenerator;
import tools.ValidFileChecker;

/**
 * Gathers peptide fragmentation data such as file number and scan ID.
 *
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
     * Creates a collection of peptide objects.
     */
    private final PeptideCollectionCreator peptideCollection;

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
     * Amount of threads used.
     */
    private Integer threads;

    /**
     * Matches scan ID's to each other.
     */
    private ScanIDComparator scanMatcher;
    /**
     * List of individual PSM files.
     */
    private ArrayList<String> individualPSMList;
    /**
     * List of combined PSM files.
     */
    private ArrayList<String> combinedPSMList;
    /**
     * List of uniprot PSM files.
     */
    private ArrayList<String> uniprotPSMList;

    /**
     * Private constructor.
     */
    private PeptideScanCollector() {
        //Create new options.
        options = new Options();
        //Creates a help parameter.
        Option help = Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Help function to display all commandline options.")
                .optionalArg(true)
                .build();
        options.addOption(help);
        //Path to the uniprot mRNASeq data.
        Option uniprot = Option.builder("uniprot")
                .hasArgs()
                .desc("Path to the dataset. (/home/name/1D25/Uniprot/).")
                .build();
        options.addOption(uniprot);
        //Path to the combined mRNASeq data.
        Option combined = Option.builder("combined")
                .hasArgs()
                .desc("Path to the combined database folder (/home/name/1D25/CombinedmRNASeq/)")
                .build();
        options.addOption(combined);
        //Path to the individual mRNASeq data.
        Option individual = Option.builder("individual")
                .hasArgs()
                .desc("Path to the combined database folder (/home/name/1D25/IndividualmRNASeq/)")
                .build();
        options.addOption(individual);
        //Psm file parameter for the psm file name.
        Option psm = Option.builder("psm")
                .hasArg()
                .desc("Name of the psm file. Use double quotes if name contains a space. (DB seach psm.csv).")
                .build();
        options.addOption(psm);
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
    }

    /**
     * Starts checking the input and starts the fragmentation control.
     *
     * @param args command line arguments.
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is already opened by
     * another program.
     */
    private void start(final String[] args) throws ParseException, IOException, InterruptedException, ExecutionException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        sampleList = new ArrayList<>();
        if (Arrays.toString(args).toLowerCase().contains("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Peptide scan collector", options);
            System.exit(0);
        } else {
            //Allocate command line input to variables.
            String[] uniprotPSM = cmd.getOptionValues("uniprot");
            String[] combinedPSM = cmd.getOptionValues("combined");
            String[] individualPSM = cmd.getOptionValues("individual");
            String psmFile = cmd.getOptionValue("psm");
            String output = cmd.getOptionValue("out");
            String controlSample = cmd.getOptionValue("control");
            String targetSample = cmd.getOptionValue("target");
            if (cmd.hasOption("threads")) {
                threads = Integer.parseInt(cmd.getOptionValue("threads"));
            } else {
                threads = 2;
            }
            if (controlSample.isEmpty() || targetSample.isEmpty()) {
                throw new IllegalArgumentException("You forgot to add a target or control sample."
                        + "Please check the -target and -control input.");
            }
            //Check file and folder validity.
            fileChecker.isCsv(psmFile);
            fileChecker.isDirectory(output);
            //Control is added first.
            sampleList.add(controlSample);
            //Target is added second.
            sampleList.add(targetSample);
            //Detect sample size and add all files to a list.
            for (int i = 0; i < uniprotPSM.length; i++) {
                uniprotPSMList = new ArrayList<>();
                combinedPSMList = new ArrayList<>();
                individualPSMList = new ArrayList<>();
                fileChecker.isDirectory(uniprotPSM[i]);
                fileChecker.isDirectory(combinedPSM[i]);
                fileChecker.isDirectory(individualPSM[i]);
                //Creates a list of peptide psm files.
                uniprotPSMList.addAll(fileChecker.checkFileValidity(uniprotPSM[i], psmFile));
                combinedPSMList.addAll(fileChecker.checkFileValidity(combinedPSM[i], psmFile));
                individualPSMList.addAll(fileChecker.checkFileValidity(individualPSM[i], psmFile));
                fragmentationControl(output);
            }
        }
    }

    /**
     * Processes the matrix and peptide files and creates an output file.
     *
     * @param psmFiles PSM files from RNASeq dataset.
     * @param matrixFile matrix created by PeptideIdentificationQualityControl.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is already opened by
     * another program.
     */
    private void fragmentationControl(final String output) throws IOException, InterruptedException, ExecutionException {
        Integer datasetCount = 0;
        //File separator for the given path.
        String separator = Pattern.quote(File.separator);
        HashMap<String, Integer> datasetNumbers = new HashMap<>();
        PeptideCollection finalUniprotPeptides = new PeptideCollection();
        PeptideCollection finalCombinedPeptides = new PeptideCollection();
        PeptideCollection finalIndividualPeptides = new PeptideCollection();
        //Creates a protein object collection.
        //Iterates through all psm files inside the list.
//        for (int i = 0; i < uniprotPSMList.size(); i++) {
        String method = "";
        ArrayList<String> rnaSeqs = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Boolean newDataset = true;
            String[] uniprotPath = uniprotPSMList.get(i).split(separator);
            String uniprot = uniprotPath[uniprotPath.length - 4] + " " + uniprotPath[uniprotPath.length - 3];
            method = uniprotPath[uniprotPath.length - 4];
            String[] combinedPath = combinedPSMList.get(i).split(separator);
            String combined = combinedPath[combinedPath.length - 4] + " " + combinedPath[combinedPath.length - 3];
            String[] individualPath = individualPSMList.get(i).split(separator);
            String individual = individualPath[individualPath.length - 4] + " " + individualPath[individualPath.length - 3];
            if (rnaSeqs.isEmpty()) {
                rnaSeqs.add(uniprotPath[uniprotPath.length - 3]);
                rnaSeqs.add(combinedPath[combinedPath.length - 3]);
                rnaSeqs.add(individualPath[individualPath.length - 3]);
            }
            if (!datasetNumbers.isEmpty()) {
                //Create hashmap with dataset names and numbers.
                for (Map.Entry set : datasetNumbers.entrySet()) {
                    if (set.getKey().equals(method)) {
                        newDataset = false;
                    }
                }
                //Add new dataset and corresponding number to the hashmap.
                if (newDataset) {
                    datasetCount += 1;
                    datasetNumbers.put(method, datasetCount);
                }
            } else {
                datasetCount += 1;
                datasetNumbers.put(method, datasetCount);
            }
            //Create new peptide collections.
            PeptideCollection uniprotSamplePeptides = new PeptideCollection();
            PeptideCollection combinedSamplePeptides = new PeptideCollection();
            PeptideCollection individualSamplePeptides = new PeptideCollection();
            //Add peptide objects to the collections.
            uniprotSamplePeptides = peptideCollection.createPeptideCollection(uniprotPSMList.get(i), uniprot, sampleList);
            combinedSamplePeptides = peptideCollection.createPeptideCollection(combinedPSMList.get(i), combined, sampleList);
            individualSamplePeptides = peptideCollection.createPeptideCollection(individualPSMList.get(i), individual, sampleList);
            //Match combined peptides to the uniprot peptides.
            scanMatcher = new ScanIDComparator(uniprotSamplePeptides, combinedSamplePeptides);
            PeptideCollection matchedCombinedPeptides = scanMatcher.matchPeptideScanIDs(uniprotSamplePeptides, combinedSamplePeptides, threads);
            //Match individual peptides to the uniprot peptides.
            scanMatcher = new ScanIDComparator(uniprotSamplePeptides, individualSamplePeptides);
            PeptideCollection matchedIndividualPeptides = scanMatcher.matchPeptideScanIDs(uniprotSamplePeptides, individualSamplePeptides, threads);
            finalUniprotPeptides.getPeptides().addAll(uniprotSamplePeptides.getPeptides());
            finalCombinedPeptides.getPeptides().addAll(matchedCombinedPeptides.getPeptides());
            finalIndividualPeptides.getPeptides().addAll(matchedIndividualPeptides.getPeptides());
        }
        ArrayList<PeptideCollection> finalPeptides = new ArrayList<>();
        finalPeptides.add(finalUniprotPeptides);
        finalPeptides.add(finalCombinedPeptides);
        finalPeptides.add(finalIndividualPeptides);
        for (int i = 0; i < finalPeptides.size(); i++) {
            //Create output file in the given output path
            String outputPath = output + method + "_" + rnaSeqs.get(i) +  "_scan_data.csv";
//            HashSet<ArrayList<String>> peptideMatrix = scanMatrixCreator.createScanMatrix(finalPeptides.get(i), sampleList, sampleSize);
//            peptideMatrix = setValues.addArrayValues(finalPeptides.get(i), peptideMatrix, sampleList ,datasetNumbers, sampleSize);
            csvWriter.generateCsvFile(finalPeptides.get(i), outputPath, sampleList);
        }
    }
}

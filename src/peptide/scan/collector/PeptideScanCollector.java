/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package peptide.scan.collector;

import collections.ScanIDCollection;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import collection.creator.ScanIDCollectionCreator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import matrix.ScanIDComparator;
import filewriter.CsvWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import objects.ScanID;
import tools.SampleSizeGenerator;
import tools.ValidFileChecker;

/**
 * Gathers peptide fragmentation data such as file number and scan ID.
 *
 * @author vnijenhuis
 */
public class PeptideScanCollector {

    /**
     * @param args the command line arguments contains values for each commandline argument.
     * @throws org.apache.commons.cli.ParseException Error encountered during parsing, parameter(s) missing etc.
     * @throws java.io.IOException could not find or open file, file is being used by another program.
     * @throws java.lang.InterruptedException program was interrupted.
     * @throws java.util.concurrent.ExecutionException error in execution of the program.
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
    private final ScanIDCollectionCreator scanCollection;

    /**
     * Csv file writer.
     */
    private final CsvWriter csvWriter;

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
        Option thread = Option.builder("threads")
                .hasArg()
                .optionalArg(true)
                .desc("Amount of threads to use for this execution. (DEFAULTL: 2 threads)")
                .build();
        options.addOption(thread);
        //Checks the input files.
        fileChecker = new ValidFileChecker();
        //Creates peptide object collections.
        scanCollection = new ScanIDCollectionCreator();
        //Writes data to a csv file.
        csvWriter = new CsvWriter();
    }

    /**
     * Starts checking the input and starts the fragmentation control.
     *
     * @param args command line arguments.
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually
     * appears when a file is already opened by another program.
     */
    private void start(final String[] args) throws ParseException, IOException, InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis() / 1000;
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        sampleList = new ArrayList<>();
        //Help function.
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
            //Set the amount of threads to be used.
            if (cmd.hasOption("threads")) {
                threads = Integer.parseInt(cmd.getOptionValue("threads"));
            } else {
                threads = 2;
            }
            //Check file and folder validity.
            fileChecker.isCsv(psmFile);
            fileChecker.isDirectory(output);
            //Control is added first.
            int sampleSize = 0;
            //Detect sample size and add all files to a list.
            for (int i = 0; i < uniprotPSM.length; i++) {
                uniprotPSMList = new ArrayList<>();
                combinedPSMList = new ArrayList<>();
                individualPSMList = new ArrayList<>();
                //Check if given path is a directory.
                fileChecker.isDirectory(uniprotPSM[i]);
                fileChecker.isDirectory(combinedPSM[i]);
                fileChecker.isDirectory(individualPSM[i]);
                //Creates a list of peptide psm files from each directory.
                uniprotPSMList.addAll(fileChecker.checkFileValidity(uniprotPSM[i], psmFile));
                combinedPSMList.addAll(fileChecker.checkFileValidity(combinedPSM[i], psmFile));
                individualPSMList.addAll(fileChecker.checkFileValidity(individualPSM[i], psmFile));
                for (String file: uniprotPSMList) {
                    String[] folders = file.split("\\\\");
                    String sample = folders[folders.length-2];
                    int newSize = (Integer.parseInt(sample.replaceAll("[A-Za-z]*", "")));
                    if (sampleSize < newSize) {
                        sampleSize = newSize;
                    }
                    sample = sample.replaceAll("\\d", "");
                    if (!sampleList.contains(sample)) {
                        sampleList.add(sample);
                    }
                }
                fragmentationControl(output, sampleSize);
            }
        }
        long endTime = System.currentTimeMillis() / 1000;
        System.out.println("Process took " + (endTime - startTime) + " seconds.");
    }

    /**
     * Gathers matching data from all psm files.
     *
     * @throws IOException couldn't open/find the specified file. Usually
     * appears when a file is already opened by another program.
     */
    private void fragmentationControl(final String output, final Integer sampleSize)
            throws IOException, InterruptedException, ExecutionException {
        //File separator for the given path.
        String separator = Pattern.quote(File.separator);
        //Split on the separator to get each folder name.
        String[] uniprotFolders = uniprotPSMList.get(0).split(separator);
        String[] combinedFolders = combinedPSMList.get(0).split(separator);
        String[] individualFolders = individualPSMList.get(0).split(separator);
        //Use folder name to get dataset and method name.
        String uniprot = uniprotFolders[uniprotFolders.length - 3];
        String combined = combinedFolders[combinedFolders.length - 3];
        String individual = individualFolders[individualFolders.length - 3];
        String method = uniprotFolders[uniprotFolders.length - 4];
        //Create a list with all dataset names.
        ArrayList<String> datasets = new ArrayList<>();
        datasets.add(uniprot);
        datasets.add(combined);
        datasets.add(individual);
        //Gathers all uniprot scan ids.
        HashMap<String, ArrayList<ScanID>> uniprotScans = scanCollection.createScanCollection(uniprotPSMList, uniprot, method, datasets, sampleList);
        //Gathers all combined scn ids.
        HashMap<String, ArrayList<ScanID>> combinedScans = scanCollection.createScanCollection(combinedPSMList, combined, method, datasets, sampleList);
        //Gathers all individual scan ids.
        HashMap<String, ArrayList<ScanID>> individualScans = scanCollection.createScanCollection(individualPSMList, individual, method, datasets, sampleList);
        //Matches uniprot scan ids with the combined dataset. Matched combined scan id sequences are added to the uniprot dataset.
        scanMatcher = new ScanIDComparator(uniprotScans, combinedScans, combined, datasets);
        HashMap<String, ArrayList<ScanID>> matchedScans = scanMatcher.matchPeptideScanIDs(uniprotScans, combinedScans, threads, combined, datasets);
        //Matches uniprot scan ids with the individual dataset. Matched individual scan id sequences are added to the uniprot dataset.
        scanMatcher = new ScanIDComparator(matchedScans, individualScans, individual, datasets);
        HashMap<String, ArrayList<ScanID>> finalScans = scanMatcher.matchPeptideScanIDs(uniprotScans, individualScans, threads, individual, datasets);
        ScanIDCollection finalScanCollection = new ScanIDCollection();
        //Add all scan IDs to a final scan collection.
        for (Map.Entry<String, ArrayList<ScanID>> entry : finalScans.entrySet()) {
            for (ScanID scanEntry: entry.getValue()) {
                finalScanCollection.addScanID(scanEntry);
            }
        }
        //Create output file in the given output path
        String outputPath = output + method + "_scan_data.csv";
        //Write data to the output path.
        csvWriter.generateCsvFile(finalScanCollection, outputPath, datasets);
    }
}
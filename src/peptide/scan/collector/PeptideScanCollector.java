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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;
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
    public static void main(String[] args) throws ParseException, IOException {
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
        //Database path parmeter
        Option dbPath = Option.builder("db")
                .hasArg()
                .desc("Path to the database folder (/home/name/Databases/uniprot.fasta.gz)")
                .build();
        options.addOption(dbPath);
        //Path and name of the output file.
        Option output = Option.builder("out")
                .hasArg()
                .desc("Path to write output file to.  (/home/name/Combined/Matrix/).")
                .build();
        options.addOption(output);
                //Add sample names.
        Option target = Option.builder("target")
                .hasArg()
                .desc("Give the name of the target sample (example: COPD) (CASE SENSITIVE!)")
                .build();
        options.addOption(target);
        Option control = Option.builder("control")
                .hasArg()
                .desc("Give the name of the control sample (example: Control) (CASE SENSITIVE!)")
                .build();
        options.addOption(control);
        //Checks the input files.
        fileChecker = new ValidFileChecker();
        //Creates a peptide collection
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
     * @param args command line arguments.
     * @throws FileNotFoundException file was not found/does not exist.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    private void start(final String[] args) throws ParseException, IOException {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        psmFiles = new ArrayList<>();
        sampleList = new ArrayList<>();
        Integer copdSampleSize = 0;
        Integer healthySampleSize = 0;
        if (args.toString().toLowerCase().contains("help") || args.toString().toLowerCase().contains("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Peptide scan collecter", options );
            System.exit(0);
        } else {
            //Allocate command line input to variables.
            String[] path = cmd.getOptionValues("in");
            String psmFile = cmd.getOptionValue("psm");
            String output = cmd.getOptionValue("out");
            String database = cmd.getOptionValue("db");
            String controlSample = cmd.getOptionValue("control");
            String targetSample = cmd.getOptionValue("target");
            if (controlSample.isEmpty() || targetSample.isEmpty()) {
                throw new IllegalArgumentException("You forgot to add a target or control sample."
                        + "Please check the -target and -control input.");
            }
            //Check file and folder validity.
            fileChecker.isCsv(psmFile);
            fileChecker.isFasta(database);
            fileChecker.isDirectory(output);
            //Control is added first.
            sampleList.add(controlSample);
            //Target is added second.
            sampleList.add(targetSample);
            //Detect sample size and add all files to a list.
            for (String folder: path) {
                fileChecker.isDirectory(folder);
                SampleSizeGenerator sizeGenerator = new SampleSizeGenerator();
                ArrayList<Integer> sampleSize = sizeGenerator.getSamples(folder, sampleList);
                fileChecker.checkFileValidity(folder, psmFile, psmFiles);
                if (sampleSize.get(0) > healthySampleSize) {
                    healthySampleSize = sampleSize.get(0);
                }
                //Gets the highest copd sample size.
                if (sampleSize.get(1) > copdSampleSize) {
                    copdSampleSize = sampleSize.get(1);
                }
                fragmentationControl(output, copdSampleSize, healthySampleSize);
            }
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
            final Integer healthySampleSize) throws IOException {
        Integer datasetCount = 0;
        String pattern = Pattern.quote(File.separator);
        HashMap<String, Integer> datasetNumbers = new HashMap<>();
        for (int sample = 0; sample < psmFiles.size(); sample++) {
            String[] path = psmFiles.get(sample).split(pattern);
            Boolean newDataset = true;
            String dataset = path[path.length-4];
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
        }
        Integer sampleSize = 0;
        if (copdSampleSize > healthySampleSize) {
            sampleSize = copdSampleSize;
        } else {
            sampleSize = healthySampleSize;
        }
            PeptideCollection finalCollection = new PeptideCollection();
            for (int sample = 0; sample < psmFiles.size(); sample++) {
                peptides = new PeptideCollection();
                peptides = peptideCollection.createCollection(psmFiles.get(sample));
                finalCollection.getPeptides().addAll(peptides.getPeptides());
            }
        //Creates output file at the specified output path.
        HashSet<ArrayList<String>> peptideMatrix = new HashSet<>();
        peptideMatrix = scanMatrixCreator.createScanMatrix(finalCollection, sampleList, sampleSize);
        peptideMatrix = setValues.addArrayValues(finalCollection, peptideMatrix, sampleList ,datasetNumbers, sampleSize);
        for (ArrayList<String> p: peptideMatrix) {
            System.out.println(p);
        }
        System.exit(0);
        csvWriter.generateCsvFile(peptideMatrix, output, sampleList, sampleSize);
    }
}

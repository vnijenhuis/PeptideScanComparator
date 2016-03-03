/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to test if files in a given path are valid.
 * @author vnijenhuis
 */
public class ValidFileChecker {
    /**
     * Checks if files exist and add them to an array for further usage.
     * @param filePath path to the file(s).
     * @param fileName string that is unique to the file name. (to prevent unnecessary input).
     * @param fileList list of files to add entries to.
     * @return ArrayList with path as String.
     * @throws IOException couldn't open/find the specified file. Usually appears when a file is
     * already opened by another program.
     */
    public ArrayList<String> checkFileValidity(final String filePath, String fileName, ArrayList<String> fileList) throws IOException {
        //Go through all folders and files in a given path.
        File file = new File(filePath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
          });
        //Goes through all sample folders inside the RNASeq folder and creates a list of files.
        for (String sample: directories) {
            File path = new File(filePath + sample);
            for (File f: path.listFiles()) {
                if (f.toString().contains(fileName) && isCsv(f.toString())) {
                    System.out.println("Found file " + f);
                    fileList.add(f.toString());
                }
            }
        }
        return fileList;
    }

    /**
     * Checks if the input string is an existing file.
     * @param file input string which contains a path to a file.
     * @return file as string.
     * @throws IllegalArgumentException not a file.
     */
    public final Boolean isFile(final String file){
        File checkFile = new File(file);
        if (!checkFile.isFile()) {
            throw new IllegalArgumentException("Invalid file found:" + file);
        }
        return true;
    }

    /**
     * Checks if the input string is an existing directory.
     * @param path path of the directory.
     * @return path as String.
     * @throws IllegalArgumentException not a directory.
     */
    public final Boolean isDirectory(final String path){
        File checkPath = new File(path);
        if (!checkPath.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory found: " + path);
        }
        return true;
    }

    /**
     * Check is a file is a fasta file.
     * @param file file name as string.
     * @return true if valid, commandline exception if invalid
     */
    public final Boolean isFasta(final String file) {
        File checkFile = new File(file);
        if (!checkFile.isFile()) {
            throw new IllegalArgumentException("Invalid file found:" + file);
        }
        //Matches fastas files.
        if (!file.matches(".*\\.fa(sta){0,1}")) {
            throw new IllegalArgumentException("Invalid fasta file found: " + file);
        }
        return true;
    }

    /**
     * Check is a file is a csv file.
     * @param file file name as string.
     * @return true if valid, commandline exception if invalid
     */
    public final Boolean isCsv(final String file) {
        File checkFile = new File(file);
        if (!checkFile.isFile()) {
            throw new IllegalArgumentException("Invalid file found:" + file);
        }
        //Matches csv files.
        if (!file.matches(".*\\.csv")) {
            throw new IllegalArgumentException("Invalid fasta file found: " + file);
        }
        return true;
    }
}

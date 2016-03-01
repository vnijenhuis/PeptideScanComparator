/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
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
     * @throws IOException can't open/find the specified file or directory.
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
          for (String sample: directories) {
              File path = new File(filePath + sample);
              for (File f: path.listFiles()) {
                  if (f.toString().contains(fileName)) {
                      System.out.println("Found " + f);
                      fileList.add(f.toString());
                  }
              }
          }
        return fileList;
    }
}

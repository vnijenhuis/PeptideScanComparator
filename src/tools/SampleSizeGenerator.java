/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Determines the highest sampleSize of COPD and Healthy samples.
 * @author vnijenhuis
 */
public class SampleSizeGenerator {
    /**
     * Gathers sample numbers from the files.
     * @param filePath path of the files.
     * @param sampleList list of samples.
     * @return list of samples sizes: Healthy on index 0, COPD on index 1.
     */
    public final ArrayList<Integer> getSamples(final String filePath, final ArrayList<String> sampleList) {
        File file = new File(filePath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        Integer targetSampleSize = 0;
        Integer controlSampleSize = 0;
        String controlSample = sampleList.get(0);
        String targetSample = sampleList.get(1);
        //Goes through all sample folders inside the RNASeq folder.
        for (String sample: directories) {
            //Matches to the target sample(s)
            if (sample.contains(targetSample)) {
                int index = (Integer.parseInt(sample.substring(targetSample.length())));
                //Amount of copd samples.
                if (targetSampleSize < index) {
                    targetSampleSize = index;
                }
            }
            //Matches to the control sample(s).
            if (sample.contains(controlSample)) {
                int index = (Integer.parseInt(sample.substring(controlSample.length())));
                //Amount of control samples.
                if (controlSampleSize < index) {
                    controlSampleSize = index;
                }
            } 
        }
        //Returns sample sizes of COPD and Healthy.
        ArrayList<Integer> sampleSize = new ArrayList<>();
        sampleSize.add(controlSampleSize);
        sampleSize.add(targetSampleSize);
        //Matrix is generated based on the biggest samplesize.
        return sampleSize;
    }
}

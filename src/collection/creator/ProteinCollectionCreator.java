/*
 * @author Vikthor Nijenhuis
 * @project peptide spectrum identification quality control  *
 */
package collection.creator;

import objects.Protein;
import collections.ProteinCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

/**
 * Creates a collection of protein objects.
 * @author vnijenhuis
 */
public class ProteinCollectionCreator {
    /**
     * Buffered reader for file reading.
     */
    private BufferedReader dbReader;

    /**
     * Reads proteins.fasta files or database file such as the uniprot-database.fasta.gz
     * and creates a protein collection.
     * @param database database file.
     * @param proteins protein collection.
     * @return returns a collection of proteins.
     */
    public final ProteinCollection createCollection(final String database,
            final ProteinCollection proteins) {
        try {
            System.out.println("Loading database proteins from " + database);
            File file = new File(database);
            //Read database files. Can read .fasta and .fasta.gz files.
            if (database.matches(".*\\.fa(sta){0,1}\\.gz")) {
                InputStream fileStream = new FileInputStream(file);
                InputStream gzipStream = new GZIPInputStream(fileStream);
                Reader decoder = new InputStreamReader(gzipStream, "US-ASCII");
                dbReader = new BufferedReader(decoder);
            } else if (database.matches(".*\\.fa(sta){0,1}")){
                FileReader fr = new FileReader(file);
                dbReader = new BufferedReader(fr);
            }
            String line;
            boolean firstLine = true;
            String sequence = "";
            String accession = "";
            //Create protein objects with a sequence.
            while ((line = dbReader.readLine()) != null) {
                if (line.startsWith(">") && firstLine) {
                    accession = line.split(" ")[0].replace(">", "");
                    firstLine = false;
                } else if (line.startsWith(">")) {
                    Protein protein = new Protein(sequence, accession);
                    proteins.addProtein(protein);
                    accession = line.split(" ")[0].replace(">", "");
                    sequence = "";
                } else {
                    sequence += line.trim();
                }
            }
        }   catch (FileNotFoundException ex) {
                System.out.println("File nout found: " + ex.getMessage());
        }   catch (IOException ex) {
                System.out.println("Encountered IO Exception; " + ex.getMessage());
        }
        //Return the protein collection.
        System.out.println("Loaded " + proteins.getProteins().size() + " proteins!");
        return proteins;
    }
}


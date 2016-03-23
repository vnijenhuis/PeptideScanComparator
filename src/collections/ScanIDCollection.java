/*
 * @author Vikthor Nijenhuis
 * @project peptide fragmentation control
 */
package collections;

import java.util.ArrayList;
import objects.ScanID;

/**
 * A collection of Scan ID objects.
 *
 * @author vnijenhuis
 */
public class ScanIDCollection {

    /**
     * Creates a ArrayList for Scan ID objects.
     */
    private final ArrayList<ScanID> scanIDs;

    /**
     * Creates a new ArrayList.
     */
    public ScanIDCollection() {
        scanIDs = new ArrayList<>();
    }

    /**
     * Adds Scan ID objects to the ArrayList.
     * @param scan Scan ID object.
     */
    public final void addScanID(final ScanID scan) {
        scanIDs.add(scan);
    }

    /**
     * Removes Scan ID objects from the ArrayList.
     * @param scan peptide object.
     */
    public final void removeScanID(final ScanID scan) {
        scanIDs.remove(scan);
    }

    /**
     * Returns the ArrayList..
     * @return ArrayList of Scan ID objects..
     */
    public final ArrayList<ScanID> getScanIDs() {
        return scanIDs;
    }
}

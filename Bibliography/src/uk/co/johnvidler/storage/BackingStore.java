package uk.co.johnvidler.storage;

import uk.co.johnvidler.biblio.Entry;

import java.util.List;

/**
 * Backing stores should implement this interface to ensure that they fulfil
 * the calls from the main program.
 *
 * @author John Vidler
 */
public interface BackingStore
{

    /**
     * Returns the total number of stored entries in the backing store.
     * @return The number of entries available
     */
    public long getTotalEntries();

    /**
     * Saves an Entry object in to the backing storage.
     *
     * Should only return true if the operation was actually successful, and the backing store
     * could be restored from disk/network with the new entry intact.
     * 
     * @param entry The entry to save
     * @return True, if the save operation was successful.
     */
    public boolean saveEntry( Entry entry );

    /**
     * Should return a List of string-type keys
     * @return
     */
    public List<String> getKeysAsList();

    /**
     * Loads entries from the backing store by key.
     * @param key The key to look up
     * @return A list of Entry objects. May be of zero length.
     */
    public List<Entry> loadEntryByKey(String key);

    /**
     * Loads entries from the backing store by title.
     * @param key The key to look up
     * @return A list of Entry objects. May be of zero length.
     */
    public List<Entry> loadEntryByTitle( String title );

}

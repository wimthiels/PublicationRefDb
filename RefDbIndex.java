package publicationRefDb;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
/**
 * A class of indexes for publications containing 1 index structure that maps a string (the search term)
 * to a set of strings (the set of ID).  This class is used by the reference database as a generic way to set
 * up indexes.
 * 
 * @invar	each RefDbIndex has a proper index associated with it (hasProperIndex())
 *        
 * @author Wim Thiels
 *
 */
public class RefDbIndex {

	private Map<String, Set<String>> index;

	/**
	 * Initialise this new RefDbIndex as an empty dictionary that maps a string (the search term)
	 * to a set of strings (the set of ID)
	 * 
	 * @post	a new index is constructed with an empty index
	 * 
	 */
	public RefDbIndex() {
		index = new HashMap<String, Set<String>>();
	}

	/**
	 * get the full index 
	 * @return	the complete index of this RefDbIndex as a map
	 */
	public Map<String, Set<String>> getIndex() {
		return index;
	}

	/**
	 * get the set of IDs for the given key.
	 * If the key is not present, null will be returned
	 * @param 	key
	 * 			the key for which the set of IDs must be retrieved
	 * @return	the set of ID corresponding to the given key
	 * 			or null if the key is not found
	 */
	public Set<String> getIdSetForKey(String key) {
		return getIndex().get(key);
	}
	/**
	 * check if the given entry in the index is valid
	 * 
	 * @param entry
	 *			the tuple of the index to be checked
	 * @return true if 
	 * 			-the key refers to a set of IDs that is effective, 
	 * 		   	-the number of IDs in that set is bigger than zero, 
	 * 			-for every ID in that set, the ID is present in the ID-table of the reference database
	 *         otherwise false
	 */
	private boolean isValidIndexTuple(Entry<String, Set<String>> entry) {
		// rem: no test that that check publication itself, that is covered by the 
		// class invariant of the idTable
		Set<String> idSet = entry.getValue();

		if (idSet == null)
			return false;

		if (idSet.size() == 0)
			return false;
		
		for (String id :idSet) {
			if (!RefDb.hasIdinDb(id)) return false;
		}

		return true;
	}
	/**
	 * check if this RefDbIndex has a proper index-structure associated with it
	 * 
	 * @return 	true if every tuple in the index is valid (isValidIndexTuple() 
	 *         	otherwise false
	 */
	public boolean hasProperIndex() {
		for (Map.Entry<String, Set<String>> entry : getIndex().entrySet()) {
			if (!isValidIndexTuple(entry))
				return false;

		}
		return true;
	}
}
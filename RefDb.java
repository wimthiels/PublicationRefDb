/**
 * 
 */
package publicationRefDb;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * A class of reference databases for publications involving 
 * 	-> idTable : the main table linking a unique ID to a publication 
 * 	-> authorIndex : an index associating an authorname with the IDs of all his/her publications  
 * 	-> titleWordIndex : an index associating a word to all the IDs of the publications that have that word in the title
 * 
 * @invar	each reference database has a proper idTable associated with it (hasProperIdTable())
 * @invar	each reference database has a proper author index associated with it (hasProperAuthorIndex())
 * @invar	each reference database has a proper title word Index associated with it (hasProperTitleWordIndex())
 *        
 * @author Wim Thiels
 *
 */
public final class RefDb {

	public final static String RegexWordSplit = "[ ./@,;+{}()\"&:-]+";

	/**
	 * get the full idTable
	 * 
	 * @return idTable is given as a map
	 */
	private static Map<String, Publication> getIdTable() {
		return idTable;
	}
	
	
	/**
	 * get the number of publications in the reference database
	 */
	public static int getNbPublications() {
		return getIdTable().size();
	}

	/**
	 * get the Publication associated with the given referenceId
	 * if the ID is not valid or not present, null will be returned. 
	 * (use hasIdinDb(id),or isValidid() to do checks upfront if necessary)
	 * 
	 * @param 	referenceId
	 *			the referenceId that is associated with the publication
	 * @return	the publication associated with the given referenceId 
	 * 			if the ID is not present in the DB, null is returned

	 */
	public static Publication getPublicationById(String id) {
		if (!hasIdinDb(id))
			return null;

		return getIdTable().get(id);
	}

	/**
	 * check is the given Id is a valid ID
	 * 
	 * @param 	Id
	 *          the Id to be checked
	 * @return 	true if the Id is not null otherwise false
	 */
	private static boolean isValidId(String Id) {
		return (Id != null);
	}
	
	/**
	 * gives back a set of publications corresponding to the set of given IDs
	 * @param 	idSet
	 * 			the set of IDs to convert to map to publications
	 * @return	a set of publications corresponding to the set of given IDs
	 */
	
	private static Set<Publication> convertSetOfIdsToSetOfPublications(Set<String> idSet) {
		Set<Publication> publicationSet = new HashSet<>();
		Map<String, Publication> idTable = getIdTable();
		for (String id : idSet) {
			Publication publication = idTable.get(id);
			if (publication != null) //not strictly necessary
				publicationSet.add(publication);
		}
		return publicationSet;
	}
	
	/**
	 * check if the given ID is present in this reference database
	 * @param 	id
	 * 			the ID to check
	 * @return	true if the given ID is valid and
	 * 			the id is a key in the idTable
	 * 			otherwise false
	 */
	public static boolean hasIdinDb(String id) {
		return ((isValidId(id)) && getIdTable().containsKey(id));
	}
	
	
	/**
	 * checks if the given tuple of the idTable is valid
	 * @param 	entry
	 * 			the tuple of the idTable to be checked
	 * @return	true if the publication of the given entry is effective and 
	 * 			the reference id of the publication of the given entry has
	 * 			the same value as the reference id of the given tuple
	 * 			otherwise false
	 */
	private static boolean isValidIdTableTuple(Entry<String, Publication> entry) {
		if (entry.getValue() == null)
			return false;
		if (!(entry.getValue().getReferenceId().equalsIgnoreCase(entry.getKey())))
			return false;
		return true;
	}

	/**
	 * check if this reference database has a proper idTable associated with it
	 * 
	 * @return true if every tuple in the idTable is valid
	 *         (isValidIdTableTuple() otherwise false
	 */
	public static boolean hasProperIdTable() {
		// no need to check for doubles. keys in maps must be unique.
		// the uniqueness of the values (Publication) is implicitly enforced by
		// the mirror-referencing check in isValidIdTableTuple()
		// the consistency between this table and the indexes is done by the
		// class invariants of the index
		for (Map.Entry<String, Publication> entry : getIdTable().entrySet()) {
			if (!isValidIdTableTuple(entry))
				return false;
		}
		return true;
	}

	private static Map<String, Publication> idTable = new HashMap<>();
	
	/**
	 * get the set of publications associated with the given author name (author index)
	 * this authorname must be given as “initialOfFirstName. lastName”, e.g., A. Einstein;
	 * (leading and trailing spaces will be automatically removed)
	 * 
	 * @param 	authorName
	 *			authorName that is used as the key to search the author index
	 * @throws 	AuthorNameIsNullException
	 * @return	the set of publications associated with the given authorname
	 * 			if the authorname is not present in the author index, an empty set is returned

	 */
	public static Set<Publication> getPublicationsByAuthorName(String authorName) throws AuthorNameIsNullException {
		Set<Publication> emptySet = Collections.emptySet();
		if (authorName == null)
			throw new AuthorNameIsNullException();

		if (getAuthorIndex().containsKey(authorName.trim()))
			return convertSetOfIdsToSetOfPublications(getAuthorIndex().get(authorName.trim()));

		return emptySet;
	}
	/**
	 * get the full author index (as a map)
	 * 
	 * @return the author index is given as a map
	 */
	private static Map<String, Set<String>> getAuthorIndex() {
		return getAuthorIndexRaw().getIndex();
	}
	/**
	 * get the full author index (as instantiation of RefDbIndex)
	 * 
	 * @return the author index is given as RefDbIndex
	 */
	private static RefDbIndex getAuthorIndexRaw() {
		return authorIndex;
	}
	
	/**
	 * check if the given entry in the author index is valid
	 * 
	 * @param 	entry
	 *			the tuple of the author index to be checked
	 * @return 	true if for every id in the idset(=value of the given tuple), the corresponding
	 * 			publication has the authorname (=key of the given tuple) as one of its authors
	 * 			otherwise false
	 */
	private static boolean isValidAuthorIndexTuple(Entry<String, Set<String>> entry) {

		String authorNameKey = entry.getKey();
		for (Publication publication : convertSetOfIdsToSetOfPublications(entry.getValue())) {

			boolean authorFound = false;
			for (String authorName : publication.getAllAuthorsWithInitial()) {
				if (authorNameKey.equalsIgnoreCase(authorName)) {
					authorFound = true;
					break;
				}
			}
			if (!authorFound)
				return false;
		}

		return true;
	}
	
	/**
	 * check if this reference database has a proper author index associated with it
	 * 
	 * @return 	true if every tuple in the author index is valid (isValidAuthorIndexTuple() 
	 *         	otherwise false
	 */
	public static boolean hasProperAuthorIndex() {
		// has proper index (generic index check)
		if (!getAuthorIndexRaw().hasProperIndex())
			return false;

		// has proper authorindextuples (specific tests for the authorindex)
		for (Map.Entry<String, Set<String>> entry : getAuthorIndex().entrySet()) {
			if (!isValidAuthorIndexTuple(entry))
				return false;
		}
		return true;
	}

	private static RefDbIndex authorIndex = new RefDbIndex();
	
	/**
	 * get the set of publications that have the given word in their title (title word index)
	 * (case is ignored, leading and trailing spaces will be automatically removed)
	 * 
	 * @param 	authorName
	 *			authorName that is used as the key to search the author index
	 * @throws 	WordIsNullException
	 * @return	the set of publications that have the given word in their title
	 * 			if the word is not present in the title word index, an empty set is returned

	 */
	public static Set<Publication> getPublicationsByTitleWord(String word) throws WordIsNullException {
		if (word == null)
			throw new WordIsNullException();

		Set<Publication> emptySet = Collections.emptySet();

		if (getTitleWordIndex().containsKey(word.trim().toLowerCase()))
			return convertSetOfIdsToSetOfPublications(getTitleWordIndex().get(word.trim().toLowerCase()));

		return emptySet;
	}
	
/**
 * get the full title word index
 * 
 * @return the wordtitle index is given as a map
 */
	private static Map<String, Set<String>> getTitleWordIndex() {
		return getTitleWordIndexRaw().getIndex();
	}

	/**
	 * get the full title word index (as instantiation of RefDbIndex)
	 * 
	 * @return the title word index is given as RefDbIndex
	 */
	private static RefDbIndex getTitleWordIndexRaw() {
		return titleWordIndex;
	}
/**
 * check if the given entry in the titleword index is valid
 * 
 * @param entry
 *			the tuple of the title word index to be checked
 * @return true if 
 * 			-the titleword (key) refers to a set of IDs that is effective, 
 * 		   	-the number of IDs in that set is bigger than zero, 
 * 			-for every ID in that set, the ID is present in the ID-table
 * 			-for every ID in that set, the referenceId of the publication with the given ID is effective, and 
 * 			-for every ID in that set, that titleword (key) is one of the words in the title for the publication with that ID
 *         otherwise false
 */
	private static boolean isValidTitleWordIndexTuple(Entry<String, Set<String>> entry) {

		String titleWordKey = entry.getKey();
		for (Publication publication : convertSetOfIdsToSetOfPublications(entry.getValue())) {
			boolean titleWordFound = false;
			String[] titleWords = publication.getTitle().split(RegexWordSplit);
			for (String titleWord : titleWords) {
				if (titleWordKey.equalsIgnoreCase(titleWord)) {
					titleWordFound = true;
					break;
				}
			}
			if (!titleWordFound)
				return false;
		}

		return true;
	}

/**
 * check if this reference database has a proper title word index associated with it
 * 
 * @return 	true if every tuple in the title word index is valid (isValidTitleWordIndexTuple() 
 *         	otherwise false
 */
	public static boolean hasProperTitleWordIndex() {

		// has proper index (generic index check)
		if (!getTitleWordIndexRaw().hasProperIndex())
			return false;

		// specific tests for the word index
		for (Map.Entry<String, Set<String>> entry : getTitleWordIndex().entrySet()) {
			if (!isValidTitleWordIndexTuple(entry))
				return false;
		}
		return true;
	}

	private static RefDbIndex titleWordIndex = new RefDbIndex();

	/**
	 * @return the idCounter
	 */
	private static long getIdCounter() {
		return idCounter;
	}

	/**
	 * set the idCounter to the given value
	 * 
	 * @param idCounter
	 *            the idCounter to set
	 */
	private static void setIdCounter(long idCounter) {
		RefDb.idCounter = idCounter;
	}

	private static long idCounter = 0;

	/**
	 * add a publication to the reference database. meaning : 
	 * 1) the publication with the given ID will get a unique ID and registered in the idTable 
	 * 2) the authornames of the publication with the given ID will be indexed 
	 * 3) the words in the title of the publication with the given ID will be indexed
	 * 
	 * @param 	publication
	 *          the publication to be added
	 *          
	 * @throws 	PublicationIsNullException
	 * @throws 	DuplicateEntryRefDbException
	 * 
	 * @post 	the publication with the given ID will have a unique referenceID and will be
	 *       	registered in the reference database (idTable + indexes)
	 */
	public static void addPublicationToDb(Publication publication)
			throws PublicationIsNullException, DuplicateEntryRefDbException {
		// cannot be null
		if (publication == null)
			throw new PublicationIsNullException();
		//check already in DB
		if (publication.getReferenceId() != null)
			throw new PublicationAlreadyInDbException();
		//check for similar publication
		if (hasSamePublicationInDb(publication))
			throw new PublicationDuplicateValueException();
		
		// in case of duplicate increment (by resetting key to zero, one could reclaim abandoned keys caused by removal of
		// publications)
		long newId = getIdCounter() + 1;
		while (getIdTable().containsKey(newId))
			newId++;

		// at this point the tuple (key, value) should always be a valid entry
		// set up the link between RefDB and publication (must be done first)
		getIdTable().put(Long.toString(newId), publication);
		try {
			publication.setReferenceId(Long.toString(newId));
		} catch (InputFieldNotValidException e1) {
			assert (false); // can never occur
			e1.printStackTrace();
		}
		
		//update the counter
		setIdCounter(newId);

		// update author index 
		for (int authorRank = 1; authorRank <= publication.getNbAuthors(); authorRank++) {
			try {
				addAuthorNameToIndex(authorRank, Long.toString(newId));
			} catch (InputFieldNotValidException e) {
				assert false; // cannot occur, neutralise these errors
				e.printStackTrace();
			}
		}

		// update title word index
		addTitleWordsToIndex(Long.toString(newId));

	}

	/**
	 * add the author with the given rank of the publication with the given ID to the author index of this reference database
	 * 
	 * (if the given id is not present in the DB or not valid, this method will do nothing, no errors will be given.  
	 * Use checkers hasIdinDb() or isValidId() upfront if necessary)
	 * 
	 * @param 	rank
	 * 			the rank of the author for which the authorname will be added to the author index
	 * @param 	id
	 * 			the id of the publication for which the title words must be added to the title word index
	 * @throws	InputFieldNotValidException
	 * @result	the words of the title of the publication with the given ID will be registered in the title word index
	 */
	public static void addAuthorNameToIndex(int authorRank, String id) throws InputFieldNotValidException {
		// using rank instead of name(string) ensures getting a valid name

		if (!hasIdinDb(id))
			return;

		String authorName = getPublicationById(id).getAuthorWithInitialAt(authorRank);
		// add the publication to the set that is linked to that authorname
		Set<String> idSet = getAuthorIndex().get(authorName);
		// if the author is not present in the index, first initialise
		if (idSet == null)
			idSet = new HashSet<String>();
		idSet.add(id);
		// replace the tuple with the new set of publications
		getAuthorIndex().put(authorName, idSet);
	}
		

	
	/**
	 * add words of the title of the publication with the given ID to the titleword index of this reference database
	 * 
	 * (if the given id is not present in the DB or not valid, this method will do nothing, no errors will be given.  
	 * Use checkers hasIdinDb() or isValidId() upfront if necessary)
	 * 
	 * @param 	id
	 * 			the id of the publication associated with title for which the words must be added to the title word index
	 * 
	 * @result	the words of the title of the publication with the given ID will be registered in the title word index
	 */
	public static void addTitleWordsToIndex(String id) {
		if (!hasIdinDb(id))
			return;

		for (String word : getPublicationById(id).getTitle().toLowerCase().split(RegexWordSplit)) {
			Set<String> idSet = getTitleWordIndex().get(word);
			// for a new word a set must first be initialised
			if (idSet == null)
				idSet = new HashSet<String>();
			// add the publication to the set that is linked to that word
			idSet.add(id);
			getTitleWordIndex().put(word.trim().toLowerCase(), idSet);
		}

	}


	/**
	 * remove a publication from the reference database. meaning : 
	 * 1) the referenceID of the publication with the given ID will be set to null and the ID is removed from the idTable 
	 * 2) the authornames of the publication with the given ID will be removed from the index 
	 * 3) the words in the title of the publication with the given ID will be removed from the index 
	 * 4) all the citations and citators of the publication with the given ID will be removed (unless not registered 
	 * 	  in the reference database
	 * 
	 * if the publication with the given ID is not present in the reference database no error will be given
	 * (if the given id is not present in the DB or not valid, this method will do nothing, no errors will be given.  
	 * Use checkers hasIdinDb() or isValidId() upfront if necessary)
	 * 
	 * @param 	id
	 *          the id of the publication to be removed
	 * @post 	the publication with the given ID will have a non-effective referenceID and is
	 *       	not present the reference database (idTable + indexes)
	 */
	public static void removePublicationFromDb(String id) {
		// if not present in the db, then no need for action (no error given)
		if (!hasIdinDb(id))
			return;

		Publication publication = getPublicationById(id);
		// remove authors from the author index
		for (int authorRank = publication.getNbAuthors(); authorRank > 0; authorRank--) {
			try {
				removeAuthorNameFromIndex(authorRank, id);
			} catch (InputFieldNotValidException e) {
				assert false; // errors cannot occur
				e.printStackTrace();
			}
		}
		// remove title words from index
		removeTitleWordsFromIndex(id);

		// remove the cites relations //only if registered
		Set<Publication> citationSet = publication.getAllCitations();
		Publication[] citationArray = citationSet.toArray(new Publication[citationSet.size()]);
		for (Publication citation : citationArray) {
			if (citation.hasReferenceId())
				publication.removeAsCitation(citation);
		}

		// remove the cited by relations //only if registered
		Set<Publication> citatorSet = publication.getAllCitators();
		Publication[] citatorArray = citatorSet.toArray(new Publication[citatorSet.size()]);
		for (Publication citator : citatorArray) {
			if (citator.hasReferenceId())
				citator.removeAsCitation(publication);
		}

		// break link between refDB and publication (must be done last)
		getIdTable().remove(id);
		try {
			publication.setReferenceId(null);
		} catch (InputFieldNotValidException e) {
			assert (false); // can never occur
			e.printStackTrace();
		} // refDb is the controlling class in
			// this relationship

	}

	/**
	 * remove the authorname of the author with the given rank of the
	 * publication with the given ID from the author index of this reference
	 * database
	 * 
	 * (if the given id is not present in the DB or not valid, this method will
	 * do nothing, no errors will be given. Use checkers hasIdinDb() or
	 * isValidId() upfront if necessary)
	 * 
	 * @param authorRank
	 *            the rank of the author for which the author name must be
	 *            removed from the author index
	 * @param id
	 *            the id of the publication associated with the author for which
	 *            the author name must be removed from the author index
	 * 
	 * @throws InputFieldNotValidException
	 * 
	 * @result the authorname of the author with the given rank of the
	 *         publication with the given ID will not be registered in the
	 *         author index
	 */
	
	public static void removeAuthorNameFromIndex(int authorRank, String id) throws InputFieldNotValidException {
		// remark : using rank instead of name(string) ensures getting a valid
		// name
		if (!hasIdinDb(id))
			return;

		String authorName = getPublicationById(id).getAuthorWithInitialAt(authorRank);

		// remove the publication to the set that is linked to that authorname
		Set<String> idSet = getAuthorIndex().get(authorName);
		if (idSet == null)
			assert false; // cannot occur;

		idSet.remove(id);

		// if author has no more publications, then delete the key, otherwise
		// replace the tuple

		if (idSet.size() == 0) {
			getAuthorIndex().remove(authorName);
		} else {
			getAuthorIndex().put(authorName, idSet);
		}

	}
		/**
		 * remove the words of the title of the publication with the given ID from the title word index of this reference database
		 * (if the given id is not present in the DB or not valid, this method will do nothing, no errors will be given.  
		 * Use checkers hasIdinDb() or isValidId() upfront if necessary)
		 * @param 	id
		 * 			the ID of the publication associated with title for which the words must be removed from the title word index
		 * 
		 * @result	the words of the title of the publication with the given ID will not be registered in the title word index
		 */
	public static void removeTitleWordsFromIndex(String id) {
		if (!hasIdinDb(id))
			return;

		for (String word : getPublicationById(id).getTitle().toLowerCase().split(RegexWordSplit)) {

			Set<String> idSet = getTitleWordIndex().get(word);
			// remove the publication to the set that is linked to that word

			if (idSet == null)
				continue;

			idSet.remove(id);

			// if word has no more publications, then delete the key, otherwise
			// replace the tuple
			if (idSet.size() == 0) {
				getTitleWordIndex().remove(word);
			} else {
				getTitleWordIndex().put(word, idSet);
			}
		}

	}
		
	/**
	 * adds a citation.  The ID given first (=citator) cites the ID given second (=citation) .
	 * @param 	idCitator
	 * 			the ID of the publication that cites the citation
	 * @param 	idCitation
	 * 			the ID of the publication that is cited by the citator
	 * 
	 * @throws InputFieldNotValidException
	 * @throws IdNotInReferenceDbException
	 * 
	 * @result	a citation will be added, meaning that 
	 * 				1) the citator will add the citation to it's 'cites' list
	 * 				2) the citation will add the citator to it's 'cited by' list
	 */
	public static void addCitationReference(String idCitator, String idCitation)
			throws IdNotInReferenceDbException, InputFieldNotValidException {
		if (!isValidId(idCitator))
			throw new IdNotValidException(idCitator);
		if (!isValidId(idCitation))
			throw new IdNotValidException(idCitation);
		if (!hasIdinDb(idCitator))
			throw new IdNotInReferenceDbException(idCitator);
		if (!hasIdinDb(idCitation))
			throw new IdNotInReferenceDbException(idCitation);

		getPublicationById(idCitator).addAsCitation(getPublicationById(idCitation));
	}

	/**
	 * removes a citation.  The ID given first (=citator) cites the ID given second (=citation) .
	 * @param 	idCitator
	 * 			the ID of the publication that cites the citation
	 * @param 	idCitation
	 * 			the ID of the publication that is cited by the citator
	 * 
	 * @throws InputFieldNotValidException
	 * @throws IdNotInReferenceDbException
	 * 
	 * @result	a citation is removed, meaning that 
	 * 				1) the citator is removed from the citation to it's 'cites' list
	 * 				2) the citation is removed from the citator to it's 'cited by' list
	 */
	public static void removeCitationReference(String idCitator, String idCitation)
			throws IdNotInReferenceDbException, InputFieldNotValidException {
		if (!isValidId(idCitator))
			throw new IdNotValidException(idCitator);
		if (!isValidId(idCitation))
			throw new IdNotValidException(idCitation);
		if (!hasIdinDb(idCitator))
			throw new IdNotInReferenceDbException(idCitator);
		if (!hasIdinDb(idCitation))
			throw new IdNotInReferenceDbException(idCitation);

		getPublicationById(idCitator).removeAsCitation(getPublicationById(idCitation));
	}

	/**
	 * checks if the given publication is the same as a publication that is already registered on the DB
	 * (equality here means, having identical characteristics, it uses checker Publication.isEqualTo() to check for equality)
	 * @param 	publication
	 * @return	true if the given publication is equal to a publication that is already stored on the DB
	 */
	private static boolean hasSamePublicationInDb(Publication publication) {
		// get a limited set of publications that are possible duplicates using
		// indexes
		Set<String> idSetPossibleDuplicates = new HashSet<String>();
		Set<String> idSetTemp = new HashSet<String>();
		for (String authorname : publication.getAllAuthorsWithInitial()) {
			idSetTemp = getAuthorIndex().get(authorname);
			if (idSetTemp != null)
				idSetPossibleDuplicates.addAll(idSetTemp);
		}
		for (String word : publication.getTitle().toLowerCase().split(RegexWordSplit)) {
			idSetTemp = getTitleWordIndex().get(word);
			if (idSetTemp != null)
				idSetPossibleDuplicates.addAll(idSetTemp);
		}
		// for every possible duplicate do a thorough search
		for (String idPossibleDuplicate : idSetPossibleDuplicates) {
			if (publication.isEqualTo(getPublicationById(idPossibleDuplicate)))
				return true;
		}

		return false;
	}

/**
 * get the citation index for the given author
 * the author name must be given in the default name format (e.g. King, Martin Luther)
 * The citation index is defined as the weighted sum of the citations of all the author’s publications. 
 * The weights depend on the type of publication the author is cited in.
 * 
 * @param 	authorName
 * 			name of the author for which the citation index will be calculated
 * 
 * @return	the citationindex of the given author
 * 
 * @throws AuthorNotInDbException
 * @throws AuthorNameNotValidException 

 */
	public static double getCitationIndex(String authorName)
			throws AuthorNotInDbException, AuthorNameNotValidException {
		double citationIndex = 0;
		if (!Publication.isValidAuthorName(authorName))
			throw new AuthorNameNotValidException();
		// get name in index format of author (King, Martin Luther => M. L.
		// King)

		String[] nameSplit = authorName.trim().split(",");
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(nameSplit[1], " ");
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken().substring(0, 1).toUpperCase());
			sb.append(". ");
		}
		sb.append(nameSplit[0].trim());

		// get publication set of author
		Set<Publication> publicationSet;
		try {
			publicationSet = getPublicationsByAuthorName(sb.toString());
		} catch (AuthorNameIsNullException e) {
			throw new AuthorNameNotValidException();
		}
		if (publicationSet.isEmpty())
			throw new AuthorNotInDbException();
		for (Publication publication : publicationSet) {
			// check full name of author (filter out if needed)
			boolean authorFullNameFound = false;
			for (String authorNameFull : publication.getAllAuthors())
				if (authorNameFull.equalsIgnoreCase(authorName.trim())) {
					authorFullNameFound = true;
					continue;
				}
			if (!authorFullNameFound) {
				continue;
			}
			// call getCitationWeight for all the citators (polymorphism at
			// work)
			for (Publication citator : publication.getAllCitators()) {
				citationIndex += citator.getCitationScore();
			}
		}
		return citationIndex;
	}

/**
 * this method will for a given publication, return all publications that directly or indirectly cite that publication. 
 * A publication is cited indirectly in a publication when that publication cites a publication in which that publication is cited directly, or
 * indirectly. In mathematical terms, compute the transitive closure of the cited relation
 * @param 	publication
 * 			publication for which the transitive closure of the cited relation will be composed
 * @return	a set of publications that directly or indirectly cite the given publication. if no publications are found an empty set will be returned
 */
	public static Set<Publication> getTransitiveClosureCitedBy(Publication publication) {
		Set<Publication> transitiveClosureSet = new HashSet<Publication>();
		getTransitiveClosureCitedBy(publication, transitiveClosureSet);
		return transitiveClosureSet;
	}
/**
 * this method will, for a given publication and a given set of publications, add all publications that directly or indirectly cite that publication
 * to the given publicationset.  This function is used recursively to build up the transitive closure of the cites-relation.   this private function
 *  is used in conjunction with the public method with the same name. 
 * @param 	publication
 * 			publication for which the transitive closure of the cited relation will be composed
 * @param	publicationSet
 * 			the publicationSet to which new citations will be added to
 * @return	a set of publications that directly or indirectly cite the given publication. if no publications are found an empty set will be returned
 */
	private static Set<Publication> getTransitiveClosureCitedBy(Publication publication,
			Set<Publication> publicationSet) {
		for (Publication citator : publication.getAllCitators()) {
			if (!publicationSet.contains(citator)) {
				publicationSet.add(citator);
				if (citator.getNbCitators() > 0)
					publicationSet.addAll(getTransitiveClosureCitedBy(citator, publicationSet));
			}
		}
		return publicationSet;
	}

/**
 * check if this reference database has a proper publications associated with it.
 * So checks for every publication in the database that the class invariant is fulfilled
 * 
 * @return true if every publication in the idTable is a proper publication (has
 */
	public static boolean hasProperPublications() {
		for (Map.Entry<String, Publication> entry : getIdTable().entrySet()) {
			if (!isValidIdTableTuple(entry))
				return false;
		}
		return true;
	}


	/**
	 * print out the entire database
	 * 
	 */
	public static void printRefdb() {
		System.out.println("\nID-table");
		System.out.println("--------");

		for (Map.Entry<String, Publication> entry : getIdTable().entrySet()) {
			System.out.printf("%-10s", "id : ");
			System.out.printf("%-20s", entry.getKey());
			// System.out.printf("%-10s",">>publication : ");
			System.out.println();
			System.out.printf("%-50s", entry.getValue());
			System.out.println();
		}

		System.out.println("\nauthor index-table");
		System.out.println("--------");
		for (Map.Entry<String, Set<String>> entry : getAuthorIndex().entrySet()) {
			System.out.printf("%-10s", "author : ");
			System.out.printf("%-20s", entry.getKey());
			System.out.printf("%-10s", ">>ID set : ");
			System.out.printf("%-50s", entry.getValue());
			System.out.println();
		}

		System.out.println("\nword-table");
		System.out.println("--------");
		for (Map.Entry<String, Set<String>> entry : getTitleWordIndex().entrySet()) {
			System.out.printf("%-10s", "word : ");
			System.out.printf("%-20s", entry.getKey());
			System.out.printf("%-10s", ">>ID set : ");
			System.out.printf("%-50s", entry.getValue());
			System.out.println();

		}
	}
}

/**
 * 
 */
package publicationRefDb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * A class of publications involving a title, year of publication, reference-ID,author(s),
 * citations (=the publications cited by this publication) and citators (the publications
 *  citing this publication) 
 * 
 * @invar	every publication is a proper publication (isProperPublication())

 * @author	 Wim Thiels
 */
public abstract class Publication {
	/**
	 * Initialise this new publication with the given title,given year of publication, 
	 * referenceId set to null, and given author(s). 
	 * Leading and trailing spaces will be removed from title, and authors before initialisation.
	 * 
	 * @param	title
	 *        	the title for this new publication
	 * @param 	yearPub
	 *        	the year of publication for this new publication
	 * @param 	author
	 *        	The name(s) of the author(s) for this new publication
	 *        
	 * @throws InputFieldNotSpecifiedException 
	 * @throws InputFieldNotValidException 
	 *        
	 *    
	 * @post 	a new publication is constructed with the given title, given year of publication and given
	 *       	author(s). 
	 *       	Leading and trailing spaces will be removed from title, and authors before initialisation
	 */
	public Publication(String title, int yearPub, String... author)
			throws InputFieldNotSpecifiedException, InputFieldNotValidException {
		setTitle(title);
		setYearOfPublication(yearPub);
		setReferenceId(null);
		if (author.length == 0)
			throw new ZeroAuthorsException();
		authorList = new ArrayList<String[]>();
		for (String a : author) {
			addAsAuthor(a);
		}
		cites = new HashSet<Publication>();
		citedBy = new HashSet<Publication>();

	}
	/**
	 * Initialize this new publication with the given title. 
	 * All the other parameters are set to a an 'unkown' value: 
	 * 		- year of publication is set to the minimum (getMinYearOfPublication()), 
	 * 		- first author is set to "Unknown, Unknown"
	 * 
	 * @param 	title
	 *          the title for this new publication
	 *          
	 * @throws InputFieldNotValidException 
	 * @throws InputFieldNotSpecifiedException 
	 *          
	 * 
	 * @post 	a new publication is constructed with the given title and all
	 *       	the other parameters set to an 'unknown' value
	 */
	public Publication(String title) throws InputFieldNotSpecifiedException, InputFieldNotValidException {
		this(title, getMinYearOfPublication(), "Unknown, Unknown");
	}

	/**
	 * Return the title of this publication
	 * 
	 */
	public String getTitle() {
		return new String(title);
	}

	/**
	 * Set the title of this publication to the given title (without leading
	 * or trailing spaces)
	 * if the title is already indexed in the reference database, then this index is updated
	 * 
	 * @param 	title
	 *          The new title for this publication
	 *          
	 * @post 	the title of this publication is equal to the given title
	 *      	(without leading or trailing spaces)

	 * @throws InputFieldNotSpecifiedException
	 */
	public void setTitle(String title) throws InputFieldNotSpecifiedException {
		if (title == null)
			throw new TitleIsNullException();
		if ("".equals(title.trim()))
			throw new TitleIsBlankException();

		// first remove the old title index if necessary
		if (hasReferenceId())
			RefDb.removeTitleWordsFromIndex(getReferenceId());

		// set the title
		this.title = new String(title).trim();

		// update the word title index with the new value if necessary
		if (hasReferenceId())
			RefDb.addTitleWordsToIndex(getReferenceId());

	}

	/**
	 * Change the title of this publication to a format where every first
	 * letter of every word is capitalised 
	 * e.g. change “Brownian motion in fluids”, to “Brownian Motion In Fluids”
	 * 
	 * @throws	ErrorInConvertedTitleException
	 * 
	 * @post 	the title of this publication is changed so that every first
	 *       	letter of every word is capitalised
	 */
	public void CapitaliseEveryWordOfTitle() throws ErrorInConvertedTitleException {
		StringBuffer TitleCap = new StringBuffer(title.length());
		StringTokenizer st = new StringTokenizer(title, " ", true);
		while (st.hasMoreTokens()) {
			String word = st.nextToken();
			if (word.equals(" ")) {
				TitleCap.append(" ");
			} else {
				String capWord = word.substring(0, 1).toUpperCase() + word.substring(1);
				TitleCap.append(capWord);
			}
		}

		// group these errors into something that is more meaningful for the
		// caller
		try {
			this.setTitle(TitleCap.toString());
		} catch (InputFieldNotSpecifiedException e) {
			throw new ErrorInConvertedTitleException();
		}
	}

	private String title;

	/**
	 * Return the year of publication of this publication
	 */
	public int getYearOfPublication() {
		return yearPub;
	}

	/**
	 * Set the year of publication of this publication to the given year
	 * 
	 * @param 	yearPub
	 *          the new year of publication for this publication
	 *          
	 * @throws	YearOfPublicationNotValidException	  
	 * 
	 * @post 	the year of publication of this publication is equal to the given year of publication
	 */
	public void setYearOfPublication(int yearPub) throws YearOfPublicationNotValidException {
		if (!isValidYearOfPublication(yearPub))
			throw new YearOfPublicationNotValidException();
		this.yearPub = yearPub;
	}

	/**
	 * Check if this publication was published more than 10 years ago
	 * 
	 * @return true if (currentYear - yearPub) > 10
	 */
	public boolean olderThan10Years() {
		return (Calendar.getInstance().get(Calendar.YEAR) - getYearOfPublication() > 10);
	}

	/**
	 * Check if the given year is a valid year of publication for a publication
	 * 
	 * @param 	year
	 *          the year to check
	 * @return 	true if ((year >= getMinYearOfPublication()) && (year =< getMaxYearOfPublication()))
	 */
	public static boolean isValidYearOfPublication(int year) {
		return ((year >= getMinYearOfPublication()) && (year <= getMaxYearOfPublication()));
	}

	/**
	 * return the earliest possible year of publication for all publications
	 * 
	 * @return 	the earliest possible year of publication is always >= -9999 (=9999 B.C.)
	 * 			and < getMaxYearOfPublication()
	 */
	public static int getMinYearOfPublication() {
		// for now set the value to 1 A.C.
		return 1;
	}

	/**
	 * return the latest possible year of publication for all publications
	 * 
	 * @return 	the latest possible year of publication is always equal or bigger than the current year
	 * 			and > getMinYearOfPublication()
	 */
	public static int getMaxYearOfPublication() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	private int yearPub;
	
	/**
	 * get the referenceID of this publication
	 */
	public String getReferenceId() {
		if (referenceId == null)
			return null;
		return new String(referenceId);
	}
	/**
	 * check whether this publication has a referenceID
	 * @return 	true if the referenceID of this publication is effective
	 * 			otherwise false
	 */
	public boolean hasReferenceId() {
		return getReferenceId() != null;
	}

	/**
	 * check whether this publication has a proper referenceID
	 * @return true if the referenceID of this publication does not reference an id, or
	 * 			if the id referenced by this publication in turn references this publication
	 * 			(RefDB.getPublicationById(id)==this)
	 * 			otherwise false
	 */
	public boolean hasProperReferenceId() {

		if (getReferenceId() == null)
			return true;

		// strictly speaking, we only need to check if the id is in the RefDb,
		// the mirrorcheck is not
		// necessary (because this is already covered by class invariant of
		// RefDb)
		if (RefDb.getPublicationById(getReferenceId()) == this)
			return true;

		return false;
	}
	
	/**
	 * set the referenceId of this publication to the given ID
	 * @param	id
	 * 			the ID to be set
	 * @throws InputFieldNotValidException 
	 * @pre		if the given Id is effective then it must already reference this publication
	 * 			RefDb.getPublicationById(Id)==this
	 * @pre		if the given Id not effective and this and this publication already has a reference Id
	 * 			then that reference id may not reference this publication
	 * @post	this publication has the given id as its referenceId
	 */
	public void setReferenceId(String id) throws InputFieldNotValidException {
		if (id != null) {
			if (RefDb.getPublicationById(id) != this)
				throw new IdNotValidException(id);
			this.referenceId = new String(id).trim();
		}
		if (id == null) {
			if (hasReferenceId())
				if (RefDb.getPublicationById(getReferenceId()) != null)
					throw new IdNotValidException(id);
			referenceId = null;
		}
	}

	private String referenceId;

	/**
	 * Check if the name given is a valid author name. A valid name complies with
	 * the syntax-rules given below. It is the default name format that is used
	 * throughout the methods of this class
	 * 
	 * @param 	name
	 *          the name to be checked
	 * 
	 * 
	 * @return 	true if the name is not null and complies with all of the syntax rules given below :
	 * 
	 *         	A name is represented as 1 string that is composed of these parts 
	 *         		1) Last name 
	 *         		2) First name 
	 *         		3) (Middle names) 
	 *         
	 *         	rule 1 : The last name must be followed by a comma, then followed by the first name 
	 *              	 (blanks before and after the comma are allowed, also multiple commas). 
	 *         	rule 2 : Both last name and first name are mandatory. (middle names are optional.
	 *         			 they can be added after the first name (separated by blanks)) 
	 *         	rule 3 : -the last name can only contain alphabetical characters
	 *          		 -the first name can contain alphabetical characters, '.' (not repeating),
	 *          		  or blanks (to separate middle names) 
	 *          		  e.g. : "Einstein, Albert", "Roosevelt, Franklin Delano" "Downey, Robert Jr."
	 * 
	 * @note	due to non-generality, no restrictions are made concerning 
	 * 				- uppercase/lowercase 
	 * 				- the number of letters in a name part (e.g. a name part with 1 letter is allowed) 
	 *      	It is also not possible to check the correct order of the name parts 
	 *      	(e.g. first name is passed as the first name part instead of last name)
	 */
	public static boolean isValidAuthorName(String name) {
		// this method checks the classinvariant for the name, the
		// representation invariant differs however
		if (name == null)
			return false;
		boolean nameOK = true;

		int namePartCounter = 0;
		StringTokenizer st = new StringTokenizer(name, ",");

		while (st.hasMoreTokens() && nameOK) {
			String namePart = st.nextToken().trim();
			if (namePart.length() == 0)
				;
			else {
				namePartCounter++;
				if (namePartCounter == 1)
					nameOK = namePart.matches("[a-zA-Z]+");
				else {
					nameOK = namePart.matches("[a-zA-Z .]+");
					for (int i = 1; i < namePart.length(); i++) {
						if ((namePart.charAt(i) == '.') && (namePart.charAt(i - 1)) == '.') {
							nameOK = false;
						}
					}
				}
			}
		}

		if (namePartCounter != 2)
			nameOK = false;

		return nameOK;
	}

	/**
	 * Check whether this publication can have the given author as one of
	 * its authors.
	 * 
	 * @param 	name 
	 * 			the authorname to check
	 * 
	 * @throws 	AuthorIsNullException 
	 * 
	 * @post 	False if the name is not a valid authorname (isValidAuthorName()) 
	 *       	otherwise true
	 * 
	 * @note 	Different authors of a publication can have the same name
	 */
	public boolean canHaveAsAuthor(String name) throws AuthorIsNullException {
		// for now, this method only checks the name, but this can expand later
		// (e.g. not allow duplicate authors)
		return (isValidAuthorName(name));
	}

	/**
	 * Check whether this publication has proper authors associated with it
	 * 
	 * @return 	True if this publication can have each of its authors as an
	 *        	author (canHaveAsAuthor(name))
	 */
	public boolean hasProperAuthors() {

		for (int i = 1; i <= getAllAuthors().size(); i++)
			try {
				if (!canHaveAsAuthor(getAuthorAt(i)))
					return false;
			} catch (AuthorIsNullException | InputFieldNotValidException e) {
				// the nullexception is hidden from the caller, the caller just
				// wants to know true or false
				// the other 2 exceptions can never occur. they are also hidden
				// from the caller
				return false;
			}
		return true;
	}

	/**
	 * Return the number of authors of this publication
	 * 
	 */
	public int getNbAuthors() {
		return getAuthorList().size();
	}

	/**
	 * Return the author associated with this publication at the given rank
	 * (in default nameformat : eg. King, Martin Luther)
	 * 
	 * @param	rank 
	 * 			rank of the author to be returned
	 * 
	 * @throws 	InputFieldNotValidException
	 * 
	 * @post 	The name will be returned as a valid authorname (isValidAuthorname())
	 */
	
	
	/**
	 * get the authorlist 
	 * (in internal format : a list of stringarrays, where String[0] = last name, String[1] = first (and middle) names
	 * @return the authorList
	 */
	private List<String[]> getAuthorList() {
		return authorList;
	}
	
	
	/**
	 * get the authorname of the author with the given rank.  
	 * nameformat = default.  e.g. "Einstein, Albert"
	 * @param rank
	 * @throws InputFieldNotValidException
	 * @return the authorname of the author with the given rank in default nameformat

	 */
	public String getAuthorAt(int rank) throws InputFieldNotValidException {
		if (rank <= 0)
			throw new RankNotPositiveException();
		if (rank > (getNbAuthors()))
			throw new RankTooBigException();

		String[] author = getAuthorList().get(rank - 1);
		return (author[0] + ", " + author[1]);
	}

	/**
	 * Return the author associated with this publication at the given rank
	 * where the name is composed of the author's initial(s) followed by the last
	 * name, e.g., “A. Einstein” , "M. L. King"
	 * 
	 * @param 	rank 
	 * 			rank of the author to be returned
	 * 
	 * @throws InputFieldNotValidException 
	 * 
	 * @post 	The name will be returned composed of the author's initial(s) (first and middle names) followed
	 *       	by the last name 
	 */
	public String getAuthorWithInitialAt(int rank) throws InputFieldNotValidException {
		if (rank <= 0)
			throw new RankNotPositiveException();
		if (rank > (getNbAuthors()))
			throw new RankTooBigException();

		String[] author = getAuthorList().get(rank - 1);

		StringTokenizer st = new StringTokenizer(author[1], " ");
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken().substring(0, 1).toUpperCase());
			sb.append(". ");
		}

		sb.append(author[0]);

		return sb.toString();
	}

	/**
	 * Return a list of all authors of this publication 
	 * (in default nameformat : "King, Martin Luther")
	 * 
	 * @return The number of elements in the resulting list is equal to the
	 *         number of authors associated with this publication
	 * @return Each element at a given rank in the resulting list is the same
	 *         as the author associated with this publication at the
	 *         corresponding rank
	 */
	public ArrayList<String> getAllAuthors() {
		ArrayList<String> allAuthors = new ArrayList<String>();
		for (int i = 1; i <= getAuthorList().size(); i++)
			try {
				allAuthors.add(getAuthorAt(i));
			} catch (InputFieldNotValidException e) {
				// these errors can not occur. they are shielded from the caller
			}
		return allAuthors;
	}

	/**
	 * Return a list of all authors of this publication, where every author
	 * is composed of the author's initial followed by the last name, e.g., “A.
	 * Einstein”
	 * 
	 * @return The number of elements in the resulting list is equal to the
	 *         number of authors associated with this publication
	 * @return Each element at a given rank  in the resulting list corresponds
	 *         to the same author associated with this publication at the
	 *         corresponding rank, but in a different format, namely, the
	 *         author's initial followed by the last name
	 */
	public ArrayList<String> getAllAuthorsWithInitial() {
		ArrayList<String> allAuthors = new ArrayList<String>();
		for (int i = 1; i <= getAuthorList().size(); i++)
			try {
				allAuthors.add(getAuthorWithInitialAt(i));
			} catch (InputFieldNotValidException e) {
				// these exceptions can not occur and are shielded from caller
			}
		return allAuthors;
	}

	/**
	 * Converts a valid authorname to the internal representation of that name
	 * e.g.  Einstein, Albert converts to [Einstein, Albert]
	 * 
	 * @param 	name
	 *        	the authorname to be converted
	 *        
	 * @throws	AuthorNameNotValidException
	 * 
	 * @return 	the internal representation of the authorname given
	 */
	private static String[] convertNameToIntRepr(String name) throws AuthorNameNotValidException {
		if (!isValidAuthorName(name))
			throw new AuthorNameNotValidException();

		// Internal representation as an array to ease later manipulation and
		// retrieval
		String[] nameArray = new String[2];
		StringTokenizer st = new StringTokenizer(name, ",");
		nameArray[0] = st.nextToken().trim();
		nameArray[1] = st.nextToken().trim();
		return nameArray;
	}

	/**
	 * Add the given name as an author for this publication at the given rank
	 * 
	 * @param 	name
	 *          the name of the author to be added
	 * @param 	rank
	 *         	the rank of the author to be added
	 *            
	 * @throws 	InputFieldNotValidException 
	 * @throws 	AuthorIsNullException 
	 * 
	 * @post 	This publication has the given author as one of its authors at the given rank
	 *       	The number of authors for this publication is incremented by 1 (getNbAuthors()) 
	 *       	All authors for this publication at an rank exceeding the given rank, 
	 *       	are registered as author at one rank higher
	 *          if the publication is registered in the reference DB, then the given author
	 *          is added to the author index of the reference DB
	 * 
	 */
	public void addAuthorAt(String name, int rank) throws InputFieldNotValidException, AuthorIsNullException {
		if (rank <= 0)
			throw new RankNotPositiveException();
		if (rank > (getNbAuthors() + 1))
			throw new RankTooBigException();

		getAuthorList().add(rank - 1, convertNameToIntRepr(name));

		// add to authorindex
		if (hasReferenceId())
			RefDb.addAuthorNameToIndex(rank, getReferenceId());
	}

	/**
	 * Remove the author for this publication at the given rank
	 * 
	 * @param 	rank
	 *          the rank of the author to be removed
	 * @throws 	InputFieldNotValidException 
	 * @post 	This publication no longer has the given author at the given rank 
	 * 			The number of authors for this publication is decreased by 1 
	 * 			All authors for this publication at a rank exceeding the
	 *       	given rank, are registered as author at one rank lower
	 *       	if the publication is registered in the reference database then the given 
	 *       	author is removed from the author index of the reference database
	 */
	public void removeAuthorAt(int rank) throws InputFieldNotValidException {
		if (rank <= 0)
			throw new RankNotPositiveException();
		if (rank > (getNbAuthors()))
			throw new RankTooBigException();

		// remove entry in author index (must be done before deleting from
		// authorList)
		if (hasReferenceId())
			RefDb.removeAuthorNameFromIndex(rank, getReferenceId());

		// remove from authorlist
		getAuthorList().remove(rank - 1);
	}

	/**
	 * Add the given author as an author for this publication
	 * 
	 * @param 	name
	 *          the name of the author to become an author for this publication
	 *          
	 * @throws	AuthorIsNullException       
	 * @throws 	AuthorNameNotValidException 
	 *   
	 * @post 	This publication has the given author as one of its authors at the end of the list 
	 * 			The number of authors for this publication is incremented by 1
	 */
	public void addAsAuthor(String name) throws AuthorIsNullException, AuthorNameNotValidException {
		getAuthorList().add(convertNameToIntRepr(name));

		// add to authorindex
		if (hasReferenceId())
			try {
				RefDb.addAuthorNameToIndex(getNbAuthors(), getReferenceId());
			} catch (InputFieldNotValidException e) {
				assert false; // cannot occur
				e.printStackTrace();
			}

	}

	/**
	 * Remove the given author as an author for this publication
	 * the name must be in the default name format e.g. "Einstein, Albert"
	 * 
	 * @param 	name
	 *          the name of the author to be removed for this publication
	 *          
	 * @throws 	AuthorNameNotValidException 
	 * 
	 * @post 	This publication no longer has the given author as one of its authors 
	 * 			The number of authors for this publication is decreased by the number of 
	 * 			times the given author appeared as an author for this publication 
	 * 			All authors for this publication at a rank exceeding the rank of a removed author, 
	 * 			are registered as author at one rank lower
	 * 	        if the publication is registered in the reference database then the given 
	 *       	author is removed from the author index of the reference database
	 */
	public void removeAsAuthor(String name) throws AuthorNameNotValidException {
		for (int rank = 1; rank <= getAuthorList().size(); rank++) {
			if (Arrays.equals(convertNameToIntRepr(name), getAuthorList().get(rank - 1))) {
				try {
					removeAuthorAt(rank);
				} catch (InputFieldNotValidException e) {
					// these exceptions cannot occur and are therefore shielded
					// from caller
					e.printStackTrace();
				}
				// resetting the loop to start all over in case of duplicate
				// authornames
				rank = 0;
			}
		}
	}

	protected List<String[]> authorList;


	/**
	 * Check whether this publication cites the given publication
	 * 
	 * @param publication
	 *            the publication to check
	 */
	public boolean hasAsCitation(Publication publication) {
		return this.getAllCitations().contains(publication);
	}
	/**
	 * Check whether this publication can cite the given publication
	 * 
	 * @param	publication
	 *         	the publication to be checked
	 * @return 	True if 
	 * 			1)the given publication is not null and 
	 * 			2)the given publication is not the same this publication
	 */
	public boolean canHaveAsCitation(Publication publication) {
		// doubles are not checked, because a set is used
		return (publication != null && publication != this);
	}
	/**
	 * Check whether this publication has proper citations (=references of
	 * this publication to other publications)
	 * 
	 * @return True if all the citations of this publication are valid
	 *         (canHaveAsCitation()) , and each of the citations has this
	 *         publication as a citator (=cited-by relation)
	 *         (publication.hasAscitator(this)==TRUE) otherwise false
	 */
	public boolean hasProperCitations() {
		for (Publication citation : this.getAllCitations()) {
			if (!canHaveAsCitation(citation))
				return false;
			if (citation.hasAsCitator(this) != true)
				return false;
		}
		return true;
	}
	/**
	 * get the number of citations in this publication
	 * 
	 * @return the number of citations in this publication ('cites'relation)
	 */
	public int getNbCitations() {
		return this.getAllCitations().size();
	}
	/**
	 * return a set of publications that are cited in this publication
	 * @return	an effective set containing all the publications cited in this publication 
	 */
	public Set<Publication> getAllCitations() {
		Set<Publication> newSet = new HashSet<Publication>();
		newSet.addAll(this.cites);
		return newSet;
	}
	/**
	 * add the given publication as a citation to this publication
	 * 
	 * @param 	publication
	 *          the publication to add
	 * @throws 	PublicationIsNotValidException
	 * @post 	this publication has the given publication as a citation
	 * @post 	the given publication has this publication as a citator
	 *       	('cited by' relation)
	 */
	public void addAsCitation(Publication publication) throws PublicationIsNotValidException {
		if (!canHaveAsCitation(publication))
			throw new PublicationIsNotValidException();
		this.cites.add(publication);
		publication.addAsCitator(this);
	}
	/**
	 * Remove the given publication from the set of citations of this
	 * publication
	 * 
	 * @param 	publication
	 * @post 	this publication does not have the given publication as one
	 *       	of its citations
	 * @post 	the given publication does not have this publication as one
	 *       	of its citators (cited by relation)
	 */
	public void removeAsCitation(Publication publication) {
		if (hasAsCitation(publication)) {
			this.cites.remove(publication);
			publication.removeAsCitator(this);
		}
	
	}


	protected final Set<Publication> cites;

	
	
	/**
	 * Check whether this publication is cited by the given publication
	 * 
	 * @param publication
	 *            the publication to check
	 */
	public boolean hasAsCitator(Publication publication) {
		return this.getAllCitators().contains(publication);
	}
	
	
	/**
	 * Check whether this publication can be cited by the given publication
	 * 
	 * @param  publication
	 *         the publication to be checked
	 * @return True if 1)the given publication is not null and 2)the given
	 *         publication is not the same this publication
	 */
	public boolean canHaveAsCitator(Publication publication) {
		return (publication != null && publication != this);
	}
	/**
	 * Check whether this publication has proper citators (=publications
	 * citing this publication)
	 * 
	 * @return True if all the citators of this publication are valid
	 *         (canHaveAsCitator()) , and each of the citators has this journal
	 *         article as a citation (publication.hasAscitation(this)==TRUE)
	 *         otherwise false
	 */
	public boolean hasProperCitators() {
		for (Publication citator : this.getAllCitators()) {
			if (!canHaveAsCitator(citator))
				return false;
			if (citator.hasAsCitation(this) != true)
				return false;
		}
		return true;
	}
	/**
	 * return the number of citators of this publication
	 * 
	 * @return the number of citators of this publication 
	 * 			('cited by' relation)
	 */
	public int getNbCitators() {
		return this.getAllCitators().size();
	}
	/**
	 * return a set of publications that cite this publication 
	 * @return	an effective set containing all the publications that cite this publication 
	 */
	public Set<Publication> getAllCitators() {
		Set<Publication> newSet = new HashSet<Publication>();
		newSet.addAll(citedBy);
		return newSet;
	}
	/**
	 * add the given publication as a citator to this publication
	 * 
	 * @param 	publication
	 *          the publication to add
	 * @throws 	PublicationIsNotValidException
	 * @post 	this publication has the given publication as a citator
	 *       	(cited by relation)
	 * @post 	the given publication has this publication as a citation
	 *      	(cites relation)
	 */
	private void addAsCitator(Publication publication) {
		// set to private, completely controlled by addCitation()

		assert (publication.hasAsCitation(this)); // assert as a precautionary
													// integrity check
		this.citedBy.add(publication);
	}
	/**
	 * Remove the given publication from the set of citators of this publication
	 * 
	 * @param publication
	 * @post this publication does not have the given publication as one
	 *       of its citators (cited by relation)
	 * @post the given publication does not have this publication as one
	 *       of its citations(cites relation)
	 */
	private void removeAsCitator(Publication publication) {
		// set to private, completely controlled by removeAsCitation()
		if (hasAsCitator(publication)) {
			this.citedBy.remove(publication);
		}

	}
	/**
	 * terminate this publication
	 * All links to other objects will be removed
	 * @effect	each citation is removed from this publication, and for every citation this publication is removed as citator
	 * @effect	each citator is removed from this publication, and for every citator this publication is removed as citation
	 * @effect	this publication will not be registered in the reference database (hasReferenceId()==false)
	 */
	public void terminate() {
		for (Publication citation : this.getAllCitations()) {
			this.removeAsCitation(citation);
		}
		for (Publication citator : this.getAllCitators()) {
			citator.removeAsCitation(this);
		}
		// this will clean up the refDb(indexes and so on), cites and cited-by
		// clean-up will be already done
		if (hasReferenceId()) {
			RefDb.removePublicationFromDb(getReferenceId());
		}
	}

	protected final Set<Publication> citedBy;

	/**
	 * checks if this publication is equal to the given publication.  Being equal means
	 * 		- have the same title (case insensitive)
	 * 	 	- have the same year of publication
	 * 		- are of the same class
	 *  	- have the same authors. This means every authorname in this publication must match 
	 *    	an authorname of the given publication and vice versa.  authors with identical names are considered
	 *    	separate (so if two identically named authors "Albert, Einstein" are present in this publication
	 *    	then also exactly two "Albert, Einstein" authors must be present in the given publication )
	 * @param 	publication
	 * 			the publication to be compared with this publication
	 * 		
	 * @return : true if this publication is equal to the given publication (see description for conditions)
	 * 			 otherwise false
	 */
	public boolean isEqualTo(Publication publication) {
		
		if (!(this.getTitle().toLowerCase().equals(publication.getTitle().toLowerCase())))
			return false;
		
		if (!(this.getYearOfPublication() == publication.getYearOfPublication()))
			return false;

		if (!this.getClass().equals(publication.getClass())) {
			return false;
		}

		ArrayList<String> allAuthors = this.getAllAuthors();
		ArrayList<String> allAuthorsCompare = publication.getAllAuthors();

		boolean authorFound;
		for (int i = 0; i < allAuthors.size(); i++) {
			authorFound = false;

			innerLoop: for (int j = 0; j < allAuthorsCompare.size(); j++) {
				if (allAuthors.get(i).equals(allAuthorsCompare.get(j))) {
					authorFound = true;
					allAuthors.remove(i);
					allAuthorsCompare.remove(j);
					break innerLoop;
				}
			}

			if (!authorFound)
				return false;
			i = -1; // start all over again with reduced lists
		}

		// there may still be extra authors in the second lists
		// authors with identical names are also detected as separate authors
		if (allAuthorsCompare.size() > 0)
			return false;

		return true;
	}
	


	/**
	 * get the citationscore of this publication
	 */
	public abstract double getCitationScore();

	
	/**
	 * checks if this publication is a proper publication, meaning it respects all the classinvariants
	 * 
	 *@return	true if this publication respects all the classinvariants listed below :
	 * 
	 * @invar 	the title of a publication is never blank and never null

	 * @invar 	the year of publication of each publication must be valid :
	 *        	isValidYearOfPublication()
	 *         
	 * @invar 	the names of all the authors of this publication must be valid
	 *        	for this publication : hasProperAuthors()
	 *        
	 * @invar 	the citations (=the publications cited by this publication) are valid (hasProperCitations())
	 * @invar 	the citators (=the publications citing this publication)  are valid (hasProperCitators())
	 * 
	 * @invar 	the referenceId is valid (hasProperReferenceId())
	 * @invar 	if the referenceId is not null, then every word in the title of this publication is
	 * 			registered in the titleword-index of the reference database 
	 * 			(ReferenceDB.getPublicationByTitleWord(word in title).contains(this)==TRUE)
	 * @invar 	if the referenceId is not null, then the names of all the authors of this publication are 
	 * 			taken up in the authorindex of the reference database 
	 * 			(ReferenceDB.getPublicationByAuthor(authorList[x]).contains(this)==TRUE)
	 */
	public boolean isProperPublication(){
			if (getTitle() == null)
				return false;
			if (getTitle() == " ")
				return false;
			if (!isValidYearOfPublication(getYearOfPublication()))
				return false;
			if (!hasProperAuthors())
				return false;
			if (!hasProperCitations())
				return false;
			if (!hasProperCitators())
				return false;
			if (!hasProperReferenceId())
				return false;
			if (getReferenceId() != null) {
				for (String word : getTitle().toLowerCase().split(RefDb.RegexWordSplit)) {
					try {
						if (!RefDb.getPublicationsByTitleWord(word).contains(this))
							return false;
					} catch (WordIsNullException e) {
						return false;
					}

				}
				for (String author : getAllAuthorsWithInitial()) {
					try {
						if (!RefDb.getPublicationsByAuthorName(author).contains(this))
							return false;
					} catch (AuthorNameIsNullException e) {
						return false;
					}
				}
			}		
				
		return true;		
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String s1 = String.format("%1$-20s", "   >>>>>title  ");
		String s2 = String.format("%1$-50s", getTitle());
		String s3 = String.format("%1$-20s", "      >>authors  ");
		String s4 = String.format("%1$-10s", getAllAuthors());
		String s5 = String.format("%1$-20s", "      >>ref id  ");
		String s6 = String.format("%1$-10s", getReferenceId());
		String s7 = String.format("%1$-20s", "      >>type  ");
		String s8 = " ";

		if (this instanceof JournalArticle) {
			s8 = String.format("%1$-20s", "Journal article");
		}
		if (this instanceof Book) {
			s8 = String.format("%1$-20s", "Book");
		}
		if (this instanceof ConferencePaper) {
			s8 = String.format("%1$-20s", "Conference paper");
		}

		sb.append(s1 + s2 + "\n" + s3 + s4 + "\n" + s5 + s6 + "\n" + s7 + s8);

		if (getNbCitations() > 0) {
			sb.append("\n      >>cites : ");
			for (Publication citation : getAllCitations()) {
				sb.append("\n           " + citation.getTitle() + "  refID : " + citation.getReferenceId());
			}

		}
		if (getNbCitators() > 0) {
			sb.append("\n      >>is cited by : ");
			for (Publication citator : getAllCitators()) {
				sb.append("\n           " + citator.getTitle() + "  refID : " + citator.getReferenceId());
			}

		}
		return sb.toString();
	}
}

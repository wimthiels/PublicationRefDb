package publicationRefDb;


/**
 * A class of journal articles involving a title, reference-ID, name of the
 * journal it was published in, issue number of the journal, year of
 * publication, citations , citators and author(s)
 * 
 * @invar the name of the journal in which a journal article appeared is never
 *        null
 * @invar the issue number of the journal in which the journal article appeared
 *        must be strictly positive for all journal articles

 * 
 * @author Wim Thiels
 */
public class JournalArticle extends Publication {

	/**
	 * Initialise this new journal article with the given title, given name of
	 * journal, given issue number, given year of publication, given author(s)
	 * and with no citations to or from other articles Leading and trailing
	 * spaces will be removed from title, name of journal and authors before
	 * initialisation.
	 * 
	 * @param title
	 *            the title for this new journal article
	 * @param journalName
	 *            the name of the journal for this new journal article
	 * @param issueNumber
	 *            the issue number for this new journal article
	 * @param yearPub
	 *            the year of publication for this new journal article
	 * @param author
	 *            The name(s) of the author(s) for this new journal article
	 *            
	 * @throws InputFieldNotValidException 
	 * @throws InputFieldNotSpecifiedException 

	 * 
	 * @post a new journal article is constructed with the given title, given
	 *       name of journal, given issue number, given year of publication and
	 *       given author(s). Leading and trailing spaces will be removed from
	 *       title, name of journal and authors before initialisation
	 */
	public JournalArticle(String title, String journalName, int issueNumber, int yearPub, String... author) throws InputFieldNotSpecifiedException, InputFieldNotValidException
		{
		super(title, yearPub, author);
		setJournalName(journalName);
		setIssueNumber(issueNumber);

	}
	
	/**
	 * Initialize this new journal with the given title. 
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
	 * @post 	a new publication is constructed with the given title and all
	 *       	the other parameters set to an 'unknown' value
	 */
	public JournalArticle(String title) throws InputFieldNotSpecifiedException, InputFieldNotValidException
		 {
		super(title, getMinYearOfPublication(), "Unknown, Unknown");
		setJournalName("Unknown");
		setIssueNumber(0);
	}

	/**
	 * Return the name of the journal of this journal article
	 */
	public String getJournalName() {
		return new String(journalName);
	}

	/**
	 * Set the name of the journal in which this journal article appeared to the
	 * given name of journal (without leading or trailing spaces)
	 * 
	 * @param journalName
	 *            the new name of the journal for this journal article
	 * 
	 * @throws JournalNameIsNullException
	 * 
	 * @post the journal name of this journal article is equal to the given
	 *       journal name (without leading or trailing spaces)
	 */
	public void setJournalName(String journalName) throws JournalNameIsNullException {
		if (journalName == null)
			throw new JournalNameIsNullException();
		this.journalName = new String(journalName).trim();
	}

	private String journalName;

	/**
	 * Return the issue number of the journal in which this journal article
	 * appeared
	 */
	public int getIssueNumber() {
		return issueNumber;
	}

	/**
	 * Set the issue number of the journal in which this journal article
	 * appeared to the given issue number
	 * 
	 * @param issueNumber
	 *            the new issue number of this journal article
	 * 
	 * @throws IssueNumberIsNegativeException
	 * 
	 * @post the issue number of this journal article is equal to the given
	 *       issue number
	 */
	public void setIssueNumber(int issueNumber) throws IssueNumberIsNegativeException {
		if (issueNumber < 0)
			throw new IssueNumberIsNegativeException();
		this.issueNumber = issueNumber;
	}

	private int issueNumber;
	
	@Override
	public double getCitationScore() {
		return PublicationType.JOURNALARTICLE.getCitationWeight();
	}
}
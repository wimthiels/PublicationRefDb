/**
 * 
 */
package publicationRefDb;

/**
 * A class of conference papers involving a title, year of publication,
 * reference-ID, conference, citations , citators  and author(s)
 * 
 * @invar the conference of a conference paper is never blank and never null
 * 
 * @author Wim Thiels
 */
public class ConferencePaper extends Publication {

	/**
	 * Initialise this new conference paper with the given title, given year of publication,
	 * given conference and given author(s). 
	 * Leading and trailing spaces will be removed from title, name of journal 
	 * and authors before initialisation.
	 * 
	 * @param	title
	 *        	the title for this new book
	 * @param 	yearPub
	 *        	the year of publication for this new book
	 * @param 	publisher
	 *        	the name of the publisher for this new book
	 * @param 	author
	 *        	The name(s) of the author(s) for this new book
	 *        
	 * @throws InputFieldNotValidException 
	 * @throws InputFieldNotSpecifiedException 
	 * 
	 *    
	 * @post 	a new conference paper is constructed with the given title, given year of publication,
	 * 			given conference and given author(s). 
	 *       	Leading and trailing spaces will be removed from title, name of journal, conference 
	 *       	and authors before initialisation
	 */
	public ConferencePaper(String title, int yearPub, String conference, String... author) throws InputFieldNotSpecifiedException, InputFieldNotValidException {
		super(title, yearPub, author);
		setConference(conference);

	}

	/**
	 * Initialize this new conference paper with the given title and given conference. 
	 * All the other parameters are set to a an 'unkown' value: 
	 * 		- year of publication is set to the minimum (getMinYearOfPublication()), 
	 * 		- first author is set to "Unknown, Unknown"
	 * 
	 * @param 	title
	 *          the title for this new conference paper
	 * @throws InputFieldNotValidException 
	 * @throws InputFieldNotSpecifiedException 
	
	 * 
	 * @post 	a new conference paper is constructed with the given title, given conference and all
	 *       	the other parameters set to an 'unknown' value
	 */
	public ConferencePaper(String title, String conference) throws InputFieldNotSpecifiedException, InputFieldNotValidException  {
		super(title);
		setConference(conference);
	}

	/**
	 * Return the conference of this conference paper
	 * 
	 */
	public String getConference() {
		return new String(conference);
	}

	/**
	 * Set the conference of this conference paper to the given conference (without
	 * leading or trailing spaces)
	 * 
	 * @param conference
	 *        The new conference for this conference paper
	 *        
	 * @throws InputFieldNotSpecifiedException 
	 * 
	 * @post the conference of this conference paper is equal to the given
	 *       conference (without leading or trailing spaces)
	 */
	public void setConference(String conference) throws InputFieldNotSpecifiedException  {
		if (conference == null)
			throw new ConferenceIsNullException();
		if ("".equals(conference.trim()))
			throw new ConferenceIsBlankException();

		this.conference = new String(conference).trim();
	}

	private String conference;

	@Override
	public double getCitationScore() {
		return PublicationType.CONFERENCEPAPER.getCitationWeight();
	}
}

/**
 * 
 */
package publicationRefDb;

/**
 * A class of books involving a title, year of publication,
 * reference-ID, publisher citations , citators and author(s)
 * 
 * @invar the publisher of a book is never blank and never null
 * 
 * @author Wim Thiels
 */
public class Book extends Publication {

	/**
	 * Initialise this new book with the given title, given year of publication,
	 * given publisher and given author(s). 
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
	 * @post 	a new journal article is constructed with the given title, given year of publication,
	 * 			given publisher and given author(s). 
	 *       	Leading and trailing spaces will be removed from title, name of journal, publisher 
	 *       	and authors before initialisation
	 */
	public Book(String title, int yearPub, String publisher, String... author) throws InputFieldNotSpecifiedException, InputFieldNotValidException {
		super(title, yearPub, author);
		setPublisher(publisher);

	}

	/**
	 * Initialize this new book with the given title and given publisher. 
	 * All the other parameters are set to a an 'unkown' value: 
	 * 		- year of publication is set to the minimum (getMinYearOfPublication()), 
	 * 		- first author is set to "Unknown, Unknown"
	 * 
	 * @param 	title
	 *          the title for this new book
	 *          
	 * @throws InputFieldNotValidException 
	 * @throws InputFieldNotSpecifiedException 
	 *
	 * 
	 * @post 	a new book is constructed with the given title, given publisher and all
	 *       	the other parameters set to an 'unknown' value
	 */
	public Book(String title, String publisher) throws InputFieldNotSpecifiedException, InputFieldNotValidException  {
		super(title);
		setPublisher(publisher);
	}

	/**
	 * Return the publisher of this book
	 * 
	 */
	public String getPublisher() {
		return new String(publisher);
	}

	/**
	 * Set the publisher of this book to the given publisher (without
	 * leading or trailing spaces)
	 * 
	 * @param publisher
	 *        The new publisher for this book
	 *        
	 * @throws InputFieldNotSpecifiedException 
	 * 
	 * 
	 * @post the publisher of this book is equal to the given
	 *       publisher (without leading or trailing spaces)
	 */
	public void setPublisher(String publisher) throws InputFieldNotSpecifiedException   {
		if (publisher == null)
			throw new PublisherIsNullException();
		if ("".equals(publisher.trim()))
			throw new PublisherIsBlankException();

		this.publisher = new String(publisher).trim();
	}

	private String publisher;

	@Override
	public double getCitationScore() {
		return PublicationType.BOOK.getCitationWeight();
	}

}

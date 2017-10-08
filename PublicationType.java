package publicationRefDb;
/**
 * An Enum class of all the different publication types with their
 * corresponding citation weight 
 * the citation weight can be changed through the setter
 * 
 * @author Wim Thiels
 */
public enum PublicationType {
	
	JOURNALARTICLE(1), CONFERENCEPAPER(0.7), BOOK(1.2);
	private double citationWeight;

	private PublicationType(double citationWeight) {
		this.citationWeight = citationWeight;
	}

	/**
	 * get citation weight for the publication type
	 */
	public double getCitationWeight() {
		return citationWeight;
	}

	/**
	 * set citation weight for the publication type to the given value
	 * 
	 * @param 	citationWeight
	 * 			the value to be given
	 * @return	the citation weight for the publication type will be set to the given
	 * 			citation weight
	 */
	public void setCitationWeight(double citationWeight) {
		this.citationWeight = citationWeight;
	}
 
}

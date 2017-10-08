package publicationRefDb;

public class IdNotInReferenceDbException extends NotFoundOnRefDbException
 {
	String errMsg ;

	IdNotInReferenceDbException(String id){
		this.errMsg= "this ID is not in the reference database : " + id;
	}
	/**
	 * @return the errMsg
	 */
	public String getErrMsg() {
		return errMsg;
	}
}

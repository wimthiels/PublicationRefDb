package publicationRefDb;

public class IdNotValidException extends InputFieldNotValidException
 {
	String errMsg;

IdNotValidException(String id){
	this.errMsg= "not a valid id : " + id;
}
/**
 * @return the errMsg
 */
public String getErrMsg() {
	return errMsg;
}
}

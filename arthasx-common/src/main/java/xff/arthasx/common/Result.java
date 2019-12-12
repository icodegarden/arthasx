package xff.arthasx.common;
/**
 * 
 * @author Fangfang.Xu
 *
 * @param <T>
 */
public class Result<T> {

	private boolean success;

	private String code;

	private String message;

	private T body;

	public boolean isSuccess() {
		return success;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public T getBody() {
		return body;
	}
	
	public String toSimpleJson() {
		return "{\"success\":"+success+",\"code\":\""+code+"\",\"message\":\""+message+"\",\"body\":\""+body+"\"}";
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		public <T> Result<T> buildSuccess(T body) {
			Result<T> result = new Result<T>();
			result.success = true;
			result.body = body;
			return result;
		}
		
		public <T> Result<T> buildFailed(String message) {
			return buildFailed(null, message, null);
		}
		
		public <T> Result<T> buildFailed(String code, String message) {
			return buildFailed(code, message, null);
		}

		public <T> Result<T> buildFailed(String code, String message, T body) {
			Result<T> result = new Result<T>();
			result.success = false;
			result.code = code;
			result.message = message;
			result.body = body;
			return result;
		}
	}
}

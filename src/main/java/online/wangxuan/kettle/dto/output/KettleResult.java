package online.wangxuan.kettle.dto.output;

/**
 * Created by wangxuan on 2017/6/9.
 */
public class KettleResult<T> {

    private static final String SUCCESS_DESCRIPTION = "success";

    public static final Integer SUCCESS_CODE = 200;

    public static final Integer ERROR_CODE = -99999;

    public static final String ERROR_DESCRIPTION = "error";

    private boolean success;
    private T data;
    private String description;
    private Integer errorCode;

    public KettleResult() {
        this(false);
    }

    public KettleResult(boolean success) {
        this(success, SUCCESS_DESCRIPTION, SUCCESS_CODE);
    }

    public KettleResult(boolean success, String description) {
        this(success, description, SUCCESS_CODE);
    }

    public KettleResult(boolean success, String description, Integer errorCode) {
        this.success = success;
        this.description = description;
        this.errorCode = errorCode;
    }

    public KettleResult(boolean success, String description, Integer errorCode, T data) {
        this.success = success;
        this.description = description;
        this.errorCode = errorCode;
        this.data = data;
    }

    public static KettleResult error(){
        return new KettleResult(false, ERROR_DESCRIPTION, ERROR_CODE);
    }
    public static KettleResult error(Integer errorCode, String description) {
        return new KettleResult(false, description, errorCode);
    }
    public static KettleResult error(Object data, Integer errorCode, String description) {
        return new KettleResult(false, description, errorCode, data);
    }

    public static KettleResult error(Object data) {
        return new KettleResult(false, ERROR_DESCRIPTION, ERROR_CODE, data);
    }
    
    public static KettleResult success() {
        return new KettleResult(true, SUCCESS_DESCRIPTION, SUCCESS_CODE);
    }

    public static KettleResult success(String description) {
        return new KettleResult(true, description);
    }

    public static KettleResult success(Object data) {
        return new KettleResult(true, SUCCESS_DESCRIPTION, SUCCESS_CODE, data);
    }

    public static KettleResult success(String description, Object data) {
        return new KettleResult(true, description, SUCCESS_CODE, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}

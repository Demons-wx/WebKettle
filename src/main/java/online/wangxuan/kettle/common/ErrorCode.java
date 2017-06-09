package online.wangxuan.kettle.common;

/**
 * Created by wangxuan on 2017/6/9.
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    ERROR_DEFAULT(-99999, "发生异常，未知错误"),
    ERROR_CONNECT_REOP(1001, "连接资源库失败"),
    ERROR_CLOSE_TRANSACTION(1002, "关闭transaction失败"),
    ERROR_LOAD_JOBS(1003, "获取作业列表失败"),
    ERROR_LOAD_JOBELEM(1004, "获取作业对象失败"),
    ERROR_LOAD_ROOTDIR(1005, "获取根目录失败"),
    ERROR_RUN_JOB_LOCAL(1006, "本地执行job出错"),
    ERROR_LOAD_JOBDIR(1007, "获取作业目录失败"),
    ERROR_RUN_JOB_REMOTE(1008, "远程执行job出错"),
    ERROR_GET_JOB_STATUS(1009, "获取job执行日志异常");

    private int code;
    private String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg == null ? "" : msg.trim();
    }
}

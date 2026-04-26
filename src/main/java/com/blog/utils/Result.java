package com.blog.utils;

public class Result<T> {

    /*
    * 状态码
    * 提示信息
    * 返回的数据
    * */
    private Integer code;
    private String message;
    private T data;

    public Result(){}
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /*
    * 使用与无数据返回，且成功的请求
    * 删除、更新等
    * */
    public static <T> Result<T> success(){
        return new Result<>(200,"success",null);
    }

    /*
     * 使用与有数据返回，且成功的请求
     * 查询操作等
     * */
    public static <T> Result<T> success(T data){
        return new Result<>(200,"success",data);
    }

    /**
     * 失败（自定义消息）
     * 使用场景：业务校验失败、参数错误等
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败（自定义状态码和消息）
     * 使用场景：需要区分不同的错误类型
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

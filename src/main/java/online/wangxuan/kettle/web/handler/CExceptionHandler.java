package online.wangxuan.kettle.web.handler;

import com.alibaba.fastjson.JSONObject;
import online.wangxuan.kettle.common.ErrorCode;
import online.wangxuan.kettle.dto.output.KettleResult;
import online.wangxuan.kettle.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常增强，以JSON的形式返回给客服端
 * 异常增强类型：NullPointerException,RunTimeException,ClassCastException,
 　　　　　　　　 NoSuchMethodException,IOException,IndexOutOfBoundsException
 　　　　　　　　 以及springmvc自定义异常等，如下：
 SpringMVC自定义异常对应的status code
 Exception                       HTTP Status Code
 ConversionNotSupportedException         500 (Internal Server Error)
 HttpMessageNotWritableException         500 (Internal Server Error)
 HttpMediaTypeNotSupportedException      415 (Unsupported Media Type)
 HttpMediaTypeNotAcceptableException     406 (Not Acceptable)
 HttpRequestMethodNotSupportedException  405 (Method Not Allowed)
 NoSuchRequestHandlingMethodException    404 (Not Found)
 TypeMismatchException                   400 (Bad Request)
 HttpMessageNotReadableException         400 (Bad Request)
 MissingServletRequestParameterException 400 (Bad Request)
 *
 */
@ControllerAdvice
public class CExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    
    /***********************************Customer define Exception*****************************************/
    //业务异常
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public KettleResult BusinessExceptionHandler(BusinessException ex) {
        return doException(ex,ex.getCode());
    }

    /***********************************Java Exception*****************************************/
    //运行时异常
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public KettleResult runtimeExceptionHandler(RuntimeException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //空指针异常
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public KettleResult nullPointerExceptionHandler(NullPointerException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //类型转换异常
    @ExceptionHandler(ClassCastException.class)
    @ResponseBody
    public KettleResult classCastExceptionHandler(ClassCastException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //IO异常
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public KettleResult iOExceptionHandler(IOException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //未知方法异常
    @ExceptionHandler(NoSuchMethodException.class)
    @ResponseBody
    public KettleResult noSuchMethodExceptionHandler(NoSuchMethodException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //数组越界异常
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    public KettleResult indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    /***********************************spring framwork DefaultException*****************************************/

    //400错误
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public KettleResult requestNotReadable(HttpMessageNotReadableException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //400错误
    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    public KettleResult requestTypeMismatch(TypeMismatchException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //400错误
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public KettleResult requestMissingServletRequest(MissingServletRequestParameterException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //405错误
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public KettleResult request405(HttpRequestMethodNotSupportedException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //406错误
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseBody
    public KettleResult request406(HttpMediaTypeNotAcceptableException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //500错误
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    @ResponseBody
    public KettleResult server500(RuntimeException ex) {

        return doException(ex, ErrorCode.ERROR_DEFAULT.getCode());
    }

    //处理Exception
    public KettleResult doException(Exception ex, Integer errorCode){
        JSONObject jsonObject = new JSONObject();
        List<Map> stackList = new ArrayList<>();
        //获取异常堆栈，转换为JSONObject
        for (int i = 0; i < ex.getStackTrace().length; i++){
            Map<String,String> stack = new HashMap<>();
            stack.put("" + i, ex.getStackTrace()[i].toString());
            stackList.add(stack);
        }

        jsonObject.put("exception", ex.getClass().toString());
        jsonObject.put("ExceptionStartTrace",stackList);
        jsonObject.put("errorCode", errorCode);
        jsonObject.put("description",ex.getMessage());
        logger.error(jsonObject.toJSONString());

        return KettleResult.error(errorCode,ex.getMessage());
    }
}
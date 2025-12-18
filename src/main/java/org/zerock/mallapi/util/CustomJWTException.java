package org.zerock.mallapi.util;

public class CustomJWTException extends RuntimeException{
    
    public CustomJWTException(String msg){
        super(msg);
    }
    
}

//JWT예외 처리를 담당.

package com.my.lionrpc.protocol;

import java.io.Serializable;

/**
 * Created by littlechen on 17/5/21.
 */
public class ResponseMessage implements Serializable{

	private static final long serialVersionUID = -6845726676952200560L;
	private String requestId;
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}

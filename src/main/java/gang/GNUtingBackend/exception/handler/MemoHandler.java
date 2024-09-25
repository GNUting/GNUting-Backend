package gang.GNUtingBackend.exception.handler;


import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class MemoHandler extends GeneralException {

    public MemoHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
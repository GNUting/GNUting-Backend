package gang.GNUtingBackend.exception.handler;

import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class BoardHandler extends GeneralException {

    public BoardHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
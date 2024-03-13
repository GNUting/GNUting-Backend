package gang.GNUtingBackend.exception.handler;

import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class TokenHandler extends GeneralException {

    public TokenHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

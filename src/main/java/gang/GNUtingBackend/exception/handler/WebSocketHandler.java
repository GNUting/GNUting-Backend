package gang.GNUtingBackend.exception.handler;

import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class WebSocketHandler extends GeneralException {

    public WebSocketHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

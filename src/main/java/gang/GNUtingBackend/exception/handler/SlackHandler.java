package gang.GNUtingBackend.exception.handler;

import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class SlackHandler extends GeneralException {

    public SlackHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
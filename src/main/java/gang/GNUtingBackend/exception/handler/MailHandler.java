package gang.GNUtingBackend.exception.handler;

import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class MailHandler extends GeneralException {

    public MailHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

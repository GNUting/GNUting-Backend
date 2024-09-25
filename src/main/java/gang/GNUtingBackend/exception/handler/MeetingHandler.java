package gang.GNUtingBackend.exception.handler;


import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class MeetingHandler extends GeneralException {

    public MeetingHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
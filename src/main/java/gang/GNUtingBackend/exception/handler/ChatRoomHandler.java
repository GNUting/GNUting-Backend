package gang.GNUtingBackend.exception.handler;

import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class ChatRoomHandler extends GeneralException {

    public ChatRoomHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

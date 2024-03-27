package gang.GNUtingBackend.exception.handler;

import gang.GNUtingBackend.exception.GeneralException;
import gang.GNUtingBackend.response.code.BaseErrorCode;

public class ChatRoomUserHandler extends GeneralException {

    public ChatRoomUserHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

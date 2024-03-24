package gang.GNUtingBackend.chat.handler;

import gang.GNUtingBackend.chat.domain.enums.MessageType;
import gang.GNUtingBackend.chat.dto.ChatRequestDto;
import gang.GNUtingBackend.exception.handler.WebSocketHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessageSendingOperations messagingTemplate;

    // 연결 요청
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("새로운 웹 소켓에 연결되었습니다.");
    }

    // 구독 요청(입장)
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        logger.info("새로운 웹 소켓에 구독되었습니다.");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userEmail = (String)getValue(accessor, "userEmail");
        String userNickname = (String)getValue(accessor, "userNickname");
        Long chatRoomId = (Long)getValue(accessor, "chatRoomId");

        logger.info("{}({})님이 ChatRoomId : {}를 구독하였습니다.", userNickname, userEmail, chatRoomId);

        ChatRequestDto chatRequest = new ChatRequestDto(MessageType.ENTER,
                userNickname + " 님이 채팅방에 입장했습니다.");
        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chatRequest);

    }

    // 연결 해제
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userEmail = (String)getValue(accessor, "userEmail");
        String userNickname = (String)getValue(accessor, "userNickname");
        Long chatRoomId = (Long)getValue(accessor, "chatRoomId");

        logger.info("{}({})님이 ChatRoomId : {}를 떠났습니다.", userNickname, userEmail, chatRoomId);

        ChatRequestDto chatRequest = new ChatRequestDto(MessageType.LEAVE,
                userNickname + "님이 채팅방을 떠났습니다.");

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chatRequest);
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if (Objects.isNull(value)) {
            throw new WebSocketHandler(ErrorStatus.SESSION_ATTRIBUTE_NOT_FOUND);
        }

        return value;
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (Objects.isNull(sessionAttributes)) {
            throw new WebSocketHandler(ErrorStatus.SESSION_ATTRIBUTES_IS_NULL);
        }

        return sessionAttributes;
    }
}

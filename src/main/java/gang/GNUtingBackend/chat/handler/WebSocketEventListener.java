package gang.GNUtingBackend.chat.handler;

import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.exception.handler.ChatRoomHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final ChatRoomUserRepository chatRoomUserRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("새로운 웹 소켓 연결이 생성되었습니다.");
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        try {
            String userEmail = safelyGetValue(accessor, "userEmail", String.class);
            String userNickname = safelyGetValue(accessor, "userNickname", String.class);
            Long chatRoomId = safelyGetValue(accessor, "chatRoomId", Long.class);

            logger.info("{}({})님이 ChatRoomId : {}를 구독하였습니다.", userNickname, userEmail, chatRoomId);

        } catch (Exception e) { // WebSocketHandler 대신 Exception을 사용하여 모든 예외를 포착.
            logger.error("구독 처리 중 예외 발생: {}", e.getMessage(), e);
            // 필요한 예외 처리 로직을 추가 가능.
        }
    }



    @Transactional
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userEmail = safelyGetValue(accessor, "userEmail", String.class);
        String userNickname = safelyGetValue(accessor, "userNickname", String.class);
        Long chatRoomId = safelyGetValue(accessor, "chatRoomId", Long.class);

        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserEmail(chatRoomId, userEmail)
                .orElseThrow(() -> new ChatRoomHandler(ErrorStatus.NOT_FOUND_CHAT_ROOM_USER));
        chatRoomUser.setLastDisconnectedTime(LocalDateTime.now());
        chatRoomUserRepository.save(chatRoomUser);

        logger.info("{}({})님이 ChatRoomId : {}를 떠났습니다.", userNickname, userEmail, chatRoomId);
    }

    private <T> T safelyGetValue(StompHeaderAccessor accessor, String key, Class<T> type) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new IllegalArgumentException("Session attributes are null");
        }
        Object value = sessionAttributes.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Session attribute for key '" + key + "' not found");
        }
        return type.cast(value);
    }
}

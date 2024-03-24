package gang.GNUtingBackend.chat.handler;

import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.exception.handler.WebSocketHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import gang.GNUtingBackend.user.token.TokenProvider;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    private UserRepository userRepository;
    private ChatRoomUserRepository chatRoomUserRepository;
    private static final String DEFAULT_DESTINATION = "/sub/chat/";


    /**
     * webSocket을 통해 들어온 요청이 처리 되기전에 실행된다.
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization").substring(7);
            String email = tokenProvider.getUserEmail(jwtToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

            setValue(accessor, "userEmail", user.getEmail());
            setValue(accessor, "userNickname", user.getNickname());
            setValue(accessor, "profileImgUrl", user.getProfileImage());
        } else if(StompCommand.SUBSCRIBE.equals(command)) {

            String userEmail = (String)getValue(accessor, "userEmail");
            Long chatRoomId = parseChatRoomIdFromPath(accessor);
            setValue(accessor, "chatRoomId", chatRoomId);
            chatRoomUserRepository.findByChatRoomIdAndUserEmail(chatRoomId, userEmail)
                    .orElseThrow(() -> new WebSocketHandler(ErrorStatus.NOT_FOUND_CHAT_ROOM_USER));

        } else if (StompCommand.DISCONNECT.equals(command)) {

            String userNickname = (String)getValue(accessor, "userNickname");
            log.info("DISCONNECTED userNickname : {}", userNickname);

        }

        log.info("header : " + message.getHeaders());
        log.info("message:" + message);

        return message;
    }

    private Long parseChatRoomIdFromPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return Long.parseLong(destination.substring(DEFAULT_DESTINATION.length()));
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (Objects.isNull(sessionAttributes)) {
            throw new WebSocketHandler(ErrorStatus.SESSION_ATTRIBUTES_IS_NULL);
        }
        return sessionAttributes;
    }

    private void setValue(StompHeaderAccessor accessor, String key, Object value) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(key, value);
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if (Objects.isNull(value)) {
            throw new WebSocketHandler(ErrorStatus.SESSION_ATTRIBUTE_NOT_FOUND);
        }

        return value;
    }
}

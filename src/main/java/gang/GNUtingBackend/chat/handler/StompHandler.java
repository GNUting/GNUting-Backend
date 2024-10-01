package gang.GNUtingBackend.chat.handler;

import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.exception.handler.ChatRoomHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.exception.handler.WebSocketHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import gang.GNUtingBackend.user.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        try {
            switch (accessor.getCommand()) {
                case CONNECT:
                    handleConnect(accessor);
                    break;
                case SUBSCRIBE:
                    handleSubscribe(accessor);
                    break;
                case DISCONNECT:
                    handleDisconnect(accessor);
                    break;
                default:
                    log.debug("Unhandled STOMP command: {}", accessor.getCommand());
            }
        } catch (Exception e) {
            log.error("Error during WebSocket message handling: {}", e.getMessage(), e);
            // Depending on your error handling policy, you might want to return null or rethrow the exception
        }
        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String jwtToken = getJwtTokenFromHeader(accessor);
        String email = tokenProvider.getUserEmail(jwtToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        log.info("CONNECT - userEmail: {} | nickName: {}", user.getEmail(), user.getNickname());

        accessor.getSessionAttributes().put("userEmail", user.getEmail());
        accessor.getSessionAttributes().put("userNickname", user.getNickname());
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String userEmail = (String) accessor.getSessionAttributes().get("userEmail");
        Long chatRoomId = parseChatRoomIdFromPath(accessor);
        accessor.getSessionAttributes().put("chatRoomId", chatRoomId);
        chatRoomUserRepository.findByChatRoomIdAndUserEmail(chatRoomId, userEmail)
                .orElseThrow(() -> new ChatRoomHandler(ErrorStatus.NOT_FOUND_CHAT_ROOM_USER));

        log.info("SUBSCRIBE - ChatRoomId: {} | userEmail: {}", chatRoomId, userEmail);
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        String userNickname = (String) accessor.getSessionAttributes().get("userNickname");
        log.info("DISCONNECTED userNickname: {}", userNickname);
    }

    private String getJwtTokenFromHeader(StompHeaderAccessor accessor) {
        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            throw new WebSocketHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        }
        return jwtToken.substring(7);
    }

    private Long parseChatRoomIdFromPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            throw new WebSocketHandler(ErrorStatus.INVALID_DESTINATION);
        }
        return Long.parseLong(destination.split("/")[3]); // Assuming the destination format is "/sub/chatRoom/{id}"
    }

//    private Long parseChatRoomIdFromPath(StompHeaderAccessor accessor) {
//        String destination = accessor.getDestination();
//        if (destination == null) {
//            throw new WebSocketHandler(ErrorStatus.INVALID_DESTINATION);
//        }
//        // 경로가 예상보다 길 경우, 마지막 세그먼트를 채팅방 ID로 추정
//        String[] pathSegments = destination.split("/");
//        if (pathSegments.length < 2) {
//            throw new WebSocketHandler(ErrorStatus.DESTINATION_PATH_IS_SHORT);
//        }
//        // 마지막 세그먼트를 ID로 추출
//        try {
//            return Long.parseLong(pathSegments[pathSegments.length - 1]);
//        } catch (NumberFormatException e) {
//            throw new WebSocketHandler(ErrorStatus.LAST_SEGMENT_IS_NOT_CHATROOMID);
//        }
//    }
}
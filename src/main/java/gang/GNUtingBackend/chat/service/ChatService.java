package gang.GNUtingBackend.chat.service;

import gang.GNUtingBackend.chat.domain.Chat;
import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.dto.ChatRequestDto;
import gang.GNUtingBackend.chat.dto.ChatResponseDto;
import gang.GNUtingBackend.chat.repository.ChatRepository;
import gang.GNUtingBackend.chat.repository.ChatRoomRepository;
import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.exception.handler.ChatRoomHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import gang.GNUtingBackend.notification.service.FCMService;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final FCMService fcmService;

    @Transactional
    public ChatResponseDto sendMessage(ChatRequestDto chatRequestDto, Long chatRoomId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender(user.getNickname())
                .message(chatRequestDto.getMessage())
                .messageType(chatRequestDto.getMessageType())
                .build();

        chatRepository.save(chat);

        ChatResponseDto chatResponse = ChatResponseDto.builder()
                .id(chat.getId())
                .chatRoomId(chatRoomId)
                .messageType(chatRequestDto.getMessageType())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .message(chatRequestDto.getMessage())
                .createdDate(chat.getCreateDate())
                .build();

        messagingTemplate.convertAndSend("/sub/chatRoom/" + chatRoomId, chatResponse);

        notifyOtherUsers(chatRoom, chat, user);

        return chatResponse;
    }

    private void notifyOtherUsers(ChatRoom chatRoom, Chat chat, User user) {
        chatRoom.getChatRoomUsers().stream()
                .filter(chatRoomUser -> !chatRoomUser.getUser().equals(user) && chatRoomUser.getNotificationSetting() == NotificationSetting.ENABLE)
                .forEach(chatRoomUser -> {
                    if (hasNewMessages(chatRoomUser.getUser().getEmail(), chatRoom.getId())) {
                        fcmService.sendMessageTo(chatRoomUser.getUser(), chatRoomUser.getChatRoom().getTitle(), chat.getMessage());
                    }
                });
    }

    @Transactional(readOnly = true)
    public List<ChatResponseDto> findAllChatByChatRoomId(Long chatRoomId, String email) {
        List<Chat> chats = chatRepository.findByChatRoomId(chatRoomId);

        boolean isMember = chatRoomUserRepository.findByChatRoomIdAndUserEmail(chatRoomId, email).isPresent();
        if (!isMember) {
            throw new ChatRoomHandler(ErrorStatus.NOT_FOUND_CHAT_ROOM_USER);
        }

        return chats.stream().map(chat -> {
            Optional<User> user = userRepository.findByNickname(chat.getSender());
            String userEmail = user.map(User::getEmail).orElse(null);
            String userProfileImage = user.map(User::getProfileImage).orElse(null);
            String userNickname = user.map(User::getNickname).orElse(null);

//            User user = userRepository.findByNickname(chat.getSender())
//                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

            return ChatResponseDto.builder()
                    .id(chat.getId())
                    .chatRoomId(chatRoomId)
                    .messageType(chat.getMessageType())
                    .email(userEmail)
                    .profileImage(userProfileImage)
                    .nickname(userNickname)
                    .message(chat.getMessage())
                    .createdDate(chat.getCreateDate())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean hasNewMessages(String email, Long chatRoomId) {
        LocalDateTime lastDisconnectedTime = chatRoomUserRepository.findLastDisconnectedTimeByUserEmailAndChatRoomId(email, chatRoomId);
        Long newMessagesCount = chatRepository.countByChatRoomIdAndCreateDateAfter(chatRoomId, lastDisconnectedTime);

        return newMessagesCount > 0;
    }
}

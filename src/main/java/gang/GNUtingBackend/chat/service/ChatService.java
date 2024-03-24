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
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import java.util.List;
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

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chatResponse);

        return chatResponse;
    }

    @Transactional(readOnly = true)
    public List<ChatResponseDto> findAllChatByChatRoomId(Long chatRoomId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        List<Chat> chats = chatRepository.findByChatRoomId(chatRoomId);

        return chats.stream().map(chat -> ChatResponseDto.builder()
                        .id(chat.getId())
                        .chatRoomId(chatRoomId)
                        .messageType(chat.getMessageType())
                        .email(email)
                        .profileImage(user.getProfileImage())
                        .nickname(user.getNickname())
                        .message(chat.getMessage())
                        .createdDate(chat.getCreateDate())
                        .build())
                .collect(Collectors.toList());
    }
}

package gang.GNUtingBackend.chat.service;

import gang.GNUtingBackend.chat.domain.Chat;
import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.domain.enums.MessageType;
import gang.GNUtingBackend.chat.dto.ChatRequestDto;
import gang.GNUtingBackend.chat.dto.ChatResponseDto;
import gang.GNUtingBackend.chat.dto.ChatRoomResponseDto;
import gang.GNUtingBackend.chat.dto.ChatRoomUserDto;
import gang.GNUtingBackend.chat.repository.ChatRepository;
import gang.GNUtingBackend.chat.repository.ChatRoomRepository;
import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.exception.handler.ChatRoomHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import gang.GNUtingBackend.notification.service.FCMService;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.auth.PrincipalDetails;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

        LocalDate today = LocalDate.now();

        LocalDateTime lastMessageTime = chatRepository.findLastMessageTimeByChatRoomId(chatRoom.getId()); //재욱추가
        //findFirstByChatRoomOrderByCreateDateDesc
        Optional<Chat> lastChat = chatRepository.findByTopChat(chatRoom, MessageType.CHAT, lastMessageTime); //재욱변경

        LocalDate lastMessageDate = lastChat.isPresent() ? lastChat.get().getCreateDate().toLocalDate() : null;


        if (lastMessageDate == null || !lastMessageDate.isEqual(today)) {
            Chat dateChat = Chat.builder()
                    .chatRoom(chatRoom)
                    .sender("관리자")
                    .messageType(MessageType.DAILY)
                    .message(today.format(DateTimeFormatter.ofPattern("uuuu년 M월 d일 EEEE", Locale.KOREA)))
                    .build();
            chatRepository.save(dateChat);

            ChatResponseDto dateChatResponse = ChatResponseDto.builder()
                    .id(dateChat.getId())
                    .chatRoomId(chatRoom.getId())
                    .messageType(dateChat.getMessageType())
                    .email(null)
                    .profileImage(null)
                    .nickname(null)
                    .message(dateChat.getMessage())
                    .createdDate(dateChat.getCreateDate())
                    .department(null)
                    .studentId(null)
                    .build();

            messagingTemplate.convertAndSend("/sub/chatRoom/" + chatRoomId, dateChatResponse);
        }

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
                .department(user.getDepartment())
                .studentId(user.getStudentId() + "학번")
                .build();

        messagingTemplate.convertAndSend("/sub/chatRoom/" + chatRoomId, chatResponse);

        notifyOtherUsers(chatRoom, chat, user);

        return chatResponse;
    }


    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findChatRoomsByUserEmail(String email) {
        List<ChatRoomUser> allByUserEmail = chatRoomUserRepository.findAllByUserEmail(email);

        List<ChatRoomResponseDto> chatRooms = allByUserEmail.stream()
                .map(cru -> {
                    ChatRoom chatRoom = cru.getChatRoom();
                    List<String> chatRoomUserProfileImages = chatRoom.getChatRoomUsers().stream()
                            .filter(chatRoomUser -> !chatRoomUser.getUser().getEmail().equals(email))
                            .map(chatRoomUser -> chatRoomUser.getUser().getProfileImage())
                            .collect(Collectors.toList());

                    // 현재 사용자를 제외하고 필터링하여 chatRoomUserDto 리스트 반환
                    List<ChatRoomUserDto> chatRoomUserDtos = chatRoom.getChatRoomUsers().stream()
                            .filter(chatRoomUser -> !chatRoomUser.getUser().getEmail().equals(email))
                            .map(cruUser -> ChatRoomUserDto.builder()
                                    .id(cruUser.getId())
                                    .userId(cruUser.getUser().getId())
                                    .chatRoomId(chatRoom.getId())
                                    .nickname(cruUser.getUser().getNickname())
                                    .profileImage(cruUser.getUser().getProfileImage())
                                    .department(cruUser.getUser().getDepartment())
                                    .studentId(cruUser.getUser().getStudentId())
                                    .build())
                            .collect(Collectors.toList());

                    boolean hasNewMessage = hasNewMessages(email, chatRoom.getId());
                    LocalDateTime lastMessageTime = chatRepository.findLastMessageTimeByChatRoomId(chatRoom.getId());
                    String lastMessage = chatRepository.findByTopChat(chatRoom, MessageType.CHAT,lastMessageTime)
                            .map(Chat::getMessage)
                            .orElse("");

                    return ChatRoomResponseDto.builder()
                            .id(chatRoom.getId())
                            .title(chatRoom.getTitle())
                            .leaderUserDepartment(chatRoom.getLeaderUserDepartment())
                            .applyLeaderDepartment(chatRoom.getApplyLeaderDepartment())
                            .ChatRoomUserProfileImages(chatRoomUserProfileImages)
                            .hasNewMessage(hasNewMessage)
                            .chatRoomUsers(chatRoomUserDtos)
                            .lastMessageTime(lastMessageTime)
                            .lastMessage(lastMessage)
                            .build();
                })
                .sorted(Comparator.comparing(ChatRoomResponseDto::getLastMessageTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        return chatRooms;
    }


    private void notifyOtherUsers(ChatRoom chatRoom, Chat chat, User user) {
        chatRoom.getChatRoomUsers().stream()
                .filter(chatRoomUser -> !chatRoomUser.getUser().equals(user) && chatRoomUser.getNotificationSetting() == NotificationSetting.ENABLE)
                .forEach(chatRoomUser -> {
                    if (hasNewMessages(chatRoomUser.getUser().getEmail(), chatRoom.getId())) {
                        fcmService.sendMessageToNotSave(chatRoomUser.getUser(), chatRoomUser.getChatRoom().getTitle(), chat.getMessage(),"chat",chatRoom.getId());
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
            String userDepartment = user.map(User::getDepartment).orElse(null);
            String userStudentId = user.map(User::getStudentId).orElse(null);

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
                    .department(userDepartment)
                    .studentId(userStudentId + "학번")
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

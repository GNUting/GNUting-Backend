package gang.GNUtingBackend.chat.service;

import gang.GNUtingBackend.board.dto.ChatMemberDto;
import gang.GNUtingBackend.chat.domain.Chat;
import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.domain.enums.MessageType;
import gang.GNUtingBackend.chat.dto.ChatRequestDto;
import gang.GNUtingBackend.chat.dto.ChatRoomResponseDto;
import gang.GNUtingBackend.chat.dto.ChatRoomUserDto;
import gang.GNUtingBackend.chat.dto.ChatRoomUserInfoDto;
import gang.GNUtingBackend.chat.repository.ChatRepository;
import gang.GNUtingBackend.chat.repository.ChatRoomRepository;
import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.exception.handler.ChatRoomHandler;
import gang.GNUtingBackend.exception.handler.ChatRoomUserHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserService chatRoomUserService;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;
    private final ChatRepository chatRepository;

    /**
     * 채팅방 생성
     *
     * @param chatMemberDto
     * @return
     */
    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatMemberDto chatMemberDto) {
        ChatRoom chatRoom = ChatRoom.builder()
                .title(chatMemberDto.getTitle())
                .leaderUserDepartment(chatMemberDto.getParticipantUserDepartment())
                .applyLeaderDepartment(chatMemberDto.getApplyUserDepartment())
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);

        List<ChatRoomUser> chatRoomUsers = new ArrayList<>();
        ChatRoom finalChatRoom = chatRoom;
        chatMemberDto.getApplyUser().forEach(user ->
                chatRoomUsers.add(chatRoomUserService.createChatRoomUser(finalChatRoom, user)));
        ChatRoom finalChatRoom1 = chatRoom;
        chatMemberDto.getParticipantUser().forEach(user ->
                chatRoomUsers.add(chatRoomUserService.createChatRoomUser(finalChatRoom1, user)));

        chatRoom.setChatRoomUsers(chatRoomUsers);
        chatRoomRepository.save(chatRoom);

        String enterChatRoomUsers = chatRoomUsers.stream()
                .map(chatRoomUser -> chatRoomUser.getUser().getNickname())
                .collect(Collectors.joining("님, ", "", "님이 채팅방에 입장하셨습니다."));

        ChatRequestDto enterMessage = new ChatRequestDto(MessageType.ENTER, enterChatRoomUsers);
        messagingTemplate.convertAndSend("/sub/chatRoom/" + chatRoom.getId(), enterMessage);

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender("관리자")
                .messageType(enterMessage.getMessageType())
                .message(enterMessage.getMessage())
                .build();

        chatRepository.save(chat);

        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .leaderUserDepartment(chatRoom.getLeaderUserDepartment())
                .applyLeaderDepartment(chatRoom.getApplyLeaderDepartment())
                .build();
    }

    /**
     * 해당 이메일을 가진 유저가 참여중인 모든 채팅방을 조회
     * hasNewMessage를 기준으로 정렬
     * @param email
     * @return
     */
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

                    List<ChatRoomUserDto> chatRoomUserDtos = chatRoom.getChatRoomUsers().stream()
                            .filter(chatRoomUser -> !chatRoomUser.getUser().getEmail().equals(email))
                            .map(chatRoomUser -> ChatRoomUserDto.builder()
                                    .id(chatRoomUser.getId())
                                    .userId(chatRoomUser.getUser().getId())
                                    .chatRoomId(chatRoomUser.getId())
                                    .nickname(chatRoomUser.getUser().getNickname())
                                    .profileImage(chatRoomUser.getUser().getProfileImage())
                                    .department(chatRoomUser.getUser().getDepartment())
                                    .studentId(chatRoomUser.getUser().getStudentId()+"학번")
                                    .build())
                            .collect(Collectors.toList());

                    boolean hasNewMessage = chatService.hasNewMessages(email, chatRoom.getId());
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

    /**
     * 해당 이메일을 가진 유저가 참여중인 특정 채팅방의 사용자들을 조회
     * @param chatRoomId
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public List<ChatRoomUserDto> findChatRoomUsersByUserEmail(Long chatRoomId, String email) {
        return chatRoomUserRepository.findByChatRoomIdAndUserEmail(chatRoomId, email)
                .map(chatRoomUser -> {
                    ChatRoom chatRoom = chatRoomUser.getChatRoom();
                    return chatRoom.getChatRoomUsers().stream()
                            .map(cru -> ChatRoomUserDto.builder()
                                    .id(cru.getId())
                                    .userId(cru.getUser().getId())
                                    .chatRoomId(chatRoom.getId())
                                    .nickname(cru.getUser().getNickname())
                                    .profileImage(cru.getUser().getProfileImage())
                                    .department(cru.getUser().getDepartment())
                                    .studentId(cru.getUser().getStudentId()+"학번")
                                    .build())
                            .collect(Collectors.toList());
                })
                .orElseThrow(() -> new ChatRoomHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));
    }

    /**
     * 해당 이메일을 가진 유저가 특정 채팅방 나가기
     * @param chatRoomId
     * @param email
     */
    @Transactional
    public boolean leaveChatRoom(Long chatRoomId, String email) {
        ChatRoomUser cru = chatRoomUserRepository.findByChatRoomIdAndUserEmail(chatRoomId, email)
                .orElseThrow(() -> new ChatRoomUserHandler(ErrorStatus.NOT_FOUND_CHAT_ROOM_USER));

        chatRoomUserRepository.delete(cru);

        String leaveChatRoomUser = cru.getUser().getNickname() + "님이 채팅방을 나갔습니다.";

        ChatRequestDto leaveMessage = new ChatRequestDto(MessageType.LEAVE, leaveChatRoomUser);
        messagingTemplate.convertAndSend("/sub/chatRoom/" + chatRoomId, leaveMessage);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender("관리자")
                .messageType(leaveMessage.getMessageType())
                .message(leaveMessage.getMessage())
                .build();

        chatRepository.save(chat);

        if (chatRoomUserRepository.findAllByChatRoomId(chatRoomId).isEmpty()) {
            chatRoomRepository.deleteById(chatRoomId);
        }

        return true;
    }
}

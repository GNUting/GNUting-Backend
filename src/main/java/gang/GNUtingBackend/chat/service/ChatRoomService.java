package gang.GNUtingBackend.chat.service;

import gang.GNUtingBackend.board.dto.ChatMemberDto;
import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.dto.ChatRoomResponseDto;
import gang.GNUtingBackend.chat.dto.ChatRoomUserDto;
import gang.GNUtingBackend.chat.repository.ChatRoomRepository;
import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserService chatRoomUserService;
    private final ChatRoomUserRepository chatRoomUserRepository;

    /**
     * 채팅방 생성
     * @param chatMemberDto
     * @return
     */
    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatMemberDto chatMemberDto) {
        ChatRoom chatRoom = ChatRoom.builder()
                .title(chatMemberDto.getBoard().getTitle())
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

        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .leaderUserDepartment(chatRoom.getLeaderUserDepartment())
                .applyLeaderDepartment(chatRoom.getApplyLeaderDepartment())
                .build();
    }

    /**
     * 해당 이메일을 가진 유저가 참여중인 모든 채팅방을 조회
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findChatRoomsByUserEmail(String email) {
        List<ChatRoomUser> allByUserEmail = chatRoomUserRepository.findAllByUserEmail(email);

        return allByUserEmail.stream()
                .map(cru -> {
                    ChatRoom chatRoom = cru.getChatRoom();
                    List<String> chatRoomUserProfileImages = chatRoom.getChatRoomUsers().stream()
                            .filter(chatRoomUser -> !chatRoomUser.getUser().getEmail().equals(email))
                            .map(chatRoomUser -> chatRoomUser.getUser().getProfileImage())
                            .collect(Collectors.toList());

                    return ChatRoomResponseDto.builder()
                            .id(chatRoom.getId())
                            .title(chatRoom.getTitle())
                            .leaderUserDepartment(chatRoom.getLeaderUserDepartment())
                            .applyLeaderDepartment(chatRoom.getApplyLeaderDepartment())
                            .ChatRoomUserProfileImages(chatRoomUserProfileImages)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

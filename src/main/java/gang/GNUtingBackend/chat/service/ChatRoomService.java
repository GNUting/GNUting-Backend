package gang.GNUtingBackend.chat.service;

import gang.GNUtingBackend.board.dto.ChatMemberDto;
import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.dto.ChatRoomResponse;
import gang.GNUtingBackend.chat.repository.ChatRoomRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserService chatRoomUserService;

    @Transactional
    public ChatRoomResponse createChatRoom(ChatMemberDto chatMemberDto) {
        ChatRoom chatRoom = ChatRoom.builder()
                .title(chatMemberDto.getBoard().getTitle())
                .leaderUserDepartment(chatMemberDto.getParticipantUserDepartment())
                .applyLeaderDepartment(chatMemberDto.getApplyUserDepartment())
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);

        Set<ChatRoomUser> chatRoomUsers = new HashSet<>();
        ChatRoom finalChatRoom = chatRoom;
        chatMemberDto.getApplyUser().forEach(user ->
                chatRoomUsers.add(chatRoomUserService.createChatRoomUser(finalChatRoom, user)));
        ChatRoom finalChatRoom1 = chatRoom;
        chatMemberDto.getParticipantUser().forEach(user ->
                chatRoomUsers.add(chatRoomUserService.createChatRoomUser(finalChatRoom1, user)));

        chatRoom.setChatRoomUsers(chatRoomUsers);
        chatRoomRepository.save(chatRoom);

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .leaderUserDepartment(chatRoom.getLeaderUserDepartment())
                .applyLeaderDepartment(chatRoom.getApplyLeaderDepartment())
                .build();
    }

}

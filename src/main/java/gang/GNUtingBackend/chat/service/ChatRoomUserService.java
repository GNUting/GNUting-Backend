package gang.GNUtingBackend.chat.service;

import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomUserService {

    private final ChatRoomUserRepository chatRoomUserRepository;

    @Transactional
    public ChatRoomUser createChatRoomUser(ChatRoom chatRoom, User user) {
        ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatRoomUserRepository.save(chatRoomUser);

        return chatRoomUser;
    }
}

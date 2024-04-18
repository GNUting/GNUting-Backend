package gang.GNUtingBackend.chat.service;

import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.exception.handler.ChatRoomHandler;
import gang.GNUtingBackend.exception.handler.ChatRoomUserHandler;
import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
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
                .notificationSetting(NotificationSetting.ENABLE)
                .build();
        chatRoomUserRepository.save(chatRoomUser);

        return chatRoomUser;
    }

    @Transactional
    public boolean updateNotificationSetting(Long chatRoomId, String email, NotificationSetting notificationSetting) {
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserEmail(chatRoomId, email)
                .orElseThrow(() -> new ChatRoomUserHandler(ErrorStatus.NOT_FOUND_CHAT_ROOM_USER));

        chatRoomUser.updateNotificationSetting(notificationSetting);
        chatRoomUserRepository.save(chatRoomUser);

        return true;
    }
}

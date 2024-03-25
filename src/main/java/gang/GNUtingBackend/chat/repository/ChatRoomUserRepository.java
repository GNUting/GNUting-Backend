package gang.GNUtingBackend.chat.repository;

import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomUserRepository extends JpaRepository <ChatRoomUser, Long> {

    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.chatRoom.id = :chatRoomId AND cru.user.email = :email")
    Optional<ChatRoomUser> findByChatRoomIdAndUserEmail(Long chatRoomId, String email);

    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.user.email = :email")
    List<ChatRoomUser> findAllByUserEmail(String email);
}

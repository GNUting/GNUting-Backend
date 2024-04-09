package gang.GNUtingBackend.chat.repository;

import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomUserRepository extends JpaRepository <ChatRoomUser, Long> {

    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.chatRoom.id = :chatRoomId AND cru.user.email = :email")
    Optional<ChatRoomUser> findByChatRoomIdAndUserEmail(@Param("chatRoomId") Long chatRoomId, @Param("email") String email);

    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.user.email = :email")
    List<ChatRoomUser> findAllByUserEmail(@Param("email") String email);

    @Query("SELECT cru.lastDisconnectedTime FROM ChatRoomUser cru WHERE cru.user.email = :email AND cru.chatRoom.id = :chatRoomId")
    LocalDateTime findLastDisconnectedTimeByUserEmailAndChatRoomId(@Param("email") String email, @Param("chatRoomId") Long chatRoomId);

    List<ChatRoomUser> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}

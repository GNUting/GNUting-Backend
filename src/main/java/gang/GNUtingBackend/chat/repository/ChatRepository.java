package gang.GNUtingBackend.chat.repository;

import gang.GNUtingBackend.chat.domain.Chat;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByChatRoomId(Long chatRoomId);

    Long countByChatRoomIdAndCreatedAtAfter(Long chatRoomId, LocalDateTime lastDisconnectedTime);
}

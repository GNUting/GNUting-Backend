package gang.GNUtingBackend.chat.repository;

import gang.GNUtingBackend.chat.domain.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findById(Long ChatRoomId);
}

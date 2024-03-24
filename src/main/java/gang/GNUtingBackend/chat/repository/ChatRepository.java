package gang.GNUtingBackend.chat.repository;

import gang.GNUtingBackend.chat.domain.Chat;
import gang.GNUtingBackend.chat.dto.ChatResponseDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByChatRoomId(Long chatRoomId);
}

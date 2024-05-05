package gang.GNUtingBackend.chat.repository;

import gang.GNUtingBackend.chat.domain.Chat;
import gang.GNUtingBackend.chat.domain.ChatRoom;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByChatRoomId(Long chatRoomId);

    Long countByChatRoomIdAndCreateDateAfter(Long chatRoomId, LocalDateTime lastDisconnectedTime);

    /**
     * 해당 채팅방에 마지막 메세지 시간 조회
     * @param chatRoomId
     * @return
     */
    @Query("SELECT MAX(c.createDate) FROM Chat c WHERE c.chatRoom.id = :chatRoomId")
    LocalDateTime findLastMessageTimeByChatRoomId(Long chatRoomId);

    Chat findTopByChatRoomOrderByCreateDateDesc(ChatRoom chatRoom);
}

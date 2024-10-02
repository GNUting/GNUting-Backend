package gang.GNUtingBackend.chat.repository;

import gang.GNUtingBackend.chat.domain.Chat;
import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.enums.MessageType;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    /**
     * MessageType이 CHAT인 메세지 중에서 가장 최근 메시지를 조회
     * @param chatRoom
     * @param messageType
     * @return
     */
    @Query("select c from Chat c where c.chatRoom = :chatRoom and c.messageType = :messageType order by c.createDate desc")
    Optional<Chat> findFirstByChatRoomOrderByCreateDateDesc(ChatRoom chatRoom, MessageType messageType);

    @Query("select c from Chat c where c.chatRoom = :chatRoom and c.messageType = :messageType and c.createDate = :createDate order by c.createDate desc")
    Optional<Chat> findByTopChat(ChatRoom chatRoom, MessageType messageType, LocalDateTime createDate);

    List<Chat> findBySender(String nickname);
}

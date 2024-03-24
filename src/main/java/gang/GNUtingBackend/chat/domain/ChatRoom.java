package gang.GNUtingBackend.chat.domain;

import gang.GNUtingBackend.user.domain.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String leaderUserDepartment;
    private String applyLeaderDepartment;

    @OneToMany(mappedBy = "chatRoom")
    private Set<ChatRoomUser> chatRoomUsers = new HashSet<>();

    public void setChatRoomUsers(Set<ChatRoomUser> chatRoomUsers) {
        this.chatRoomUsers = chatRoomUsers;
    }
}

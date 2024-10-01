package gang.GNUtingBackend.board.repository;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.user.domain.User;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardApplyLeaderRepository extends JpaRepository<BoardApplyLeader, Long> {

    void deleteByBoardId(Board boardDelete);

    List<BoardApplyLeader> findByBoardId(Board board);


    @Query("SELECT bal FROM BoardApplyLeader bal WHERE bal.boardId = :board " +
            "AND bal.receiveShowStatus = 'SHOW'")
    List<BoardApplyLeader> findByBoardIdAndNotHide(Board board);

    @Query("SELECT bal FROM BoardApplyLeader bal WHERE bal.leaderId = :user " +
            "AND bal.applyShowStatus = 'SHOW' " +
            "ORDER BY bal.modifiedDate DESC, bal.createdDate DESC")
    List<BoardApplyLeader> findByLeaderIdOrderByModifiedDateDescCreatedDateDesc(User user);

    @Modifying
    @Query("UPDATE BoardApplyLeader bal SET bal.receiveShowStatus = 'HIDE' WHERE bal = :boardApplyLeader")
    void updateReceivedStateHide(BoardApplyLeader boardApplyLeader);

    @Modifying
    @Query("UPDATE BoardApplyLeader bal SET bal.applyShowStatus = 'HIDE' WHERE bal = :boardApplyLeader")
    void updateApplyStateHide(BoardApplyLeader boardApplyLeader);


    @Query(" SELECT bal FROM BoardApplyLeader bal WHERE bal.boardId= :boardId " + "AND bal.status ='대기중'")
    List<BoardApplyLeader> findByBoardIdAndWaiting(Board boardId);
}

//package gang.GNUtingBackend.meeting.repository;
//
//import gang.GNUtingBackend.meeting.entity.Meeting;
//import gang.GNUtingBackend.user.domain.User;
//import gang.GNUtingBackend.user.domain.enums.Gender;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface MeetingRepository extends JpaRepository<Meeting,Long> {
//
//    @Query("SELECT m FROM Meeting m WHERE m.userId = :user AND m.status = 'OPEN'")
//
//    Meeting findByUserAndStatusOpen(User user);
//
//    @Query(value = "SELECT m FROM Meeting m WHERE m.gender != :userGender AND m.status = 'OPEN' ORDER BY RAND() ")
//    List<Meeting> findRandomAndGenderList(Gender userGender, Pageable pageable);
//
//
//}

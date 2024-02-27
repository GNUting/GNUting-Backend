package gang.GNUtingBackend.user.repository;

import gang.GNUtingBackend.user.domain.User;
import java.util.Optional;

import gang.GNUtingBackend.user.domain.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일을 통해 해당 이메일 주소를 가진 User를 찾는다.
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.gender = :gender and u.nickname = :nickname")
    User findByUserSearch(Gender gender,String nickname);

    /**
     * 닉네임을 통해 해당 닉네임을 가진 User를 찾는다.
     * @param nickname
     * @return
     */
    Optional<User> findByNickname(String nickname);
}

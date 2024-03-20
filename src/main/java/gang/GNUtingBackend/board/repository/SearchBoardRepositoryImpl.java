package gang.GNUtingBackend.board.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gang.GNUtingBackend.board.dto.BoardSearchResultDto;
import gang.GNUtingBackend.board.entity.QBoard;
import gang.GNUtingBackend.user.domain.QUser;
import gang.GNUtingBackend.user.domain.enums.Gender;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchBoardRepositoryImpl implements SearchBoardRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<BoardSearchResultDto> searchByTitleOrDepartment(String keyword, String email, Pageable pageable) {

        QBoard qBoard = QBoard.board;
        QUser qUser = QUser.user;

        Gender userGender = getUserGenderByEmail(email);

        List<BoardSearchResultDto> results = jpaQueryFactory
                .select(Projections.constructor(BoardSearchResultDto.class,
                        qBoard.id,
                        qBoard.title,
                        qUser.department,
                        qUser.studentId,
                        qBoard.inUserCount))
                .from(qBoard)
                .join(qBoard.userId, qUser)
                .where(qBoard.title.contains(keyword)
                        .or(qUser.department.contains(keyword))
                        .and(qBoard.gender.ne(userGender)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .selectFrom(qBoard)
                .join(qBoard.userId, qUser)
                .where(qBoard.title.contains(keyword)
                        .or(qUser.department.contains(keyword))
                        .and(qBoard.gender.ne(userGender)))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private Gender getUserGenderByEmail(String userEmail) {
        QUser qUser = QUser.user;
        Gender gender = jpaQueryFactory
                .select(qUser.gender)
                .from(qUser)
                .where(qUser.email.eq(userEmail))
                .fetchOne();
        return gender == Gender.MALE ? Gender.MALE : Gender.FEMALE;
    }

}

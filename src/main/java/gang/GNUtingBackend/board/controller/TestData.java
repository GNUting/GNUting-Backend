//package gang.GNUtingBackend.board.controller;
//
//import gang.GNUtingBackend.board.entity.Board;
//import gang.GNUtingBackend.board.entity.enums.Status;
//import gang.GNUtingBackend.board.repository.BoardRepository;
//
//import gang.GNUtingBackend.user.domain.User;
//import gang.GNUtingBackend.user.domain.enums.Gender;
//import gang.GNUtingBackend.user.domain.enums.UserRole;
//import gang.GNUtingBackend.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//
//import java.time.LocalDate;
//
//@RequiredArgsConstructor
//public class TestData {
//
//    private final BoardRepository boardRepository;
//    private final UserRepository userRepository;
//
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void initData() {
//        // 먼저 User 객체를 생성하고 저장
//        User user = User.builder()
//                .email("example@email.com")
//                .password("examplepassword")
//                .name("John Doe")
//                .phoneNumber("1234567890")
//                .gender(Gender.MALE) // 적절한 성별로 변경
//                .birthDate(LocalDate.of(1990, 1, 1)) // 적절한 생년월일로 변경
//                .nickname("john_doe")
//                .department("Computer Science")
//                .studentId("123456789")
//                .profileImage("profile.jpg")
//                .userRole(UserRole.ROLE_USER) // 적절한 역할로 변경
//                .build();
//
//        User savedUser = userRepository.save(user); // 유저 저장
//        for (int i = 0; i < 100; i++) {
//            boardRepository.save(Board.builder()
//                    .title("Example Title")
//                    .detail("Example Detail")
//                    .status(Status.OPEN) // 적절한 상태로 변경
//                    .gender(Gender.MALE) // 적절한 성별로 변경
//                    .userId(savedUser) // 저장된 User 객체 할당
//                    .build());
//        }
//    }
//
//
//}

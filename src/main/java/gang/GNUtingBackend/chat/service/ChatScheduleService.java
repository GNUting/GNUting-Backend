//package gang.GNUtingBackend.chat.service;
//
//import gang.GNUtingBackend.chat.domain.Chat;
//import gang.GNUtingBackend.chat.domain.enums.MessageType;
//import gang.GNUtingBackend.chat.dto.ChatResponseDto;
//import gang.GNUtingBackend.chat.repository.ChatRepository;
//import gang.GNUtingBackend.chat.repository.ChatRoomRepository;
//import gang.GNUtingBackend.user.domain.User;
//import gang.GNUtingBackend.user.repository.UserRepository;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Locale;
//import java.util.Optional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ChatScheduleService {
//
//    private final SimpMessageSendingOperations messagingTemplate;
//    private final ChatRoomRepository chatRoomRepository;
//    private final ChatRepository chatRepository;
//    private final UserRepository userRepository;
//
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void sendDailyMessage() {
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu년 M월 d일 EEEE", Locale.KOREA);
//        String message = LocalDate.now().format(dateTimeFormatter);
//
//        chatRoomRepository.findAll().forEach(chatRoom -> {
//            Chat chat = Chat.builder()
//                    .chatRoom(chatRoom)
//                    .sender("관리자")
//                    .messageType(MessageType.DAILY)
//                    .message(message)
//                    .build();
//
//            chatRepository.save(chat);
//
//            Optional<User> user = userRepository.findByNickname(chat.getSender());
//            String userEmail = user.map(User::getEmail).orElse(null);
//            String userProfileImage = user.map(User::getProfileImage).orElse(null);
//            String userNickname = user.map(User::getNickname).orElse(null);
//            String userDepartment = user.map(User::getDepartment).orElse(null);
//            String userStudentId = user.map(User::getStudentId).orElse(null);
//
//            ChatResponseDto chatResponse = ChatResponseDto.builder()
//                    .id(chat.getId())
//                    .chatRoomId(chatRoom.getId())
//                    .messageType(chat.getMessageType())
//                    .email(userEmail)
//                    .profileImage(userProfileImage)
//                    .nickname(userNickname)
//                    .message(chat.getMessage())
//                    .createdDate(chat.getCreateDate())
//                    .department(userDepartment)
//                    .studentId(userStudentId + "학번")
//                    .build();
//
//            messagingTemplate.convertAndSend("/sub/chatRoom/" + chatRoom.getId(), chatResponse);
//        });
//    }
//}

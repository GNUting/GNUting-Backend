package gang.GNUtingBackend.notification.service;

import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.notification.dto.UserNotificationResponseDto;
import gang.GNUtingBackend.notification.entity.UserNotification;
import gang.GNUtingBackend.notification.repository.UserNotificationRepository;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    public void saveNotification(User user, String notification) {
        UserNotification userNotification = UserNotification.builder()
                .userId(user)
                .notification(notification)
                .build();
        userNotificationRepository.save(userNotification);
    }

    public List<UserNotificationResponseDto> showNotification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        List<UserNotification> userNotifications = userNotificationRepository.findByUserId(user);
        return userNotifications.stream().map(UserNotificationResponseDto::toDto).collect(Collectors.toList());
    }

    public String deleteNotification(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        UserNotification userNotification=userNotificationRepository.findById(id)
                .orElseThrow(()-> new BoardHandler(ErrorStatus.INVALID_ACCESS));
        if(user!=userNotification.getUserId()){
            throw new BoardHandler(ErrorStatus.INVALID_ACCESS);
        }
        userNotificationRepository.deleteById(id);

        return id+"알림이 삭제되었습니다";
    }
}

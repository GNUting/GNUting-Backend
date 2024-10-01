package gang.GNUtingBackend.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import com.google.gson.JsonParseException;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.notification.dto.FCMTokenSaveDto;
import gang.GNUtingBackend.notification.dto.FcmMessage;
import gang.GNUtingBackend.notification.entity.FCM;
import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import gang.GNUtingBackend.notification.repository.FCMRepository;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import gang.GNUtingBackend.user.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.aspectj.lang.annotation.Around;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.joda.time.DateTime;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {
    private final UserRepository userRepository;
    private final FCMRepository fcmRepository;
    private final UserNotificationService userNotificationService;


    public boolean sendMessageTo(User findId, String title, String body,String location,Long locationId) {
        // 알림이 활성화되어 있지 않으면 알림 메세지 보내지 않도록 구현
        if (findId.getNotificationSetting() != NotificationSetting.ENABLE) {
            return false;
        }

        try {
            List<String> fcms = new ArrayList<>();
            List<FCM> fcmTokens = fcmRepository.findByUserId(findId);
            for (FCM fcmToken : fcmTokens) {
                fcms.add(fcmToken.getFcmToken());
            }
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(
                            AndroidConfig.builder()
                                    .setNotification(
                                            AndroidNotification.builder()
                                                    .setTitle(title)
                                                    .setBody(body)
                                                    .setClickAction("push_click")
                                                    .build()
                                    )
                                    .putData("location",location)
                                    .putData("locationId",locationId.toString())
                                    .build()
                    )
                    .setApnsConfig(
                            ApnsConfig.builder()
                                    .setAps(Aps.builder()
                                            .setCategory("push_click")
                                            .build()
                                    )

                                    .putCustomData("location",location)
                                    .putCustomData("locationId",locationId.toString())
                                    .build()

                    )
                    .addAllTokens(fcms)
                    .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
            userNotificationService.saveNotification(findId, title, body,location,locationId);
            return true;
        } catch (IllegalArgumentException e) { //토큰이 없을시 예외처리 필요
            userNotificationService.saveNotification(findId, title, body,location,locationId); //fcm토큰이 null일 수 도 있다고 예상하고 처리
            return true;
        } catch (NullPointerException e) { //토큰이 없을시 예외처리 필요
            userNotificationService.saveNotification(findId, title, body,location,locationId); //어떤값이 null일 수 도 있다고 예상하고 처리
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
            throw new BoardHandler(ErrorStatus.FIREBASE_ERROR);
        }
    }

    /**
     * 알림 메세지를 보내지만 db에 저장되지 않게 함
     * @param findId
     * @param title
     * @param body
     * @return
     */
    public boolean sendMessageToNotSave(User findId, String title, String body,String location,Long locationId) {
        // 알림이 활성화되어 있지 않으면 알림 메세지 보내지 않도록 구현
        if (findId.getNotificationSetting() != NotificationSetting.ENABLE) {
            return false;
        }
        try {
            List<String> fcms = new ArrayList<>();
            List<FCM> fcmTokens = fcmRepository.findByUserId(findId);
            for (FCM fcmToken : fcmTokens) {
                fcms.add(fcmToken.getFcmToken());
            }
            MulticastMessage message = MulticastMessage.builder()

                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(
                            AndroidConfig.builder()
                                    .setNotification(
                                            AndroidNotification.builder()
                                                    .setTitle(title)
                                                    .setBody(body)
                                                    .setClickAction("push_click")
                                                    .build()
                                    )
                                    .putData("location",location)
                                    .putData("locationId",locationId.toString())
                                    .build()
                    )
                    .setApnsConfig(
                            ApnsConfig.builder()
                                    .setAps(Aps.builder()
                                            .setCategory("push_click")
                                            .build()
                                    )

                                    .putCustomData("location",location)
                                    .putCustomData("locationId",locationId.toString())
                                    .build()

                    )
                    .addAllTokens(fcms)
                    .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
            return true;
        }
        catch (IllegalArgumentException e) {
//            userNotificationService.saveNotification(findId, title, body,location,locationId); //fcm토큰이 null일 수 도 있다고 예상하고 처리
            return true;
        } catch (NullPointerException e) {
//            userNotificationService.saveNotification(findId, title, body,location,locationId); //어떤값이 null일 수 도 있다고 예상하고 처리
            return true;
        }
        catch (Exception e) {
            throw new BoardHandler(ErrorStatus.FIREBASE_ERROR);
        }

    }

    public void sendAllMessage(List<User> findId, String title, String body,String location,Long locationId) {

            List<String> fcms = new ArrayList<>();
            for (User user : findId) {
                if(user.getNotificationSetting() == NotificationSetting.ENABLE) {
                    List<FCM> fcmTokens = fcmRepository.findByUserId(user);
                    for (FCM fcmToken:fcmTokens) {
                        fcms.add(fcmToken.getFcmToken());
                    }
                    userNotificationService.saveNotification(user, title, body,location,locationId);
                }
            }
        for (String fcm:fcms) {
            System.out.println("Ddd"+fcm);
        }
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(
                            AndroidConfig.builder()
                                    .setNotification(
                                            AndroidNotification.builder()
                                                    .setTitle(title)
                                                    .setBody(body)
                                                    .setClickAction("push_click")
                                                    .build()
                                    )
                                    .putData("location",location)
                                    .putData("locationId",locationId.toString())
                                    .build()
                    )
                    .setApnsConfig(
                            ApnsConfig.builder()
                                    .setAps(Aps.builder()
                                            .setCategory("push_click")
                                            .build()
                                    )

                                    .putCustomData("location",location)
                                    .putCustomData("locationId",locationId.toString())
                                    .build()

                    )
                    .addAllTokens(fcms)
                    .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
        }catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        catch (Exception e) {
            throw new BoardHandler(ErrorStatus.FIREBASE_ERROR);
        }

    }

    @Transactional
    public String saveFCMToken(FCMTokenSaveDto fcmEntity, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        fcmRepository.deleteByFcmToken(fcmEntity.getFcmToken());
        FCM saveEntity = FCMTokenSaveDto.toEntity(fcmEntity, user);
        fcmRepository.save(saveEntity);
        return user.getNickname() + "님의 토큰이 저장되었습니다";
    }

    @Transactional
    public void deleteFCMToken(String fcmToken) {
            fcmRepository.deleteByFcmToken(fcmToken);
    }

}

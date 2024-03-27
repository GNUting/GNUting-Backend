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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {
    private final UserRepository userRepository;
    private final FCMRepository fcmRepository;
    private final ObjectMapper objectMapper;
    private final UserNotificationService userNotificationService;
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/" + "1036172493674/messages:send";


    public void sendMessageTo(User findId, String title, String body) {
        try {
            //board에 신청했다고 알림보낼때
            FCM fcmToken = fcmRepository.findByUserId(findId);
            String message = makeMessage(fcmToken.getFcmToken(), title, body);
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message,
                    MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(request).execute();

            System.out.println(response.body().string());
            System.out.println("전송완료");
            userNotificationService.saveNotification(findId, body);
        } catch (JsonProcessingException e) {
            throw new BoardHandler(ErrorStatus.JSON_FILE_ROAD_FAIL);
        } catch (IOException e) {
            throw new BoardHandler(ErrorStatus.INPUT_ERROR);
//        } catch (NullPointerException e) {
//            throw new BoardHandler(ErrorStatus.NOT_FOUND_FIREBASE_TOKEN);
        } catch (Exception e) {
            throw new BoardHandler(ErrorStatus.FIREBASE_ERROR);
        }

    }

    public void sendAllMessage(List<User> findId, String title, String body) {
        try {
            List<String> fcms = new ArrayList<>();
            for (User user : findId) {
                FCM fcmToken = fcmRepository.findByUserId(user);
                fcms.add(fcmToken.getFcmToken());
            }
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .addAllTokens(fcms)
                    .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
            for (User user : findId) {
                userNotificationService.saveNotification(user, body);
            }

        }catch (Exception e){
            System.out.println(e+"@@@@@@@@@@@@@에러떳다 씨빨@@@@@@@@@@@@");
        }

    }

    private String makeMessage(String targetToken, String title, String body)
            throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "gnuting-firebase-adminsdk-tpoa0-7b6979293e.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    public String saveFCMToken(FCMTokenSaveDto fcmEntity, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        FCM overlapCheck = fcmRepository.findByUserId(user);
        if (overlapCheck != null) {
            fcmRepository.delete(overlapCheck);
           //throw new BoardHandler(ErrorStatus.OVERLAP_USER_TOKEN);
        }
        FCM saveEntity = FCMTokenSaveDto.toEntity(fcmEntity, user);
        fcmRepository.save(saveEntity);
        return user.getNickname() + "님의 토큰이 저장되었습니다";
    }

    public void deleteFCMToken(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        fcmRepository.delete(user.getFcms());
    }
}

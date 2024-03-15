package gang.GNUtingBackend.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.JsonParseException;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.notification.dto.FCMTokenSaveDto;
import gang.GNUtingBackend.notification.dto.FcmMessage;
import gang.GNUtingBackend.notification.entity.FCM;
import gang.GNUtingBackend.notification.repository.FCMRepository;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {
    private final UserRepository userRepository;
    private final FCMRepository fcmRepository;
    private final ObjectMapper objectMapper;

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/"+"1036172493674/messages:send";
    public void sendMessageTo(User findId, String title, String body) throws IOException {

        //board에 신청했다고 알림보낼때

        System.out.println(findId.getId());
        FCM fcmToken=fcmRepository.findByUserId(findId);


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
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonParseException, JsonProcessingException {
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
        FCM saveEntity=FCMTokenSaveDto.toEntity(fcmEntity,user);
        fcmRepository.save(saveEntity);
        return user.getNickname()+"님의 토큰이 저장되었습니다";
    }
}

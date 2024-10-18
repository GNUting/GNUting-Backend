package gang.GNUtingBackend.mail.service;

import gang.GNUtingBackend.exception.handler.MailHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.mail.entity.EventMailEntity;
import gang.GNUtingBackend.mail.repository.EventMailRepository;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.repository.UserRepository;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private static final String senderEmail = "gnuting@gmail.com";
    private final RedisTemplate<String, String> redisTemplate;
    private static final long EXPIRE_SECONDS = 180;

    private int createNumber() {
        return (int) (Math.random() * (900000)) + 100000;
    }

    private final EventMailRepository eventMailRepository;

    /**
     * 해당 email로 보낼 인증 번호를 포함한 인증 메일을 생성한다.
     * @param email
     * @return
     */
    public MimeMessage createMail(String email) {
        int number = createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[GNUting] 이메일 인증번호 입니다.");
            String body = "";
            body += "<body>";
            body += "<p style=\"font-size:10pt;font-family:sans-serif;padding:0 0 0 10pt\"><br><br></p>";
            body += "<div style=\"width:440px; margin:30px auto; padding:40px 0px 60px; background-color:#fff;";
            body += "border:1px solid #ddd; text-align:center; font-size:16px; font-family:malgun gothic;\">";
            body += "<h3 style=\"font-weight:bold; font-size:20px; margin:28px auto;\">[GNUting] 이메일 본인 인증</h3>";
            body += "<div style=\"width:200px; margin:28px auto; padding:8px 0px 9px; background-color:#f4f4f4; border-radius:3px; \">";
            body += "<span style=\"display:inline-block; vertical-align:middle; font-size:13px; color:#666;\">인증 번호</span>";
            body += "<span style=\"display:inline-block; margin-left:16px;";
            body += "vertical-align:middle; font-size:21px; font-weight:bold; color:#4d5642;\">" + number + "</span>";
            body += "</div>";
            body += "<p style=\"text-align:center; font-size:13px; color:#000; line-height:1.6; margin-top:40px; margin-bottom:0px;\">";
            body += "안내된 인증번호를 입력란에 입력해 주세요.<br> GNUting을 이용해 주셔서 감사합니다.<br>";
            body += "</p>";
            body += "</div>";
            body += "</body>";

            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    /**
     * 해당 이메일로 인증 메일을 전송한다.
     * @param email
     * @return
     */
    public int sendMail(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new UserHandler(ErrorStatus.USER_ALREADY_EXIST);
                });

        if (!isValidAddress(email)) {
            throw new MailHandler(ErrorStatus.INVALID_MAIL_ADDRESS);
        }
        MimeMessage message = createMail(email);
        int number = extractNumber(message);
        javaMailSender.send(message);
        redisTemplate.opsForValue().set(email, String.valueOf(number) , EXPIRE_SECONDS, TimeUnit.SECONDS);
        return number;
    }

    /**
     * 비밀번호를 찾는 인증메일을 보낼 경우, 해당 이메일로 인증 메일을 전송한다.
     * @param email
     * @return
     */
    public int findPasswordSendMail(String email) {
        if (!isValidAddress(email)) {
            throw new MailHandler(ErrorStatus.INVALID_MAIL_ADDRESS);
        }
        MimeMessage message = createMail(email);
        int number = extractNumber(message);
        javaMailSender.send(message);
        redisTemplate.opsForValue().set(email, String.valueOf(number) , EXPIRE_SECONDS, TimeUnit.SECONDS);
        return number;
    }

    /**
     * message에서 인증번호를 추출
     * @param message
     * @return
     */
    private int extractNumber(MimeMessage message) {
        try {
            String content = (String) message.getContent();
            Pattern pattern = Pattern.compile("(\\d{6})"); // 6자리 숫자를 찾는 정규 표현식
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isValidAddress(String email) {
        return email.endsWith("@gnu.ac.kr");
    }

    /**
     * 이메일 인증 번호를 검증한다.
     * @param email
     * @param number
     * @return
     */
    public boolean verifyNumber(String email, String number) {

        String storedNumber = redisTemplate.opsForValue().get(email);



        if (storedNumber != null && storedNumber.equals(number)) {
            redisTemplate.delete(email);
            return true;
        } else {
            throw new MailHandler(ErrorStatus.INVALID_VERIFY_NUMBER);
        }
    }
}

package gang.GNUtingBackend.mail.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "gnuting@gnuting.com";
    private static int number;

    public static void createNumber() {
        number = (int)(Math.random() * (90000)) + 100000;
    }

    public MimeMessage CreateMail(String email){
        createNumber();
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

    public int sendMail(String email){

        MimeMessage message = CreateMail(email);
        javaMailSender.send(message);
        return number;
    }

    public boolean isValidAddress(String email) {
        return email.endsWith("@gnu.ac.kr");
    }
}

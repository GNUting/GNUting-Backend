package gang.GNUtingBackend.notification.firebaseConfig;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FCMConfig {
    @PostConstruct
    public void init() {

        try {
            String firebaseConfigPath = "gnuting-firebase-adminsdk-tpoa0-7b6979293e.json";

            InputStream serviceAccount =
                    new ClassPathResource(firebaseConfigPath).getInputStream();
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



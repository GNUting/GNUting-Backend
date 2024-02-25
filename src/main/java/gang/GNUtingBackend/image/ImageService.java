package gang.GNUtingBackend.image;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${app.firebase-bucket}")
    private String fireBaseBucket;

    // 프로필 이미지 등록
    public String uploadProfileImage(MultipartFile file, String email) throws IOException {
        String fileName = generateProfileFileName(file.getOriginalFilename(), email);
        Bucket bucket = StorageClient.getInstance().bucket(fireBaseBucket);
        Blob blob = bucket.create(fileName, file.getInputStream(), file.getContentType());
        String mediaLink = blob.getMediaLink();
        return mediaLink;
    }

    // (프로필) 파일 이름 생성
    public String generateProfileFileName(String originalFilename, String email) {
        return "profile/" + email + "/" + originalFilename;
    }
}

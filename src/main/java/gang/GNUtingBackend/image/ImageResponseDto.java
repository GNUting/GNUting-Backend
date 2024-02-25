package gang.GNUtingBackend.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {

    String name;
    String contentType;
    String mediaLink;
}

package gang.GNUtingBackend.meeting.service;

import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final UserRepository userRepository;
    public boolean userMeetingInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if(user.getDrink()==null||user.getHobby()==null||user.getMbti()==null||user.getSmoke()==null){
            return false;
        }
        return true;
    }
}

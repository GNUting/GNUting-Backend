package gang.GNUtingBackend.meeting.service;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.MeetingHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.meeting.entity.Meeting;
import gang.GNUtingBackend.meeting.entity.MeetingApplyRemaining;
import gang.GNUtingBackend.meeting.repository.MeetingApplyRemainingRepository;
import gang.GNUtingBackend.meeting.repository.MeetingRepository;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingApplyRemainingRepository meetingApplyRemainingRepository;
    public boolean userMeetingInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if(user.getDrink()==null||user.getHobby()==null||user.getMbti()==null||user.getSmoke()==null){
            return false;
        }
        return true;
    }

    public String saveMeeting(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        if(meetingRepository.findByUserAndStatusOpen(user)!=null){
            throw new MeetingHandler(ErrorStatus.ALREADY_MEETING_SAVE);
        }

        Meeting meeting=Meeting.builder()
                .birthday(String.valueOf(user.getBirthDate().getYear()))
                .userId(user)
                .mbti(user.getMbti())
                .smoke(user.getSmoke())
                .hobby(user.getHobby())
                .drink(user.getDrink())
                .gender(user.getGender())
                .user_self_introduction(user.getUserSelfIntroduction())
                .status(Status.OPEN)
                .build();
        MeetingApplyRemaining meetingApplyRemaining=MeetingApplyRemaining.builder()
                .userId(user)
                .remaining(3)
                .build();
        meetingRepository.save(meeting);
        meetingApplyRemainingRepository.save(meetingApplyRemaining);

        return "1:1 매칭에 등록이 완료되었습니다.";
    }
}

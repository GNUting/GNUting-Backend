//package gang.GNUtingBackend.meeting.service;
//
//import gang.GNUtingBackend.board.entity.Board;
//import gang.GNUtingBackend.board.entity.enums.ApplyShowStatus;
//import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
//import gang.GNUtingBackend.board.entity.enums.Status;
//import gang.GNUtingBackend.exception.handler.BoardHandler;
//import gang.GNUtingBackend.exception.handler.MeetingHandler;
//import gang.GNUtingBackend.exception.handler.UserHandler;
//import gang.GNUtingBackend.meeting.dto.MeetingResponseDto;
//import gang.GNUtingBackend.meeting.entity.Meeting;
//import gang.GNUtingBackend.meeting.entity.MeetingApplyLeader;
//import gang.GNUtingBackend.meeting.entity.MeetingApplyRemaining;
//import gang.GNUtingBackend.meeting.repository.MeetingApplyLeaderRepository;
//import gang.GNUtingBackend.meeting.repository.MeetingApplyRemainingRepository;
//import gang.GNUtingBackend.meeting.repository.MeetingRepository;
//import gang.GNUtingBackend.response.code.status.ErrorStatus;
//import gang.GNUtingBackend.user.domain.User;
//import gang.GNUtingBackend.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class MeetingService {
//
//    private final UserRepository userRepository;
//    private final MeetingRepository meetingRepository;
//    private final MeetingApplyRemainingRepository meetingApplyRemainingRepository;
//    private final MeetingApplyLeaderRepository meetingApplyLeaderRepository;
//    public boolean userMeetingInfo(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
//        MeetingApplyRemaining meetingApplyRemaining=meetingApplyRemainingRepository.findByUserId(user);
//
//        if(meetingApplyRemaining==null){
//            return false; //등록하지 않았을때
//        }
//        return true; //등록했을때
//    }
//
//    public String saveMeeting(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
//        if(meetingRepository.findByUserAndStatusOpen(user)!=null){
//            throw new MeetingHandler(ErrorStatus.ALREADY_MEETING_SAVE);
//        }
//
//        Meeting meeting=Meeting.builder()
//                .userId(user)
//                .gender(user.getGender())
//                .status(Status.OPEN)
//                .build();
//        MeetingApplyRemaining meetingApplyRemaining=MeetingApplyRemaining.builder()
//                .userId(user)
//                .remaining(3)
//                .meetingId(meeting)
//                .build();
//        meetingRepository.save(meeting); //일주일 1번 삭제
//        meetingApplyRemainingRepository.save(meetingApplyRemaining); //매일 3회로 초기화 및 일주일 1번 삭제
//
//        return "1:1 매칭에 등록이 완료되었습니다.";
//    }
//
//    public List<MeetingResponseDto> RandomList(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
//        List<Meeting> meetingList=meetingRepository.findRandomAndGenderList(user.getGender(), PageRequest.of(0,20));
//        List<MeetingResponseDto> meetingResponseDto=meetingList.stream()
//                .map(MeetingResponseDto::toDto)
//                .collect(Collectors.toList());
//        return meetingResponseDto;
//    }
//
//    public String deleteOneToOne(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
//        Meeting meeting=meetingRepository.findByUserAndStatusOpen(user);
//        if(meeting==null){
//            throw new MeetingHandler(ErrorStatus.NOT_FOUNT_MEETING);
//        }
//        meetingRepository.delete(meeting);
//        return "1:1매칭 등록을 취소했습니다.";
//    }
//
//
//    public String applyMeeting(String email, Long id) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
//        Meeting meeting=meetingRepository.findById(id)
//                .orElseThrow(()->new MeetingHandler(ErrorStatus.NOT_FOUNT_MEETING));
//
//        MeetingApplyRemaining meetingApplyRemaining=meetingApplyRemainingRepository.findByUserId(user);
//
//        if(meetingApplyRemaining==null){
//            throw new MeetingHandler(ErrorStatus.NOT_POST_MEETING);
//        }
//        if(meetingApplyRemaining.getRemaining()<=0){
//            throw new MeetingHandler(ErrorStatus.NOT_HAVE_REMAINING);
//        }
//
//        if (meeting.getStatus()==Status.CLOSE){
//            throw new MeetingHandler(ErrorStatus.ALREADY_APPLY_MEETING_DONE);
//        }
//        if (meeting.getGender()==user.getGender()){
//            throw new MeetingHandler(ErrorStatus.GENDER_SAME);
//        }
//
//        List<MeetingApplyLeader> meetingApplyLeaderList=meetingApplyLeaderRepository.findByLeaderId(user);
//        for (MeetingApplyLeader checkDuplication:meetingApplyLeaderList) {
//            if(checkDuplication.getMeeting()==meeting){
//                throw new MeetingHandler(ErrorStatus.YOU_ARE_ALREADY_APPLY_MEETING);
//            }
//        }
//
//        MeetingApplyLeader meetingApplyLeader=MeetingApplyLeader.builder()
//                .applyShowStatus(ApplyShowStatus.SHOW)
//                .receiveShowStatus(ApplyShowStatus.SHOW)
//                .leaderId(user)
//                .meetingUserId(meeting.getUserId())
//                .meeting(meeting)
//                .status(ApplyStatus.대기중)
//                .build();
//        meetingApplyRemaining.minusRemaining();
//        meetingApplyRemainingRepository.save(meetingApplyRemaining);
//        meetingApplyLeaderRepository.save(meetingApplyLeader);
//
//        return "1:1 매칭을 신청했습니다.";
//    }
//}

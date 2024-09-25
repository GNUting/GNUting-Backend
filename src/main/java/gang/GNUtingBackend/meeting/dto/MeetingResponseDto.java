//package gang.GNUtingBackend.meeting.dto;
//
//import gang.GNUtingBackend.board.entity.enums.Status;
//import gang.GNUtingBackend.meeting.entity.Meeting;
//import gang.GNUtingBackend.memoThing.entity.Memo;
//import gang.GNUtingBackend.user.domain.User;
//import gang.GNUtingBackend.user.domain.enums.Gender;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Getter
//public class MeetingResponseDto {
//    private Long id;
//    private String nickname;
//    private String studentId;
//    private String department;
//    private String user_self_introduction;
//    private Gender gender;
//    private String mbti;
//    private String birthDate;
//    private String smoke;
//    private String drink;
//    private String hobby;
//    private Status status;
//
//
//    public static MeetingResponseDto toDto(Meeting meeting){
//        return MeetingResponseDto.builder()
//                .id(meeting.getId())
//                .birthDate(String.valueOf(meeting.getUserId().getBirthDate().getYear()))
//                .drink(meeting.getUserId().getDrink())
//                .hobby(meeting.getUserId().getHobby())
//                .gender(meeting.getGender())
//                .smoke(meeting.getUserId().getSmoke())
//                .department(meeting.getUserId().getDepartment())
//                .mbti(meeting.getUserId().getMbti())
//                .nickname(meeting.getUserId().getNickname())
//                .status(meeting.getStatus())
//                .studentId(meeting.getUserId().getStudentId())
//                .user_self_introduction(meeting.getUserId().getUserSelfIntroduction())
//                .build();
//    }
//
//}
//
//

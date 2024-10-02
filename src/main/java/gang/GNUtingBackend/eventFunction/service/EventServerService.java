package gang.GNUtingBackend.eventFunction.service;

import gang.GNUtingBackend.board.dto.ChatMemberDto;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.chat.dto.ChatRoomResponseDto;
import gang.GNUtingBackend.chat.service.ChatRoomService;
import gang.GNUtingBackend.eventFunction.dto.EventApplyResponseDto;
import gang.GNUtingBackend.eventFunction.dto.EventParticipateRequestDto;
import gang.GNUtingBackend.eventFunction.entity.EventApply;
import gang.GNUtingBackend.eventFunction.entity.EventServerState;
import gang.GNUtingBackend.eventFunction.repository.EventApplyRepository;
import gang.GNUtingBackend.eventFunction.repository.EventServerStateRepository;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.memoThing.dto.MemoApplyResponseDto;
import gang.GNUtingBackend.notification.service.FCMService;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServerService {
    private final EventServerStateRepository eventServerStateRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final FCMService fcmService;
    private final EventApplyRepository eventApplyRepository;
    public Status checkState() {
        EventServerState eventServerState=eventServerStateRepository.findById(1L);
        if(eventServerState.getStatus()==Status.OPEN){
            return Status.OPEN;
        }
        return Status.CLOSE;
    }


    public EventApplyResponseDto makeChat(String email, EventParticipateRequestDto eventParticipateRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        User findUser=userRepository.findByNickname(eventParticipateRequestDto.getNickname())
                .orElseThrow(()-> new UserHandler(ErrorStatus.NOT_FOUND_NICKNAME));
        EventApply eventApply=eventApplyRepository.findByUserId(user);

        EventServerState eventServerState=eventServerStateRepository.findById(1L);
        if(eventServerState.getStatus()==Status.CLOSE){
            throw new UserHandler(ErrorStatus.SERVER_NOT_OPEN);
        }

        if(eventApply!=null){
            throw new UserHandler(ErrorStatus.ALREADY_EVENT_APPLY);
        }
        if(user.getGender()==findUser.getGender()){
            throw new UserHandler(ErrorStatus.GENDER_SAME);
        }
        if(user==findUser){
            throw new UserHandler(ErrorStatus._BAD_REQUEST);
        }
        List<User> userDum=new ArrayList<>();
        List<User> findUserDum=new ArrayList<>();
        List<User> notificationUser = new ArrayList<>();
        notificationUser.add(user);
        notificationUser.add(findUser);
        userDum.add(user);
        findUserDum.add(findUser);
        ChatMemberDto chatMemberDto = ChatMemberDto.toDto("1:1", user.getDepartment(), findUser.getDepartment(), userDum,
                findUserDum);

        ChatRoomResponseDto chatRoomResponseDto=chatRoomService.createChatRoom(chatMemberDto);
        fcmService.sendAllMessage(notificationUser, "메모팅이 성사되었습니다", chatMemberDto.getApplyUserDepartment() + "와 " + chatMemberDto.getParticipantUserDepartment() + "의 메모팅이 성사되어 채팅방이 만들어졌습니다.","chat",chatRoomResponseDto.getId());
        eventApply= EventApply.builder()
                .userId(user)
                .build();
        eventApplyRepository.save(eventApply);
        MemoApplyResponseDto.builder()
                .chatId(chatRoomResponseDto.getId())
                .build();
        return EventApplyResponseDto.builder()
                .chatId(chatRoomResponseDto.getId())
                .build();


    }
}

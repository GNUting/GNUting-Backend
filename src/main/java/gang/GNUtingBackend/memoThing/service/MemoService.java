package gang.GNUtingBackend.memoThing.service;

import gang.GNUtingBackend.board.dto.ChatMemberDto;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.chat.dto.ChatRoomResponseDto;
import gang.GNUtingBackend.chat.service.ChatRoomService;
import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.MemoHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.memoThing.dto.MemoRequestDto;
import gang.GNUtingBackend.memoThing.dto.MemoResponseDto;
import gang.GNUtingBackend.memoThing.entity.Memo;
import gang.GNUtingBackend.memoThing.entity.MemoApplyRemaining;
import gang.GNUtingBackend.memoThing.repository.MemoApplyRemainingRepository;
import gang.GNUtingBackend.memoThing.repository.MemoRepository;
import gang.GNUtingBackend.notification.service.FCMService;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final UserRepository userRepository;
    private final MemoRepository memoRepository;
    private final MemoApplyRemainingRepository memoApplyRemainingRepository;
    private final ChatRoomService chatRoomService;
    private final FCMService fcmService;
    @Transactional
    public String saveMemo(String email, MemoRequestDto memoRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        if(memoRepository.findByUserIdAndStatus(user)!=null){ //open 인게 없을떄
            throw new MemoHandler(ErrorStatus.MEMO_ALREADY_SAVE);
        }
        Memo memo=MemoRequestDto.toEntity(user,memoRequestDto);
        memoRepository.save(memo);
        MemoApplyRemaining memoApplyRemaining=MemoApplyRemaining.builder()
                .userId(user)
                .remaining(1)
                .build();
        memoApplyRemainingRepository.save(memoApplyRemaining);

        return "메모가 저장되었습니다.";
    }


    @Transactional(readOnly = true)
    public List<MemoResponseDto> showMemo(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        int page = pageable.getPageNumber() - 1;
        int pageLimit = pageable.getPageSize();
        Page<Memo> memos =memoRepository.findByMemo(user.getGender(), PageRequest.of(page, pageLimit));
        if (!memos.hasContent()) {
            throw new BoardHandler(ErrorStatus.PAGE_NOT_FOUND);
        }
        return memos.stream()
                .map(MemoResponseDto::toDto)
                .collect(Collectors.toList());
    }




    @Scheduled(cron="0 05 19 * * *")
    public void autoClose (){
        memoRepository.updateMemoStatusToClose();
        memoApplyRemainingRepository.deleteAll();
    }


    public String applyMemo(Long id, String email) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Memo memo=memoRepository.findById(id)
                .orElseThrow(()->new MemoHandler(ErrorStatus.MEMO_NOT_FOUND));
        User memoUser=memo.getUserId();
        if(memo.getStatus()== Status.CLOSE){
            throw new MemoHandler(ErrorStatus.MEMO_ALREADY_APPLY);
        }

        List<User> userDum=new ArrayList<>();
        List<User> memoUserDum=new ArrayList<>();
        List<User> notificationUser = new ArrayList<>();
        notificationUser.add(user);
        notificationUser.add(memoUser);
        userDum.add(user);
        memoUserDum.add(memoUser);
        ChatMemberDto chatMemberDto = ChatMemberDto.toDto(memo.getContent(), user.getDepartment(), memo.getUserId().getDepartment(), userDum,
                memoUserDum);

        ChatRoomResponseDto chatRoomResponseDto=chatRoomService.createChatRoom(chatMemberDto);
        fcmService.sendAllMessage(notificationUser, "메모팅이 성사되었습니다", chatMemberDto.getApplyUserDepartment() + "와 " + chatMemberDto.getParticipantUserDepartment() + "의 메모팅이 성사되어 채팅방이 만들어졌습니다.","chat",chatRoomResponseDto.getId());
        memo.closeState();
        memoRepository.save(memo);
        return "채팅신청이 완료되었습니다.";

    }
}

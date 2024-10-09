package gang.GNUtingBackend.memoThing.service;

import gang.GNUtingBackend.board.dto.ChatMemberDto;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.chat.dto.ChatRoomResponseDto;
import gang.GNUtingBackend.chat.service.ChatRoomService;
import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.MemoHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.memoThing.dto.MemoApplyResponseDto;
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
        if(memoRepository.findByUserIdAndStatus(user)!=null){ //open 인게 없을떄 // 이거 굳이 없어도 될것같음
            throw new MemoHandler(ErrorStatus.MEMO_ALREADY_SAVE);
        }
        if(memoApplyRemainingRepository.findByUserId(user)!=null){
            throw new MemoHandler(ErrorStatus.MEMO_ALREADY_SAVE);
        }

        MemoApplyRemaining multiClick=memoApplyRemainingRepository.findByUserId(user);
        if(multiClick!=null){
            memoApplyRemainingRepository.deleteByUserId(user);
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




//    @Scheduled(cron="0 0 0 * * *") 클래스 이동
//    public void autoClose (){
//        memoRepository.updateMemoStatusToClose(); //메모 모두 close
//        memoApplyRemainingRepository.deleteAll();  //메모 신청횟수 초기화
//
//    }


    public MemoApplyResponseDto applyMemo(Long id, String email) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Memo memo=memoRepository.findById(id)
                .orElseThrow(()->new MemoHandler(ErrorStatus.MEMO_NOT_FOUND));
        MemoApplyRemaining memoApplyRemaining=memoApplyRemainingRepository.findByUserId(user);
        if(memoApplyRemaining==null){
            throw new MemoHandler(ErrorStatus.MUST_MEMO_POST);
        }
        if(memoApplyRemaining.getRemaining()<=0){
            throw new MemoHandler(ErrorStatus.ALREADY_MEMO_APPLY);
        }
        if(memo.getStatus()== Status.CLOSE){
            throw new MemoHandler(ErrorStatus.MEMO_ALREADY_APPLY);
        }
        if(memo.getGender()==user.getGender()){
            throw new MemoHandler(ErrorStatus.GENDER_SAME);
        }
        User memoUser=memo.getUserId();

        List<User> userDum=new ArrayList<>();
        List<User> memoUserDum=new ArrayList<>();
        List<User> notificationUser = new ArrayList<>();
        notificationUser.add(user);
        notificationUser.add(memoUser);
        userDum.add(user);
        memoUserDum.add(memoUser);
        ChatMemberDto chatMemberDto = ChatMemberDto.toDto("메모팅", user.getDepartment(), memo.getUserId().getDepartment(), userDum,
                memoUserDum);

        ChatRoomResponseDto chatRoomResponseDto=chatRoomService.createChatRoom(chatMemberDto);
        fcmService.sendAllMessage(notificationUser, "메모팅이 성사되었습니다", chatMemberDto.getApplyUserDepartment() + "와 " + chatMemberDto.getParticipantUserDepartment() + "의 메모팅이 성사되어 채팅방이 만들어졌습니다.","chat",chatRoomResponseDto.getId());
        memo.closeState();
        memoRepository.save(memo);
        memoApplyRemaining.minusRemaining();
        memoApplyRemainingRepository.save(memoApplyRemaining);
        MemoApplyResponseDto.builder()
                .chatId(chatRoomResponseDto.getId())
                .build();
        return MemoApplyResponseDto.builder()
                .chatId(chatRoomResponseDto.getId())
                .build();

    }

    public int getMemoRemaning(String email) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));
        MemoApplyRemaining memoApplyRemaining=memoApplyRemainingRepository.findByUserId(user);
        if(memoApplyRemaining==null){
            return 1;
        }
        return memoApplyRemaining.getRemaining();

    }
}

package gang.GNUtingBackend.scheduled;


import gang.GNUtingBackend.eventFunction.repository.EventApplyRepository;
import gang.GNUtingBackend.memoThing.repository.MemoApplyRemainingRepository;
import gang.GNUtingBackend.memoThing.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Schedules {

    private final MemoRepository memoRepository;
    private final MemoApplyRemainingRepository memoApplyRemainingRepository;
    private final EventApplyRepository eventApplyRepository;
    @Scheduled(cron="0 0 0 * * *")
    public void autoClose (){
        memoRepository.updateMemoStatusToClose(); //메모 모두 close
        memoApplyRemainingRepository.deleteAll();  //메모 신청횟수 초기화
        eventApplyRepository.deleteAll(); //이벤트 신청 초기화

    }
}

package gang.GNUtingBackend.scheduled;


import gang.GNUtingBackend.memoThing.repository.MemoApplyRemainingRepository;
import gang.GNUtingBackend.memoThing.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class Schedules {

    private final MemoRepository memoRepository;
    private final MemoApplyRemainingRepository memoApplyRemainingRepository;

    @Scheduled(cron="0 0 0 * * *")
    public void autoClose (){
        memoRepository.updateMemoStatusToClose(); //메모 모두 close
        memoApplyRemainingRepository.deleteAll();  //메모 신청횟수 초기화

    }
}

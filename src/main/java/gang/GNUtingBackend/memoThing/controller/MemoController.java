package gang.GNUtingBackend.memoThing.controller;

import gang.GNUtingBackend.board.dto.BoardRequestDto;
import gang.GNUtingBackend.memoThing.dto.MemoApplyResponseDto;
import gang.GNUtingBackend.memoThing.dto.MemoRequestDto;
import gang.GNUtingBackend.memoThing.dto.MemoResponseDto;
import gang.GNUtingBackend.memoThing.service.MemoService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemoController {
    private final TokenProvider tokenProvider;
    private final MemoService memoService;

    @PostMapping("/memo/save")
    @Operation(summary = "메모작성 API", description = "메모를 작성합니다.")
    public ResponseEntity<?> memoSave(@RequestBody MemoRequestDto memoRequestDto,
                                      @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        String memoSaved=memoService.saveMemo(email,memoRequestDto);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(memoSaved));
    }

    @GetMapping("/memo")
    @Operation(summary = "메모조회 API", description = "내 성별과 다른 메모를 조회합니다.")
    public ResponseEntity<?> memoShow(@RequestHeader("Authorization") String token, @PageableDefault(page = 1, size = 20) Pageable pageable){
        String email = tokenProvider.getUserEmail(token.substring(7));
        List<MemoResponseDto> showAllMemo=memoService.showMemo(email,pageable);
        //return값으로 메모id만 주고 그 id값을 가져와서 채팅방이랑 연결하게 하면 될듯
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(showAllMemo));
    }
    
    @PostMapping("/memo/{id}")
    @Operation(summary = "메모신청 API", description = "해당 메모에 채팅을 신청합니다.")
    public ResponseEntity<?> memoApply(@PathVariable Long id,@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        MemoApplyResponseDto posted=memoService.applyMemo(id,email);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(posted));
    }

    @GetMapping("/memo/remaining")
    @Operation(summary = "남은 메모신청 확인 API", description = "오늘 신청가능한 메모팅 횟수.")
    public ResponseEntity<?> memoRemaining(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        int remaining=memoService.getMemoRemaning(email);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(remaining));
    }
}

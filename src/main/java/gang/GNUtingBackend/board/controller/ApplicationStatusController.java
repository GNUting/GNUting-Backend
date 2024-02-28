package gang.GNUtingBackend.board.controller;

import gang.GNUtingBackend.board.dto.ApplicationStatusResponseDto;
import gang.GNUtingBackend.board.dto.BoardResponseDto;
import gang.GNUtingBackend.board.service.ApplicationStatusService;
import gang.GNUtingBackend.user.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationStatusController {

    private final ApplicationStatusService applicationStatusService;

    private final TokenProvider tokenProvider;


    //신청 받은 현황
    @GetMapping("board/applications/received")
    public ResponseEntity<List<ApplicationStatusResponseDto>> receivedState(@RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        List<ApplicationStatusResponseDto> userSearchResponseDto = applicationStatusService.receiveState(email);
        return ResponseEntity.status(HttpStatus.OK).body(userSearchResponseDto);
    }

    //내가 신청한 현황
    @GetMapping("board/applications/apply")
    public ResponseEntity<List<ApplicationStatusResponseDto>> applyState(@RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        List<ApplicationStatusResponseDto> userSearchResponseDto = applicationStatusService.applyState(email);
        return ResponseEntity.status(HttpStatus.OK).body(userSearchResponseDto);
    }

    //내가쓴글
    @GetMapping("board/myboard")
    public ResponseEntity<List<BoardResponseDto>> myBoard(@RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        List<BoardResponseDto> myBoards = applicationStatusService.myBoard(email);
        return ResponseEntity.status(HttpStatus.OK).body(myBoards);
    }



}

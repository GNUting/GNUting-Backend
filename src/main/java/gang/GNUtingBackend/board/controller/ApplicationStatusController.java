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

//    @GetMapping("board/applyStatus")
//    public ResponseEntity<List<List<UserSearchResponseDto>>> applyStatus(@RequestHeader("Authorization") String token){
//        String email=tokenProvider.getUserEmail(token.substring(7));
//        List<List<UserSearchResponseDto>> userSearchResponseDto= applicationStatusService.applyStatus(email);
//        return ResponseEntity.status(HttpStatus.OK).body(userSearchResponseDto);
//    }

    @GetMapping("board/applications/received")
    public ResponseEntity<List<ApplicationStatusResponseDto>> received(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        List<ApplicationStatusResponseDto> userSearchResponseDto= applicationStatusService.received(email);
        return ResponseEntity.status(HttpStatus.OK).body(userSearchResponseDto);
    }

    @GetMapping("board/myboard")
    public ResponseEntity<List<BoardResponseDto>> myBoard(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        List<BoardResponseDto> myBoards=applicationStatusService.myBoard(email);
        return ResponseEntity.status(HttpStatus.OK).body(myBoards);
    }

    @GetMapping("board/applications/apply")
    public ResponseEntity<List<ApplicationStatusResponseDto>> apply(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        List<ApplicationStatusResponseDto> userSearchResponseDto= applicationStatusService.apply(email);
        return ResponseEntity.status(HttpStatus.OK).body(userSearchResponseDto);
    }
}

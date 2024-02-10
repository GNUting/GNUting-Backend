package gang.GNUtingBackend.board.controller;

import gang.GNUtingBackend.board.dto.BoardRequestDto;
import gang.GNUtingBackend.board.service.BoardService;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.service.BoardService;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private final TokenProvider tokenProvider;

    @GetMapping("/board")
    public ResponseEntity<List<BoardRequestDto>> show(@RequestHeader("Authorization") String token){

        String email=tokenProvider.getUserEmail(token.substring(7));
        List<BoardRequestDto> board = boardService.show(email);
        return ResponseEntity.status(HttpStatus.OK).body(board);
    }

    @GetMapping("/board/{id}")
    public ResponseEntity<BoardRequestDto> inshow(@PathVariable Long id){
        BoardRequestDto board = boardService.inshow(id);
        return ResponseEntity.status(HttpStatus.OK).body(board);
    }

    @GetMapping("/board/user/search")
    public ResponseEntity<UserSearchResponseDto> userSearch(@RequestParam String nickname,@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        UserSearchResponseDto userSearch = boardService.userSearch(email,nickname);
        return ResponseEntity.status(HttpStatus.OK).body(userSearch);
    }

    @PatchMapping("board/{id}")  //유저수정 추가해야함..
    public ResponseEntity<String> edit(@PathVariable Long id, @RequestBody BoardRequestDto boardRequestDto, @RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        String response = boardService.edit(id, boardRequestDto,email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/board/save")
    public ResponseEntity<BoardRequestDto> save(@RequestBody BoardRequestDto boardRequestDto, @RequestHeader("Authorization") String token){

        String email=tokenProvider.getUserEmail(token.substring(7));

        BoardRequestDto saved= boardService.save(boardRequestDto,email);
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }
    @DeleteMapping("/board/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id,@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        Board deleted=boardService.delete(id,email);
        if (deleted != null) {
            return ResponseEntity.status(HttpStatus.OK).body("ID값이 " + deleted.getId() + "인 게시글 삭제에 성공하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID값이 " + id + "인 게시글 삭제에 실패하였습니다");
    }
}


// 유저아이디와 게시판아이디가 일치하는것을 보여주면 되겠다 . 상태를 넣어야겠네 .
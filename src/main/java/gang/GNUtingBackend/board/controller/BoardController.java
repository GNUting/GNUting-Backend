package gang.GNUtingBackend.board.controller;

import gang.GNUtingBackend.board.dto.BoardRequestDto;
import gang.GNUtingBackend.board.dto.BoardResponseDto;
import gang.GNUtingBackend.board.dto.BoardShowAllResponseDto;
import gang.GNUtingBackend.board.service.BoardService;
import gang.GNUtingBackend.board.entity.Board;

import gang.GNUtingBackend.user.dto.UserSearchRequestDto;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private final TokenProvider tokenProvider;

    //모든게시판 조회(사용자와 반대되는 성별의 게시글만 조회)
    @GetMapping("/board")
    public ResponseEntity<List<BoardShowAllResponseDto>> show(@PageableDefault(page = 1) Pageable pageable, @RequestHeader("Authorization") String token) {

        String email = tokenProvider.getUserEmail(token.substring(7));
        List<BoardShowAllResponseDto> board = boardService.show(email, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(board);
    }

    //특정 글 조회
    @GetMapping("/board/{id}")
    public ResponseEntity<BoardResponseDto> inshow(@PathVariable Long id) {
        BoardResponseDto board = boardService.inshow(id);
        return ResponseEntity.status(HttpStatus.OK).body(board);
    }

    //유저 검색
    @GetMapping("/board/user/search")
    public ResponseEntity<UserSearchResponseDto> userSearch(@RequestParam String nickname, @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        UserSearchResponseDto userSearch = boardService.userSearch(email, nickname);
        return ResponseEntity.status(HttpStatus.OK).body(userSearch);
    }

    //게시글 수정
    @PatchMapping("board/{id}")
    public ResponseEntity<String> edit(@PathVariable Long id, @RequestBody BoardRequestDto boardRequestDto, @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        String response = boardService.edit(id, boardRequestDto, email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //게시글 저장
    @PostMapping("/board/save")
    public ResponseEntity<String> save(@RequestBody BoardRequestDto boardRequestDto, @RequestHeader("Authorization") String token) {

        String email = tokenProvider.getUserEmail(token.substring(7));

        String saved = boardService.save(boardRequestDto, email);
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }

    //게시글 삭제
    @DeleteMapping("/board/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        String deleted = boardService.delete(id, email);
        return ResponseEntity.status(HttpStatus.OK).body("ID값이 " + deleted + "인 게시글 삭제에 성공하였습니다.");
    }


    //게시글에 과팅신청
    @PostMapping("/board/apply/{id}")
    public ResponseEntity<String> apply(@PathVariable Long id, @RequestBody List<UserSearchRequestDto> userSearchRequestDto, @RequestHeader("Authorization") String token) {

        String email = tokenProvider.getUserEmail(token.substring(7));

        String saved = boardService.apply(id, userSearchRequestDto, email);
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }
}


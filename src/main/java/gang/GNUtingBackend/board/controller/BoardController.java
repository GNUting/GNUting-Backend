package gang.GNUtingBackend.board.controller;

import gang.GNUtingBackend.board.dto.BoardRequestDto;
import gang.GNUtingBackend.board.dto.BoardResponseDto;
import gang.GNUtingBackend.board.dto.BoardSearchResultDto;
import gang.GNUtingBackend.board.dto.BoardShowAllResponseDto;
import gang.GNUtingBackend.board.service.BoardService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class BoardController {

    private final BoardService boardService;
    private final TokenProvider tokenProvider;

    //모든게시판 조회(사용자와 반대되는 성별의 게시글만 조회)
    @GetMapping("/board")
    @Operation(summary = "모든글 보기 API", description = "모든글을 볼 수 있습니다 (페이지네이션),(자신과 다른 성별이 올린 게시글만 볼 수 있음)")
    public ResponseEntity<?> show(@PageableDefault(page = 1, size = 20) Pageable pageable,
                                  @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        List<BoardShowAllResponseDto> board = boardService.show(email, pageable);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(board));
    }

    //특정 글 조회
    @GetMapping("/board/{id}")
    @Operation(summary = "특정 글 보기 API", description = "주소에 id값을 적으면 특정글을 볼 수 있습니다.")
    public ResponseEntity<?> inshow(@PathVariable Long id) {
        BoardResponseDto board = boardService.inshow(id);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(board));
    }

    @GetMapping("/board/user/myinfo")
    @Operation(summary = "내정보 보기 API", description = "게시글을 작성 시 작성자의 정보를 표시 합니다.")
    public ResponseEntity<?> myinfo(@RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        UserSearchResponseDto myInfo = boardService.myInfo(email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(myInfo));
    }

    //유저 검색
    @GetMapping("/board/user/search")
    @Operation(summary = "유저찾기 API", description = "게시글 작성 시,게시글에 과팅 신청 시, 과팅에 참여할 유저를 찾기 합니다. (자신의 성별과 동일한 계정만 찾을 수 있음)")
    public ResponseEntity<?> userSearch(@RequestParam String nickname, @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        UserSearchResponseDto userSearch = boardService.userSearch(email, nickname);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(userSearch));
    }

    //게시글 수정
    @PatchMapping("board/{id}")
    @Operation(summary = "게시글 수정 API", description = "작성한 게시글 수정합니다. ,게시글에 글제목, 글내용,참여유저 수정가능 합니다.")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody BoardRequestDto boardRequestDto,
                                  @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        String response = boardService.edit(id, boardRequestDto, email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(response));
    }

    //게시글 저장
    @PostMapping("/board/save")
    @Operation(summary = "게시글작성 API", description = "게시글을 작성합니다., 작성시 유저를 찾아서 참여유저 추가 해야합니다")
    public ResponseEntity<?> save(@RequestBody BoardRequestDto boardRequestDto,
                                  @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        String saved = boardService.save(boardRequestDto, email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(saved));
    }

    //게시글 삭제
    @DeleteMapping("/board/{id}")
    @Operation(summary = "게시글삭제 API", description = "게시글을 삭제합니다.")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        String deleted = boardService.delete(id, email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess("Id값이 " + deleted + "인 게시글 삭제에 성공하였습니다."));
    }


    //게시글에 과팅신청
    @PostMapping("/board/apply/{id}")
    @Operation(summary = "과팅신청 API", description = "게시글을 대상으로 과팅신청 합니다., 신청시 자신의 정보와 신청할 유저들의 정보를 검색해서 추가해야합니다.")
    public ResponseEntity<?> apply(@PathVariable Long id, @RequestBody List<UserSearchResponseDto> userSearchRequestDto,
                                   @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        String saved = boardService.apply(id, userSearchRequestDto, email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(saved));
    }

    @GetMapping("/board/search")
    @Operation(summary = "게시판 글 검색 API", description = "게시글 제목, 학과를 통해 게시판 글을 검색합니다. 사용자의 성별과 반대인글만 검색됩니다.")
    public ResponseEntity<?> searchBoards(
            @RequestParam("keyword") @Parameter(description = "게시글 제목, 학과") String keyword,
            @RequestHeader("Authorization") String token,
            Pageable pageable
    ) {
        String email = tokenProvider.getUserEmail(token.substring(7));

        Page<BoardSearchResultDto> boardSearchResultDtos = boardService.searchBoards(keyword, email, pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(boardSearchResultDtos));

    }


}


package gang.GNUtingBackend.response.code.status;

import gang.GNUtingBackend.response.code.BaseErrorCode;
import gang.GNUtingBackend.response.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // User 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "사용자가 없습니다."),
    NICKNAME_INPUT_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4002", "닉네임을 입력해주세요"),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "USER4003", "이미 사용중인 닉네임입니다."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER4004", "이미 가입된 사용자입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER4005", "비밀번호가 일치하지 않습니다."),
    USER_GENDER_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER4001", "사용자가 없습니다. (성별이 일치하지않거나 닉네임이 잘못됐습니다.)"),
    // Board 관련 에러
    BOARD_NOT_FOUND(HttpStatus.BAD_REQUEST,"BOARD5001", "게시글이 없습니다"),
    USER_NOT_FOUND_IN_BOARD(HttpStatus.BAD_REQUEST,"BOARD5002", "권한이없습니다.(게시글을 작성한 유저가 아닙니다)"),
    USER_NOT_AUTHORITY(HttpStatus.BAD_REQUEST,"BOARD5003","권한이 없습니다."),
    INCORRECT_NUMBER_OF_PEOPLE(HttpStatus.BAD_REQUEST,"BOARD4002", "인원수가 맞지 않습니다."),
    PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST,"BOARD4003","현재 페이지 내 표시할 게시글이 없습니다. "),
    WRITER_NOT_IN_BOARDPARTICIPANT(HttpStatus.BAD_REQUEST,"BOARD4004","작성자가 포함되어 있지 않습니다."),
    NOT_MATCH_GENDER(HttpStatus.BAD_REQUEST,"BOARD4005","신청자의 성별이 게시물의 성별과 동일합니다"),
    LEADER_NOT_IN_APPLYUSER(HttpStatus.BAD_REQUEST,"BOARD4006","신청자(리더)가 포함되어있지 않습니다."),

    //Apply 관련 에러
    ALREADY_IN_USER(HttpStatus.BAD_REQUEST,"APLLY6001", "유저가 이미 참여해 있습니다."),

    // 메일 관련 에러
    INVALID_MAIL_ADDRESS(HttpStatus.BAD_REQUEST, "MAIL4001", "경상국립대학교 이메일을 입력해주세요."),

    // slack 관련 에러
    CANNOT_SEND_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR, "SLACK5001", "slack으로 메세지를 보내지 못하였습니다."),

    // token 관련 에러
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001", "유효하지 않은 Access Token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001", "Refresh Token이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001", "유효하지 않은 Refresh Token입니다."),
    NOT_EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4000", "만료되지 않은 Access 토큰입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }

}

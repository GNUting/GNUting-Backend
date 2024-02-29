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
    INCORRECT_NUMBER_OF_PEOPLE(HttpStatus.BAD_REQUEST,"BOARD5002", "인원수가 맞지 않습니다."),

    //Apply 관련 에러
    ALREADY_IN_USER(HttpStatus.BAD_REQUEST,"APLLY6001", "유저가 이미 참여해 있습니다."),

    // 메일 관련 에러
    INVALID_MAIL_ADDRESS(HttpStatus.BAD_REQUEST, "MAIL4001", "경상국립대학교 이메일을 입력해주세요.");


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

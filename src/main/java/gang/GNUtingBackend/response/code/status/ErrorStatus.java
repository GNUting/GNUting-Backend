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
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4000-1", "사용자가 없습니다."),
    NICKNAME_INPUT_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4000-2", "닉네임을 입력해주세요"),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "USER4000-3", "이미 사용중인 닉네임입니다."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER4000-4", "이미 가입된 사용자입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER4000-5", "비밀번호가 일치하지 않습니다."),
    USER_GENDER_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER4000-6", "사용자가 없습니다. (성별이 일치하지않거나 닉네임이 잘못됐습니다.)"),
    INVALID_STUDENT_ID(HttpStatus.BAD_REQUEST, "USER4000-7", "학번은 숫자 2자리로 입력해주세요."),
    NICKNAME_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "USER4000-8", "닉네임은 최대 10자까지 가능합니다."),
    USER_SELF_INTRODUCTION_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "USER4000-9", "한 줄 소개는 최대 30자까지 가능합니다."),
    PASSWORD_IS_NOT_VALID(HttpStatus.BAD_REQUEST,"USER4006","비밀번호를 특수문자,영문자 1개이상 포함 8~15자 이내로 작성하세요"),

    // Board 관련 에러
    BOARD_NOT_FOUND(HttpStatus.BAD_REQUEST,"BOARD5001", "게시글이 없습니다"),
    USER_NOT_FOUND_IN_BOARD(HttpStatus.BAD_REQUEST,"BOARD5002", "권한이없습니다.(게시글을 작성한 유저가 아닙니다)"),
    USER_NOT_AUTHORITY(HttpStatus.BAD_REQUEST,"BOARD5003","권한이 없습니다."),
    INCORRECT_NUMBER_OF_PEOPLE(HttpStatus.BAD_REQUEST,"BOARD4002", "인원수가 맞지 않습니다."),
    PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST,"BOARD4003","현재 페이지 내 표시할 게시글이 없습니다. "),
    WRITER_NOT_IN_BOARD_PARTICIPANT(HttpStatus.BAD_REQUEST,"BOARD4004","작성자가 포함되어 있지 않습니다."),
    NOT_MATCH_GENDER(HttpStatus.BAD_REQUEST,"BOARD4005","신청자의 성별이 게시물의 성별과 동일합니다"),
    LEADER_NOT_IN_APPLYUSER(HttpStatus.BAD_REQUEST,"BOARD4006","신청자(리더)가 포함되어있지 않습니다."),

    //Apply 관련 에러
    ALREADY_IN_USER(HttpStatus.BAD_REQUEST,"APLLY6001", "유저가 이미 참여해 있습니다."),
    USER_NOT_APPLY(HttpStatus.BAD_REQUEST,"APPLY4002","유저가 신청하지 않았습니다"),
    ALREADY_SUCCESS_APPLY(HttpStatus.BAD_REQUEST,"APPL5003","이미 승인된 신청입니다."),

    // 메일 관련 에러
    INVALID_MAIL_ADDRESS(HttpStatus.BAD_REQUEST, "MAIL4000", "경상국립대학교 이메일을 입력해주세요."),
    INVALID_VERIFY_NUMBER(HttpStatus.BAD_REQUEST, "MAIL4000", "인증번호가 올바르지 않습니다."),

    // slack 관련 에러
    CANNOT_SEND_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR, "SLACK5001", "slack으로 메세지를 보내지 못하였습니다."),

    // token 관련 에러
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001-1", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001-2", "유효하지 않은 Access Token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001-3", "Refresh Token이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001-4", "유효하지 않은 Refresh Token입니다."),
    NOT_EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4000", "만료되지 않은 Access 토큰입니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "TOKEN4001-5", "잘못된 JWT 서명입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001-6", "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4001-7", "JWT 토큰이 잘못되었습니다."),

    // notification 관련 에러
    OVERLAP_USER_TOKEN(HttpStatus.BAD_REQUEST,"FIREBASE4000","이미 유저의 파이어베이스토큰이 저장되어 있습니다"),
    NOT_FOUND_FIREBASE_TOKEN(HttpStatus.BAD_REQUEST,"FIREBASE4001","신청하는 게시판 유저의 파이어베이스 토큰이 없습니다"),
    FIREBASE_ERROR(HttpStatus.BAD_REQUEST,"FIREBASE4002","파이어베이스 에러 관리자에게 문의하세요."),
    JSON_FILE_ROAD_FAIL(HttpStatus.BAD_REQUEST,"FIREBASE4003","서버의 JSON파일 로드 실패"),
    INVALID_ACCESS(HttpStatus.BAD_REQUEST,"NOTIFICATION4004","잘못된 접근입니다."),
    NOT_FOUND_NOTIFICATION(HttpStatus.BAD_REQUEST,"NOTIFICATION4005","알림을 찾을수가 없습니다"),
    INPUT_ERROR(HttpStatus.BAD_REQUEST,"FIREBASE4006","파일 입출 작업 에러"),

    // websocket 관련 에러
    SESSION_ATTRIBUTES_IS_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "WEBSOCKET5001", "session attributes가 null 입니다."),
    SESSION_ATTRIBUTE_NOT_FOUND(HttpStatus.BAD_REQUEST, "WEBSOCKET4001", "요청한 session attributes에 해당하는 값이 없습니다."),
    NOT_FOUND_CHAT_ROOM_USER(HttpStatus.BAD_REQUEST, "WEBSOCKET4002", "채팅방에 해당 이메일을 가진 유저가 없습니다."),
    INVALID_DESTINATION(HttpStatus.BAD_REQUEST, "WEBSOCKET4003", "잘못된 경로입니다."),


    // chatRoom 관련 에러
    CHAT_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHATROOM4001", "채팅방을 찾을 수 없습니다.");

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

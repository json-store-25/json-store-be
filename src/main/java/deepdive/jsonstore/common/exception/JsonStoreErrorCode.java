package deepdive.jsonstore.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JsonStoreErrorCode {

    //common
    INVALID_INPUT_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시스템에 문제가 발생했습니다."),

    // notification
    MISSING_FCM_TOKEN(HttpStatus.BAD_REQUEST, "FCM 토큰이 없습니다."),
    NOTIFICATION_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."),
    REDIS_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 서버에 문제가 발생했습니다."),

    // order
    TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "타임아웃"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_EXPIRED(HttpStatus.GONE, "만료된 주문입니다."),

    // Join (회원가입)
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // 로그인
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    // 토큰
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰 형식입니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어 있습니다."),

    //delivery
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    DELIVERY_NOT_FOUND(HttpStatus.BAD_REQUEST, "배송지를 찾을 수 없습니다."),
    ZIPCODE_NOT_VALID(HttpStatus.BAD_REQUEST,"유효하지 않은 우편번호입니다."),
    ADDRESS_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"주소 검색 시스템 오류"),

    //entity
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 엔티티를 찾을 수 없습니다."),

    //s3
    S3_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "s3 시스템에 문제가 발생했습니다."),

    //admin
    ADMIN_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "관리자 로그인에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

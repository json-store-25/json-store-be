package deepdive.jsonstore.domain.admin.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode {

	ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 관리자를 찾을 수 없습니다."),
	ADMIN_BAD_REQUEST(HttpStatus.BAD_REQUEST, "관리자는 필수값입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}

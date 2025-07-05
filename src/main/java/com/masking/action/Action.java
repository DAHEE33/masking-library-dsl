package com.masking.action;

import java.io.IOException;
import java.util.Map;

/**
 * 데이터 보호 작업을 정의하는 인터페이스
 * 
 * <p>Action은 데이터 마스킹, 토큰화, 암호화, 감사 등의 작업을 캡슐화합니다.
 * 단일 Action을 직접 실행하거나, 여러 Action을 조합하여 복합적인 데이터 보호 파이프라인을 구성할 수 있습니다.</p>
 * 
 * <h3>사용 예시:</h3>
 * <pre>{@code
 * // 단일 Action 실행
 * Action maskAction = MaskAction.of("email", maskStrategy);
 * maskAction.apply(record);
 * 
 * // 복합 Action 조합
 * Actions actions = Actions.of(
 *     MaskAction.of("email", maskStrategy),
 *     EncryptAction.of("ssn", encryptStrategy)
 * );
 * actions.apply(record);
 * }</pre>
 * 
 * <h3>구현 클래스:</h3>
 * <ul>
 *   <li>{@link MaskAction} - 데이터 마스킹</li>
 *   <li>{@link TokenizeAction} - 데이터 토큰화</li>
 *   <li>{@link EncryptAction} - 데이터 암호화</li>
 *   <li>{@link AuditAction} - 감사 로그 기록</li>
 *   <li>{@link Actions} - 여러 Action 조합</li>
 *   <li>{@link com.masking.pipeline.MaskPipeline} - 파이프라인 형태의 Action</li>
 * </ul>
 * 
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public interface Action {
    
    /**
     * 주어진 레코드에 Action을 적용합니다.
     * 
     * <p>이 메서드는 레코드의 특정 필드에 대해 정의된 작업(마스킹, 토큰화, 암호화 등)을 수행합니다.
     * 작업 결과는 원본 레코드를 직접 수정합니다.</p>
     * 
     * @param record 처리할 데이터 레코드 (필드명-값 쌍의 Map)
     * @throws IOException Action 실행 중 I/O 오류가 발생한 경우
     * @throws IllegalArgumentException 레코드가 null이거나 필수 필드가 누락된 경우
     * @throws RuntimeException Action 실행 중 기타 오류가 발생한 경우
     */
    void apply(Map<String, String> record) throws IOException;
}

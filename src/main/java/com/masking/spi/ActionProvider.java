package com.masking.spi;

import com.masking.action.Action;
import java.util.Map;

/**
 * Action을 외부에서 확장할 수 있도록 하는 Service Provider Interface
 * 
 * <p>이 인터페이스를 구현하여 사용자 정의 Action을 라이브러리에 추가할 수 있습니다.
 * 구현 클래스는 META-INF/services/com.masking.spi.ActionProvider 파일에 등록해야 합니다.</p>
 * 
 * <h3>사용 예시:</h3>
 * <pre>{@code
 * // 1. ActionProvider 구현
 * public class CustomActionProvider implements ActionProvider {
 *     @Override
 *     public String getActionName() {
 *         return "custom";
 *     }
 *     
 *     @Override
 *     public Action createAction(Map&lt;String, Object&gt; config) {
 *         return new CustomAction(config);
 *     }
 * }
 * 
 * // 2. META-INF/services/com.masking.spi.ActionProvider 파일에 등록
 * // com.example.CustomActionProvider
 * 
 * // 3. 사용
 * Action customAction = ActionRegistry.createAction("custom", config);
 * }</pre>
 * 
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public interface ActionProvider {
    
    /**
     * 이 Action의 고유한 이름을 반환합니다.
     * @return Action 이름 (소문자, 하이픈으로 구분 권장)
     */
    String getActionName();
    
    /**
     * 설정을 기반으로 Action 인스턴스를 생성합니다.
     * @param config Action 생성에 필요한 설정 정보
     * @return 생성된 Action 인스턴스
     * @throws IllegalArgumentException 설정이 유효하지 않은 경우
     */
    Action createAction(Map<String, Object> config);
    
    /**
     * 이 Action에 대한 설명을 반환합니다.
     * @return Action 설명
     */
    default String getDescription() {
        return "Custom action: " + getActionName();
    }
    
    /**
     * 이 Action이 지원하는 설정 키들을 반환합니다.
     * @return 지원하는 설정 키들의 Set
     */
    default java.util.Set<String> getSupportedConfigKeys() {
        return java.util.Collections.emptySet();
    }
} 
package com.masking.spi;

import com.masking.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Action Provider들을 관리하는 레지스트리
 * 
 * <p>ServiceLoader를 통해 등록된 ActionProvider들을 관리하고,
 * 이름으로 Action을 생성할 수 있도록 합니다.</p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public class ActionRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(ActionRegistry.class);
    
    private static final Map<String, ActionProvider> providers = new HashMap<>();
    
    static {
        loadProviders();
    }
    
    /**
     * ServiceLoader를 통해 ActionProvider들을 로드합니다.
     */
    private static void loadProviders() {
        ServiceLoader<ActionProvider> loader = ServiceLoader.load(ActionProvider.class);
        
        for (ActionProvider provider : loader) {
            String name = provider.getActionName();
            if (name != null && !name.trim().isEmpty()) {
                providers.put(name.toLowerCase(), provider);
                logger.info("Action Provider 등록: {} - {}", name, provider.getDescription());
            } else {
                logger.warn("Action Provider 이름이 비어있어 등록되지 않음: {}", provider.getClass().getName());
            }
        }
        
        logger.info("총 {}개의 Action Provider가 등록되었습니다.", providers.size());
    }
    
    /**
     * 지정된 이름의 Action을 생성합니다.
     * 
     * @param actionName Action 이름
     * @param config Action 생성에 필요한 설정
     * @return 생성된 Action
     * @throws IllegalArgumentException Action 이름이 존재하지 않거나 설정이 유효하지 않은 경우
     */
    public static Action createAction(String actionName, Map<String, Object> config) {
        ActionProvider provider = providers.get(actionName.toLowerCase());
        
        if (provider == null) {
            throw new IllegalArgumentException(
                "Action '" + actionName + "'을 찾을 수 없습니다. " +
                "사용 가능한 Action들: " + getAvailableActionNames()
            );
        }
        
        try {
            return provider.createAction(config);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Action '" + actionName + "' 생성 중 오류 발생: " + e.getMessage(), e
            );
        }
    }
    
    /**
     * 사용 가능한 Action 이름들을 반환합니다.
     * 
     * @return Action 이름들의 Set
     */
    public static Set<String> getAvailableActionNames() {
        return providers.keySet();
    }
    
    /**
     * 지정된 이름의 Action이 등록되어 있는지 확인합니다.
     * 
     * @param actionName 확인할 Action 이름
     * @return 등록 여부
     */
    public static boolean isActionAvailable(String actionName) {
        return providers.containsKey(actionName.toLowerCase());
    }
    
    /**
     * 지정된 이름의 ActionProvider를 반환합니다.
     * 
     * @param actionName Action 이름
     * @return ActionProvider (없으면 null)
     */
    public static ActionProvider getProvider(String actionName) {
        return providers.get(actionName.toLowerCase());
    }
    
    /**
     * 모든 등록된 ActionProvider들의 정보를 반환합니다.
     * 
     * @return ActionProvider 정보 Map
     */
    public static Map<String, String> getProviderInfo() {
        return providers.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getDescription()
            ));
    }
    
    /**
     * 레지스트리를 다시 로드합니다.
     * 런타임에 새로운 ActionProvider가 추가된 경우 사용할 수 있습니다.
     */
    public static void reload() {
        providers.clear();
        loadProviders();
    }
} 
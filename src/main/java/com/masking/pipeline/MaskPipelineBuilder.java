package com.masking.pipeline;

import com.masking.action.*;
import com.masking.strategy.mask.*;
import com.masking.strategy.tokenize.*;
import com.masking.strategy.encrypt.*;
import com.masking.audit.AuditEventHandler;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class MaskPipelineBuilder {
    private final List<Action> actions = new ArrayList<>();

    /**
     * MaskPipelineBuilder 인스턴스를 생성합니다.
     * @return MaskPipelineBuilder 인스턴스
     */
    public static MaskPipelineBuilder newBuilder() {
        return new MaskPipelineBuilder();
    }

    /**
     * 마스킹 액션을 추가합니다.
     * @param field 마스킹할 필드명
     * @param strategy 마스킹 전략
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder mask(String field, MaskStrategy strategy) {
        actions.add(MaskAction.of(field, strategy));
        return this;
    }

    /**
     * 토큰화 액션을 추가합니다.
     * @param field 토큰화할 필드명
     * @param strategy 토큰화 전략
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder tokenize(String field, TokenizationStrategy strategy) {
        actions.add(TokenizeAction.of(field, strategy));
        return this;
    }

    /**
     * AES 암호화 액션을 추가합니다.
     * @param field 암호화할 필드명
     * @param key AES 키
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder encryptAes(String field, byte[] key) {
        actions.add(EncryptAction.of(field, AesEncryptionStrategy.of(key)));
        return this;
    }

    /**
     * RSA 암호화 액션을 추가합니다.
     * @param field 암호화할 필드명
     * @param publicKey RSA 공개키
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder encryptRsa(String field, PublicKey publicKey) {
        actions.add(EncryptAction.of(field, RsaEncryptionStrategy.of(publicKey)));
        return this;
    }

    /**
     * 감사 액션을 추가합니다.
     * @param field 감사 대상 필드명
     * @param handler 감사 이벤트 핸들러
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder audit(String field, AuditEventHandler handler) {
        actions.add(AuditAction.of(field, handler));
        return this;
    }

    /**
     * 마스킹+감사 복합 액션을 추가합니다.
     * @param field 마스킹할 필드명
     * @param strategy 마스킹 전략
     * @param handler 감사 이벤트 핸들러
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder maskWithAudit(String field, MaskStrategy strategy, AuditEventHandler handler) {
        actions.add(CompositeAuditAction.of(field, handler, MaskAction.of(field, strategy)));
        return this;
    }

    /**
     * 토큰화+감사 복합 액션을 추가합니다.
     * @param field 토큰화할 필드명
     * @param strategy 토큰화 전략
     * @param handler 감사 이벤트 핸들러
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder tokenizeWithAudit(String field, TokenizationStrategy strategy, AuditEventHandler handler) {
        actions.add(CompositeAuditAction.of(field, handler, TokenizeAction.of(field, strategy)));
        return this;
    }

    /**
     * AES 암호화+감사 복합 액션을 추가합니다.
     * @param field 암호화할 필드명
     * @param key AES 키
     * @param handler 감사 이벤트 핸들러
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder encryptAesWithAudit(String field, byte[] key, AuditEventHandler handler) {
        actions.add(CompositeAuditAction.of(field, handler, EncryptAction.of(field, AesEncryptionStrategy.of(key))));
        return this;
    }

    /**
     * RSA 암호화+감사 복합 액션을 추가합니다.
     * @param field 암호화할 필드명
     * @param publicKey RSA 공개키
     * @param handler 감사 이벤트 핸들러
     * @return MaskPipelineBuilder
     */
    public MaskPipelineBuilder encryptRsaWithAudit(String field, PublicKey publicKey, AuditEventHandler handler) {
        actions.add(CompositeAuditAction.of(field, handler, EncryptAction.of(field, RsaEncryptionStrategy.of(publicKey))));
        return this;
    }

    /**
     * 파이프라인을 빌드합니다.
     * @return MaskPipeline 인스턴스
     */
    public MaskPipeline build() {
        return MaskPipeline.of(Actions.of(actions.toArray(new Action[0])));
    }
}

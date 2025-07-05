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

    public static MaskPipelineBuilder newBuilder() {
        return new MaskPipelineBuilder();
    }

    public MaskPipelineBuilder mask(String field, MaskStrategy strategy) {
        actions.add(MaskAction.of(field, strategy));
        return this;
    }

    public MaskPipelineBuilder tokenize(String field, TokenizationStrategy strategy) {
        actions.add(TokenizeAction.of(field, strategy));
        return this;
    }

    public MaskPipelineBuilder encryptAes(String field, byte[] key) {
        actions.add(EncryptAction.of(field, AesEncryptionStrategy.of(key)));
        return this;
    }

    public MaskPipelineBuilder encryptRsa(String field, PublicKey publicKey) {
        actions.add(EncryptAction.of(field, RsaEncryptionStrategy.of(publicKey)));
        return this;
    }

    public MaskPipelineBuilder audit(String field, AuditEventHandler handler) {
        actions.add(AuditAction.of(field, handler));
        return this;
    }

    // 새로운 메서드들: before/after 추적이 가능한 복합 액션들
    public MaskPipelineBuilder maskWithAudit(String field, MaskStrategy strategy, AuditEventHandler handler) {
        actions.add(CompositeAuditAction.of(field, handler, MaskAction.of(field, strategy)));
        return this;
    }

    public MaskPipelineBuilder tokenizeWithAudit(String field, TokenizationStrategy strategy, AuditEventHandler handler) {
        actions.add(CompositeAuditAction.of(field, handler, TokenizeAction.of(field, strategy)));
        return this;
    }

    public MaskPipelineBuilder encryptAesWithAudit(String field, byte[] key, AuditEventHandler handler) {
        actions.add(CompositeAuditAction.of(field, handler, EncryptAction.of(field, AesEncryptionStrategy.of(key))));
        return this;
    }

    public MaskPipelineBuilder encryptRsaWithAudit(String field, PublicKey publicKey, AuditEventHandler handler) {
        actions.add(CompositeAuditAction.of(field, handler, EncryptAction.of(field, RsaEncryptionStrategy.of(publicKey))));
        return this;
    }

    public MaskPipeline build() {
        return MaskPipeline.of(Actions.of(actions.toArray(new Action[0])));
    }
}

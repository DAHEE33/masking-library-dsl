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

    public MaskPipeline build() {
        return MaskPipeline.of(Actions.of(actions.toArray(new Action[0])));
    }
}

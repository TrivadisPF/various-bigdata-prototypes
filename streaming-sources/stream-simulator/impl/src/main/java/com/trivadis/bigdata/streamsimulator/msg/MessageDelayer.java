package com.trivadis.bigdata.streamsimulator.msg;

import java.util.concurrent.atomic.AtomicInteger;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.integration.dsl.IntegrationFlowBuilder;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties.Speedup;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom delay handler, uses a pending delayed message counter to block inbound message handler.
 * 
 * @author Markus Zehnder
 */
@Slf4j
public class MessageDelayer {

    private final Speedup cfg;
    private long delayedMsgCheckMillis = 100;
    private final AtomicInteger delayedMsgCounter = new AtomicInteger(0);

    public MessageDelayer(Speedup cfg) {
        this.cfg = cfg;
    }

    public IntegrationFlowBuilder build(IntegrationFlowBuilder builder) {
        if (!cfg.isEnabled()) {
            log.debug("Delayer is disabled");
            return builder;
        }

        if (cfg.isSimpleMode()) {
            // use built-in Spring Integration logic
            log.warn("Using simple in-memory delayer, all messages will be loaded into memory!");
            return builder.delay("delayer.messageGroupId",
                    d -> d.delayExpression("headers['" + MsgHeader.DELAY + "']"));
        }

        log.info("Creating message delayer with max delayed msg count: {}", cfg.getMaxDelayedMessages());
        return builder.delay("delayer.messageGroupId", d -> d.delayExpression("headers['" + MsgHeader.DELAY + "']")
                .advice(newInboundMsgAdvice()).delayedAdvice(delayedMsgSendAdvice()));
    }

    /**
     * Intercepts the inbound message handler of the delayer. If the message is delayed and the number of pending
     * delayed messages reaches the maximum limit, the handler is blocked until the pending message count drops below
     * the limit.
     */
    MethodInterceptor newInboundMsgAdvice() {
        return new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                Object val = invocation.proceed();
                // a delayed message will return null, otherwise the message is sent immediately without delay
                if (val == null) {
                    delayedMsgCounter.incrementAndGet();
                    // block channel until pending message count drops below limit
                    while (delayedMsgCounter.get() > cfg.getMaxDelayedMessages()) {
                        Thread.sleep(delayedMsgCheckMillis);
                    }
                }
                return val;
            }
        };
    }

    /**
     * Intercepts the delayed message once it's executed. Decrements the delayed message counter.
     */
    MethodInterceptor delayedMsgSendAdvice() {
        return new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                // TODO how does retry handling work in the delayed message processor?
                // do we get called for each retry attempt?
                delayedMsgCounter.decrementAndGet();
                return invocation.proceed();
            }
        };
    }

}

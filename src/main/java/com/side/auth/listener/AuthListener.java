package com.side.auth.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;

public interface AuthListener {

    void smsSender(Channel channel, Message message) throws IOException;

    void deleteVerifier(Channel channel, Message message) throws IOException;
}

package com.example.demo.messaging;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import static com.example.demo.config.MessageChannelConfiguration.TCP_INPUT_CHANNEL_NAME;
import static com.example.demo.config.MessageChannelConfiguration.TCP_OUTPUT_CHANNEL_NAME;

@Component
public class MessagingController {

    private final MessageChannel tcpOutputChanel;

    public MessagingController(@Qualifier(TCP_OUTPUT_CHANNEL_NAME) MessageChannel tcpOutputChanel) {
        this.tcpOutputChanel = tcpOutputChanel;
    }

    @StreamListener(TCP_INPUT_CHANNEL_NAME)
    public void messageListener(Message<?> message) {
        System.out.println("Got message via controller:" + message.getPayload());
        final String connectionId = (String) message.getHeaders().get(IpHeaders.CONNECTION_ID);
        if (connectionId != null) {
            Message<String> outputMessage = MessageBuilder.withPayload("Hello message! (using channels). Received: " + message.getPayload())
                    .setHeader(IpHeaders.CONNECTION_ID, connectionId)
                    .build();
            tcpOutputChanel.send(outputMessage);
        }
    }
}

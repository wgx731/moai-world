package com.github.wgx731.ak47.config;

import com.github.wgx731.ak47.audit.AuditorAwareImpl;
import com.github.wgx731.ak47.message.MessageQueueConst;
import com.github.wgx731.ak47.service.MessageService;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AppConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    @Bean
    Queue queue() {
        return new Queue(MessageQueueConst.TRIGGER_PROCESS_QUEUE, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(MessageQueueConst.EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(MessageQueueConst.TRIGGER_PROCESS_QUEUE);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(MessageQueueConst.TRIGGER_PROCESS_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(MessageService receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}

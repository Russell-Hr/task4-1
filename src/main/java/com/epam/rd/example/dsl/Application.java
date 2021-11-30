package com.epam.rd.example.dsl;

import com.epam.rd.example.pojo.*;
import com.epam.rd.example.pojo.Package;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;
import org.springframework.messaging.Message;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

        Post post = ctx.getBean(Post.class);
        for (int i = 1; i <= 100; i++) {
            InputDelivery inputDelivery = new InputDelivery(i);
            inputDelivery.addItem(DeliveryType.DTH);
            inputDelivery.addItem(DeliveryType.DTS);
            inputDelivery.addItem(DeliveryType.TRANSFER);
            post.placeDelivery(inputDelivery);
        }

        System.out.println("Hit 'Enter' to terminate");
        System.in.read();
        ctx.close();
    }

    @MessagingGateway
    public interface Post {

        @Gateway(requestChannel = "deliveries.input")
        void placeDelivery(InputDelivery delivery);

    }

    private AtomicInteger dthCounter = new AtomicInteger();

    private AtomicInteger dtsCounter = new AtomicInteger();

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedDelay(1000).get();
    }

    @Bean
    public IntegrationFlow deliveries() {
        return f -> f
                .split(InputDelivery.class, InputDelivery::getItems)
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .<DeliveryItem, DeliveryType>route(DeliveryItem::getType, mapping -> mapping
                        .subFlowMapping(DeliveryType.DTH, sf -> sf
                                .channel(c -> c.queue(10))
                                .publishSubscribeChannel(c -> c
                                        .subscribe(s -> s.handle(m -> sleepUninterruptibly(1, TimeUnit.SECONDS)))
                                        .subscribe(sub -> sub
                                                .<DeliveryItem, String>transform(p ->
                                                        Thread.currentThread().getName() +
                                                                " prepared package #" +
                                                                this.dthCounter.incrementAndGet() +
                                                                " to home delivery from input delivery #" + p.getDeliveryNumber() + ": " + p)
                                                .log(LoggingHandler.Level.INFO, Message::getPayload)))
                                .bridge())
                        .subFlowMapping(DeliveryType.DTS, sf -> sf
                                .channel(c -> c.queue(10))
                                .publishSubscribeChannel(c -> c
                                        .subscribe(s -> s.handle(m -> sleepUninterruptibly(5, TimeUnit.SECONDS)))
                                        .subscribe(sub -> sub
                                                .<DeliveryItem, String>transform(p ->
                                                        Thread.currentThread().getName() +
                                                                " prepared package #" +
                                                                this.dtsCounter.incrementAndGet() +
                                                                " to stay at store from input delivery #" + p.getDeliveryNumber() + ": " + p)
                                                .log(LoggingHandler.Level.INFO, Message::getPayload)))
                                .bridge())
                        .subFlowMapping(DeliveryType.TRANSFER, sf -> sf
                                .log(LoggingHandler.Level.INFO, m -> "skipped package " + m.getPayload())
                                .bridge()))
                .<DeliveryItem, Package>transform(deliveryItem ->
                        new Package(deliveryItem.getDeliveryNumber(),
                                deliveryItem.getType()))
                .aggregate(aggregator -> aggregator
                        .outputProcessor(g ->
                                new OutputDelivery(g.getMessages()
                                        .stream()
                                        .map(message -> (Package) message.getPayload())
                                        .collect(Collectors.toList())))
                        .correlationStrategy(m -> ((Package) m.getPayload()).getDeliveryNumber()))
                .handle(CharacterStreamWritingMessageHandler.stdout());
    }

    private static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
        boolean interrupted = false;
        try {
            unit.sleep(sleepFor);
        } catch (InterruptedException e) {
            interrupted = true;
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Withdrawal;
import com.nttdata.bootcamp.entity.enums.EventType;
import com.nttdata.bootcamp.events.WithdrawalCreatedEventKafka;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaTemplate<String, EventKafka<?>> producer;

    @Value("${topic.withdrawal.name}")
    private String topicWithdrawal;

    public void publish(Withdrawal withdrawal) {

        WithdrawalCreatedEventKafka created = new WithdrawalCreatedEventKafka();
        created.setData(withdrawal);
        created.setId(UUID.randomUUID().toString());
        created.setType(EventType.CREATED);
        created.setDate(new Date());

        this.producer.send(topicWithdrawal, created);
    }

}

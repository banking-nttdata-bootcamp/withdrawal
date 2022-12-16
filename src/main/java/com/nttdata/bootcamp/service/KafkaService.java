package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Withdrawal;

public interface KafkaService {
    void publish(Withdrawal customer);
}

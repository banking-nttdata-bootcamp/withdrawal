package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Withdrawal;
import com.nttdata.bootcamp.repository.WithdrawalRepository;
import com.nttdata.bootcamp.service.KafkaService;
import com.nttdata.bootcamp.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class WithdrawalServiceImpl implements WithdrawalService {
    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public Flux<Withdrawal> findAll() {
        Flux<Withdrawal> withdrawalFlux = withdrawalRepository.findAll();
        return withdrawalFlux;
    }

    @Override
    public Flux<Withdrawal> findByAccountNumber(String accountNumber) {
        Flux<Withdrawal> withdrawalFlux = withdrawalRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return withdrawalFlux;
    }

    @Override
    public Mono<Withdrawal> findByNumber(String Number) {
        Mono<Withdrawal> withdrawalMono = withdrawalRepository
                .findAll()
                .filter(x -> x.getWithdrawalNumber().equals(Number))
                .next();
        return withdrawalMono;
    }

    @Override
    public Mono<Withdrawal> saveWithdrawal(Withdrawal dataWithdrawal) {

        Mono<Withdrawal> withdrawalMono = findByNumber(dataWithdrawal.getWithdrawalNumber())
                .flatMap(__ -> Mono.<Withdrawal>error(new Error("This Withdrawal  number " + dataWithdrawal.getWithdrawalNumber() + "exists")))
                .switchIfEmpty(saveTopic(dataWithdrawal));
        return withdrawalMono;



    }

    @Override
    public Mono<Withdrawal> updateWithdrawal(Withdrawal dataWithdrawal) {

        Mono<Withdrawal> transactionMono = findByNumber(dataWithdrawal.getWithdrawalNumber());
        try {
            dataWithdrawal.setDni(transactionMono.block().getDni());
            dataWithdrawal.setAmount(transactionMono.block().getAmount());
            dataWithdrawal.setCreationDate(transactionMono.block().getCreationDate());
            return withdrawalRepository.save(dataWithdrawal);
        }catch (Exception e){
            return Mono.<Withdrawal>error(new Error("This Withdrawal  " + dataWithdrawal.getAccountNumber() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> deleteWithdrawal(String Number) {
        Mono<Withdrawal> withdrawalMono = findByNumber(Number);
        try {
            Withdrawal withdrawal = withdrawalMono.block();
            return withdrawalRepository.delete(withdrawal);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("This Withdrawal number" + Number+ " do not exists"));
        }
    }

    @Override
    public Flux<Withdrawal> findByCommission(String accountNumber) {
        Flux<Withdrawal> transactions = withdrawalRepository
                .findAll()
                .filter(x -> x.getCommission()>0 && x.getAccountNumber().equals(accountNumber));
        return transactions;
    }

    public Mono<Withdrawal> saveTopic(Withdrawal dataDeposit){
        Mono<Withdrawal> monWithdrawal = withdrawalRepository.save(dataDeposit);
        this.kafkaService.publish(monWithdrawal.block());
        return monWithdrawal;
    }




}

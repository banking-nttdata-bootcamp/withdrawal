package com.nttdata.bootcamp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WithdrawalDto {
    private String dni;
    private String accountNumber;
    private String withdrawalNumber;
    private Double amount;
}

package com.quicktutorials.learnmicroservices.AccountMicroservice.services;

import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.Account;
import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.Operation;

import java.util.List;

public interface OperationService {

    List<Operation> getAllOperationPerAccount(String accountId);
    List<Account> getAllAccountsPerUser(String userId);
    Operation saveOperation(Operation operation);

}

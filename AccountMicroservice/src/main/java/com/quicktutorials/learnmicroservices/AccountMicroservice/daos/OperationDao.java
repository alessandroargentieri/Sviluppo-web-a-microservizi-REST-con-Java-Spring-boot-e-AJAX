package com.quicktutorials.learnmicroservices.AccountMicroservice.daos;

import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OperationDao extends JpaRepository<Operation, String>{

    @Query(value = "SELECT * FROM operations WHERE FK_ACCOUNT1=:account OR FK_ACCOUNT2=:account", nativeQuery = true)
    List<Operation> findAllOperationsByAccount(@Param("account")String account);

}

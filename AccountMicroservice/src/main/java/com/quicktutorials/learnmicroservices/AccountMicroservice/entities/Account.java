package com.quicktutorials.learnmicroservices.AccountMicroservice.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="accounts")
@AllArgsConstructor @NoArgsConstructor
public class Account {

    //String ID, String FK_USER, Double TOTAL
    @Id
    @Column(name="ID")
    @NotNull @NotBlank @NotEmpty
    @Getter @Setter
    private String id;

    @Column(name="FK_USER")
    @Getter @Setter
    @NotEmpty @NotBlank @NotNull
    private  String fkUser;

    @Column(name="TOTAL")
    @Getter @Setter
    private Double total;
}

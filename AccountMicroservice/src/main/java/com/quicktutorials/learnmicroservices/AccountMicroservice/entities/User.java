package com.quicktutorials.learnmicroservices.AccountMicroservice.entities;


//1) JPA
//2) JSR-303
//3) LOMBOK



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

@AllArgsConstructor @NoArgsConstructor   //Lombok annotations
@Entity                                  //JPA defines an Entity
@Table(name = "users")                   //JPA (if table name in the DB differs from Class Name)
public class User {


    //String ID, String USERNAME, String PASSWORD, String PERMISSION

    @Id                               //JPA id of the table
    @Column(name="ID")                //JPA (if column name is different from variable name)
    @NotEmpty @NotBlank @NotNull      //JSR-303 Validation
    @Getter @Setter                   //Lombok annotations
    private String id;

    @Column(name="USERNAME")          //JPA (if column name is different from variable name)
    @NotEmpty @NotBlank @NotNull      //JSR-303 Validation
    @Getter @Setter                   //Lombok annotations
    private String username;

    @Column(name="PASSWORD")          //JPA (if column name is different from variable name)
    @NotEmpty @NotBlank @NotNull      //JSR-303 Validation
    @Getter @Setter                   //Lombok annotations
    private String password;

    @Column(name="PERMISSION")        //JPA (if column name is different from variable name)
    @NotEmpty @NotBlank @NotNull      //JSR-303 Validation
    @Getter @Setter                   //Lombok annotations
    private String permission;



}

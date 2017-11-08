package com.quicktutorials.learnmicroservices.AccountMicroservice.utils;


import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class provides a series of method in order to encrypt and decript user passwords. it uses jasypt library
 */
@Component                              //Spring will automatically instatiate and inject this component as a normal @Bean defined into a @Configuration file
public class EncryptionUtils {

    @Autowired                          //@Bean declared in @SpringBootApplication file and injected here by Spring
    BasicTextEncryptor textEncryptor;

    /*
    this method provides an encryption of the String passed in input
    */
    public String encrypt(String data){
        return textEncryptor.encrypt(data);
    }

    /*
    this method provides a decryption of the String passed in input
    */
    public String decrypt(String encriptedData){
        return textEncryptor.decrypt(encriptedData);
    }

}
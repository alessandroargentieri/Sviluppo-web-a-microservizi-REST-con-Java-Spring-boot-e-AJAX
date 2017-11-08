package com.quicktutorials.learnmicroservices.AccountMicroservice.services;

import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.User;
import com.quicktutorials.learnmicroservices.AccountMicroservice.utils.UserNotLoggedException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public interface LoginService {

    Optional<User> getUserFromDbAndVerifyPassword(String id, String password)throws UserNotLoggedException;
    //-> userDao.findById(id), encryptionUtils.decrypt(pwd) -> UserNotLoggedException

    String createJwt(String subject, String name, String permission, Date date) throws UnsupportedEncodingException;
    //-> JwtUtils.generateJwt(...) 						 -> UnsupportedEncodingException


    Map<String, Object> verifyJwtAndGetData(HttpServletRequest request) throws UserNotLoggedException, UnsupportedEncodingException;
    //-> JwtUtils.getJwtFromHttpRequest(request)		-> UserNotLoggedException
    // 	  -> JwtUtils.jwt2Map(jwt)						-> UnsupportedEncodingException
    //												->  ExpiredJwtException


}

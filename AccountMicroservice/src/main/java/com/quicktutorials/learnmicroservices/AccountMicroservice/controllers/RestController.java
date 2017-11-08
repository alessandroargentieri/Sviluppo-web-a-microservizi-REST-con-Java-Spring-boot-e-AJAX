package com.quicktutorials.learnmicroservices.AccountMicroservice.controllers;


import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.Operation;
import com.quicktutorials.learnmicroservices.AccountMicroservice.entities.User;
import com.quicktutorials.learnmicroservices.AccountMicroservice.services.LoginService;
import com.quicktutorials.learnmicroservices.AccountMicroservice.services.LoginServiceImpl;
import com.quicktutorials.learnmicroservices.AccountMicroservice.services.OperationService;
import com.quicktutorials.learnmicroservices.AccountMicroservice.utils.UserNotLoggedException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@org.springframework.web.bind.annotation.RestController
public class RestController {


    private static final Logger log = LoggerFactory.getLogger(RestController.class);

    @Autowired
    LoginService loginService;

    @Autowired
    OperationService operationService;



    @RequestMapping("/hello")
    public String sayHello(){
        return "Hello everyone!";
    }

    /*  testare con PostMan con modalità x-www-form-urlencoded */

    //if pwd is null it will still return a user
    @RequestMapping("/newuser1")
    public String addUser(User user){
        return "User added correctly:" + user.getId() + ", "+ user.getUsername();
    }

    //if pwd is null it will return a JAVA JSR-303 error message thanks to @Valid
    @RequestMapping("/newuser2")
    public String addUserValid(@Valid User user){
        return "User added correctly:" + user.getId() + ", "+ user.getUsername();
    }

    //if pwd is null it will return a JAVA JSR-303 error message thanks to Spring object BindingResult
    @RequestMapping("/newuser3")
    public String addUserValidPlusBinding(@Valid User user, BindingResult result){
        if(result.hasErrors()){
            return result.toString();
        }
        return "User added correctly:" + user.getId() + ", "+ user.getUsername();
    }

    //if pwd is null it will return a SPRING VALIDATOR error message thanks to Spring object BindingResult
    @RequestMapping("/newuser4")
    public String addUserValidPlusBinding2(User user, BindingResult result){
        /* Spring validation */
        UserValidator userValidator = new UserValidator();
        userValidator.validate(user, result);

        if(result.hasErrors()){
            return result.toString();
        }
        return "User added correctly:" + user.getId() + ", "+ user.getUsername();
    }



    /*---------------------------INNER CLASS------------------------*/
    //Spring Validator Example
    private class UserValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return User.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            User user = (User) obj;
            if (user.getPassword().length() < 8) {
                errors.rejectValue("password", "the password must be at least 8 chars long!");
            }
        }
    }
    /*---------------------------------------------------------*/

    /**
     * inner class used as the Object tied into the Body of the ResponseEntity.
     * It's important to have this Object because it is composed of server response code and response object.
     * Then, JACKSON LIBRARY automatically convert this JsonResponseBody Object into a JSON response.
     */
    @AllArgsConstructor
    public class JsonResponseBody{
        @Getter @Setter
        private int server;
        @Getter @Setter
        private Object response;
    }

    /*---------------------------------------------------------*/


    @CrossOrigin
    @RequestMapping(value = "/login", method = POST)
    public ResponseEntity<JsonResponseBody> loginUser(@RequestParam(value ="id") String id, @RequestParam(value="password") String pwd){
        //check if user exists in DB -> if exists generate JWT and send back to client
        try {
            Optional<User> userr = loginService.getUserFromDbAndVerifyPassword(id, pwd);
            if(userr.isPresent()){
                User user = userr.get();
                String jwt = loginService.createJwt(user.getId(), user.getUsername(), user.getPermission(), new Date());
                return ResponseEntity.status(HttpStatus.OK).header("jwt", jwt).body(new JsonResponseBody(HttpStatus.OK.value(), "Success! User logged in!"));
            }
        }catch (UserNotLoggedException e1){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Login failed! Wrong credentials" + e1.toString()));
        }catch (UnsupportedEncodingException e2){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Token Error" + e2.toString()));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "No corrispondence in the database of users"));
    }


    @RequestMapping("/operations/account/{account}")
    public ResponseEntity<JsonResponseBody> fetchAllOperationsPerAccount(HttpServletRequest request, @PathVariable(name = "account") String account){
        //request -> fetch JWT -> check validity -> Get operations from the user account
        try {
            loginService.verifyJwtAndGetData(request);
            //user verified
            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), operationService.getAllOperationPerAccount(account)));
        }catch(UnsupportedEncodingException e1){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Unsupported Encoding: " + e1.toString()));
        }catch (UserNotLoggedException e2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "User not correctly logged: " + e2.toString()));
        }catch(ExpiredJwtException e3){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "Session Expired!: " + e3.toString()));
        }
    }

    @RequestMapping(value = "/accounts/user", method = POST)
    public ResponseEntity<JsonResponseBody> fetchAllAccountsPerUser(HttpServletRequest request){
        //request -> fetch JWT -> recover User Data -> Get user accounts from DB
        try {
            Map<String, Object> userData = loginService.verifyJwtAndGetData(request);
            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), operationService.getAllAccountsPerUser((String) userData.get("subject"))));
        }catch(UnsupportedEncodingException e1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request: " + e1.toString()));
        }catch(UserNotLoggedException e2){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "User not logged! Login first : " + e2.toString()));
        }catch(ExpiredJwtException e3){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "Session Expired!: " + e3.toString()));
        }
    }



    @RequestMapping(value = "/operations/add", method=POST)
    public ResponseEntity<JsonResponseBody> addOperation(HttpServletRequest request, @Valid Operation operation, BindingResult bindingResult){
        //request -> fetch JWT -> recover User Data -> Save valid operation in DB
        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "Error! Invalid format of data."));
        }
        try {
            loginService.verifyJwtAndGetData(request);
            return ResponseEntity.status(HttpStatus.OK).body(new JsonResponseBody(HttpStatus.OK.value(), operationService.saveOperation(operation)));
        }catch(UserNotLoggedException e1){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponseBody(HttpStatus.FORBIDDEN.value(), "User not logged! Login first : " + e1.toString()));
        }catch(UnsupportedEncodingException e2){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseBody(HttpStatus.BAD_REQUEST.value(), "Bad Request: " + e2.toString()));
        }catch(ExpiredJwtException e3){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(new JsonResponseBody(HttpStatus.GATEWAY_TIMEOUT.value(), "Session Expired!: " + e3.toString()));
        }
    }




}
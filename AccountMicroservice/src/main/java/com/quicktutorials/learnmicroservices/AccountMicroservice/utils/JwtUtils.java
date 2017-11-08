package com.quicktutorials.learnmicroservices.AccountMicroservice.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides method in order to generate and validate JSON Web Tokens
 */
                            //it will be automatically instantiated and injected by Spring
public class JwtUtils {

    //https://stormpath.com/blog/jwt-java-create-verify
    //https://stormpath.com/blog/beginners-guide-jwts-in-java
    //https://github.com/jwtk/jjwt

    /**
     * this method generate the Jwt token to be sent to the client
     * @param subject "RGNLSN87H13D761R"
     * @param date    new Date(1300819380)
     * @param name    "Alessandro Argentieri"
     * @param scope   "user"
     * @return String jwt
     * @throws java.io.UnsupportedEncodingException
     */
    public static String generateJwt(String subject, Date date, String name, String scope) throws java.io.UnsupportedEncodingException{

        String jwt = Jwts.builder()
                .setSubject(subject)
                .setExpiration(date)
                .claim("name", name)
                .claim("scope", scope)
                .signWith(
                        SignatureAlgorithm.HS256,
                        "myPersonalSecretKey12345".getBytes("UTF-8")
                )
                .compact();

        return jwt;
    }

    /**
     * this method converts the token into a map of Userdata checking it's validity
     * @param jwt
     * @return HashMap<String, Object> of user data
     * @throws java.io.UnsupportedEncodingException
     */
    public static Map<String, Object> jwt2Map(String jwt) throws java.io.UnsupportedEncodingException, ExpiredJwtException{

        Jws<Claims> claim = Jwts.parser()
                .setSigningKey("myPersonalSecretKey12345".getBytes("UTF-8"))
                .parseClaimsJws(jwt);

        String name = claim.getBody().get("name", String.class);
        String scope = (String) claim.getBody().get("scope");

        Date expDate = claim.getBody().getExpiration();
        String subj = claim.getBody().getSubject();

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("scope", scope);
        userData.put("exp_date", expDate);
        userData.put("subject", subj);

        Date now = new Date();
        if(now.after(expDate)){
            throw new ExpiredJwtException(null, null, "Session expired!");
        }

        return userData;
    }


    /**
     * this method extracts the jwt from the header or the cookie in the Http request
     * @param request
     * @return jwt
     */
    public static String getJwtFromHttpRequest(HttpServletRequest request){
        String jwt = null;
        if(request.getHeader("jwt") != null){
            jwt = request.getHeader("jwt");     //token present in header
        }else if(request.getCookies() != null){
            Cookie[] cookies = request.getCookies();   //token present in cookie
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    jwt = cookie.getValue();
                }
            }
        }
        return jwt;
    }





}

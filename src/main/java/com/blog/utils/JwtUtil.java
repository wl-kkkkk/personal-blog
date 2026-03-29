package com.blog.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "my_test_secret_key_2024_32_bytes!!";

    //获取SECERT
    public static SecretKey getKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    //生成token
    public static String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis()+3600*1000))
                .signWith(getKey(),Jwts.SIG.HS256)
                .compact();
    }

    //解析token
    public static String parseToken(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //验证token
    public static boolean validateToken(String token){
        try{
            parseToken(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}

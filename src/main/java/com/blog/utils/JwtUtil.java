package com.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "my_test_secret_key_2026_32_bytes!!";

    //获取SECERT
    public static SecretKey getKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    //生成token
    public static String generateToken(Long userId){
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis()+3600*1000))
                .signWith(getKey(),Jwts.SIG.HS256)
                .compact();
    }

    //解析token
    public static Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
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

    //判断token是否快要过期
    public static boolean isExpireSoon(Claims claims){
        Date expiration=claims.getExpiration();
        long remain=expiration.getTime()-System.currentTimeMillis();
        return remain<(1000*60*15);
    }
}

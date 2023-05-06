package fa.training.eventbox.security;

import fa.training.eventbox.model.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TokenProvider {
    private final String secretKey = "1234567asdfgh";

    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        LocalDateTime expiredTime = LocalDateTime.now().plusHours(1);

        return Jwts.builder()
                .setSubject(authentication.getName()) // username
                .claim("authorities", authorities)
                .setExpiration(Date.from(expiredTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

        public Authentication getAuthentication(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try{
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            List<GrantedAuthority> authorityList = Arrays.stream(claims.get("authorities").toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            User principal = new User(claims.getSubject(), "", authorityList);
            return new UsernamePasswordAuthenticationToken(principal, null, authorityList);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        List<GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority(UserRole.ROLE_CUSTOMER.name()));
        Authentication authentication = new UsernamePasswordAuthenticationToken("customer01@gmail.com", "", roles);
        TokenProvider tokenProvider = new TokenProvider();
        System.out.println(tokenProvider.generateToken(authentication));
        System.out.println(tokenProvider.getAuthentication(tokenProvider.generateToken(authentication)));
    }






}
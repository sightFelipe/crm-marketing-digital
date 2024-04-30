package com.crmmarketingdigitalback2024.model.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long token_id;
    private String token;
    private Date expirationTime;
    private boolean used;

    private static final int EXPIRATION_TIME = 1440;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public PasswordResetTokenEntity(String token, UserEntity user) {
        super();
        this.token = token;
        this.user = user;
        this.expirationTime = this.getTokenExpirationTime();
        this.used = false;
    }

    public PasswordResetTokenEntity(String token) {
        super();
        this.token = token;
        this.expirationTime = this.getTokenExpirationTime();
        this.used = false;
    }

    public Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }

    public String generateJwt(String secretKey) {
        Claims claims = Jwts.claims();
        claims.put("token_id", this.token_id);
        claims.put("token", this.token);
        claims.put("expirationTime", this.expirationTime);
        claims.put("user_id", this.user.getId());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}

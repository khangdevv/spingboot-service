package com.spingboot_study.spingboot_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.spingboot_study.spingboot_service.dto.request.AuthenticationRequest;
import com.spingboot_study.spingboot_service.dto.request.IntrospectRequest;
import com.spingboot_study.spingboot_service.dto.request.LogoutRequest;
import com.spingboot_study.spingboot_service.dto.request.RefreshRequest;
import com.spingboot_study.spingboot_service.dto.response.AuthenticationResponse;
import com.spingboot_study.spingboot_service.dto.response.IntrospectResponse;
import com.spingboot_study.spingboot_service.entity.InvalidatedToken;
import com.spingboot_study.spingboot_service.entity.User;
import com.spingboot_study.spingboot_service.exception.AppException;
import com.spingboot_study.spingboot_service.exception.ErrorCode;
import com.spingboot_study.spingboot_service.repository.InvalidatedTokenRepository;
import com.spingboot_study.spingboot_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // Use final for fields to ensure immutability
public class AuthenticationService {
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    protected static final String STRING_KEY = "VkY6Of6HYxFWW5yydVr9A2nmZvyUbS6/KuuEyBPwavOCNOHS/3N+fufpubeT0mrT";

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws AppException, JOSEException, ParseException {
        var token = introspectRequest.getToken();

        boolean isValid = true;

        try {
            var signedJWT = verifyToken(token);
        }
        catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean authenticated =  passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken());

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(STRING_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date  expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expityTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw  new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user) {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getUsername()).issuer("khang").issueTime(new Date()).expirationTime(
                new Date(System.currentTimeMillis() + 3600000) // Token valid for 1 hour
        )
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user)).
                build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS512),
                payload
        );

        try {
            jwsObject.sign(new MACSigner(STRING_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error signing JWT", e);
        }
    }

    private String buildScope(User user) {
        StringJoiner scope = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {scope.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        scope.add(permission.getName());
                    });
                }
            });
        }
        return scope.toString();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken());

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var exp = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(exp)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var user = userRepository.findByUsername(signedJWT.getJWTClaimsSet().getSubject())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
}

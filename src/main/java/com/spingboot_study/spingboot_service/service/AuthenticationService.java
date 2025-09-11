package com.spingboot_study.spingboot_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.spingboot_study.spingboot_service.dto.request.AuthenticationRequest;
import com.spingboot_study.spingboot_service.dto.request.IntrospectRequest;
import com.spingboot_study.spingboot_service.dto.response.AuthenticationResponse;
import com.spingboot_study.spingboot_service.dto.response.IntrospectResponse;
import com.spingboot_study.spingboot_service.entity.User;
import com.spingboot_study.spingboot_service.exception.AppException;
import com.spingboot_study.spingboot_service.exception.ErrorCode;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // Use final for fields to ensure immutability
public class AuthenticationService {
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @NonFinal
    protected static final String STRING_KEY = "VkY6Of6HYxFWW5yydVr9A2nmZvyUbS6/KuuEyBPwavOCNOHS/3N+fufpubeT0mrT";

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws AppException, JOSEException, ParseException {
        var token = introspectRequest.getToken();

        JWSVerifier verifier = new MACVerifier(STRING_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date  expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expityTime.after(new Date()))
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

    private String generateToken(User user) {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getUsername()).issuer("khang").issueTime(new Date()).expirationTime(
                new Date(System.currentTimeMillis() + 3600000) // Token valid for 1 hour
        ).claim("scope", buildScope(user)).
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
}

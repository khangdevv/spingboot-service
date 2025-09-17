package com.spingboot_study.spingboot_service.configuration;

import com.nimbusds.jose.JOSEException;
import com.spingboot_study.spingboot_service.dto.request.IntrospectRequest;
import com.spingboot_study.spingboot_service.service.AuthenticationService;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final String secretKey = "VkY6Of6HYxFWW5yydVr9A2nmZvyUbS6/KuuEyBPwavOCNOHS/3N+fufpubeT0mrT";

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder decoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response =  authenticationService.introspect(IntrospectRequest.builder().token(token).build());

            if (!response.isValid()) {
                throw new JwtException("Invalid token");
            }
        }
        catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(decoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            decoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }

        return decoder.decode(token);
    }
}

package com.spingboot_study.spingboot_service.configuration;

import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.JOSEException;
import com.spingboot_study.spingboot_service.dto.request.IntrospectRequest;
import com.spingboot_study.spingboot_service.exception.AppException;
import com.spingboot_study.spingboot_service.exception.ErrorCode;
import com.spingboot_study.spingboot_service.service.AuthenticationService;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @NonFinal
    @Value("${spring.jwt.signerKey}")
    protected String STRING_KEY;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder decoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build());

            if (!response.isValid()) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(decoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(STRING_KEY.getBytes(), "HS512");
            decoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return decoder.decode(token);
    }
}

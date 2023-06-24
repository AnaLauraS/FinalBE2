package com.dh.msbills.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component

// KeyCloakJwtAuthenticationConverter
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthConverter.class);
    private final JwtAuthConverterProperties properties;

    public JwtAuthConverter(JwtAuthConverterProperties properties) {
        this.properties = properties;
    }
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt) throws JsonProcessingException {
        Set<GrantedAuthority> resourcesRoles = new HashSet();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        //llamo al método extract roles, y le digo desde que ruta del JWT
        resourcesRoles.addAll(extractRoles("resource_access", objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get("claims")));
        resourcesRoles.addAll(extractRoles("realm_access", objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get("claims")));
        // llamo al método para extraer el scope
        resourcesRoles.addAll(extractScope("scope", objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get("claims")));
        // llamo al método para extraer los grupos
        resourcesRoles.addAll(extractGroup("groups", objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get("claims")));
        // logueo la info
        log.info(resourcesRoles.toString());
        return resourcesRoles;
    }

    public AbstractAuthenticationToken convert(final Jwt source) {
        Collection<GrantedAuthority> authorities = null;
        try {
            authorities = this.getGrantedAuthorities(source);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new JwtAuthenticationToken(source, authorities);
    }

    public Collection<GrantedAuthority> getGrantedAuthorities(Jwt source) throws JsonProcessingException {
        return (Collection) Stream.concat(this.defaultGrantedAuthoritiesConverter.convert(source).stream(), extractResourceRoles(source).stream()).collect(Collectors.toSet());
    }

    // creo el metodo para extraer roles
    private static List<GrantedAuthority> extractRoles(String route, JsonNode jwt) {
        Set<String> rolesWithPrefix = new HashSet<>();
        jwt.path(route)
                .elements()
                .forEachRemaining(e -> e.path("roles")
                        .elements()
                        .forEachRemaining(r -> rolesWithPrefix.add("ROLE_" + r.asText())));
        final List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));
        return authorityList;
    }

    // creo el metodo para extraer scope
    private static List<GrantedAuthority> extractScope(String route, JsonNode jwt) {
        Set<String> roleScope = new HashSet<>();
        jwt.path(route)
                .elements()
                .forEachRemaining(e->roleScope.add("SCOPE_"+e.asText()));
        final List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roleScope.toArray(new String[0]));
        return authorityList;
    }

    // creo el metodo para extraer grupos
    private static List<GrantedAuthority> extractGroup(String route, JsonNode jwt) {
        Set<String> groups = new HashSet<>();
        jwt.path(route)
                .elements()
                .forEachRemaining(e->groups.add("GROUP_"+e.asText()));
        final List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(groups.toArray(new String[0]));
        return authorityList;
    }
}
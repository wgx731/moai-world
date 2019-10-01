package com.github.wgx731.ak47.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";

    @Value("${ldap.urls}")
    private String ldapUrls;

    @Value("${ldap.base.dn}")
    private String ldapBaseDn;

    @Value("${ldap.username}")
    private String ldapSecurityPrincipal;

    @Value("${ldap.password}")
    private String ldapPrincipalPassword;

    @Value("${ldap.user.dn.pattern}")
    private String ldapUserDnPattern;

    @Value("${local.user.password}")
    private String localUserPassword;

    @Value("${local.admin.password}")
    private String localAdminPassword;

    @NonNull
    private SecurityUtils securityUtils;

    @NonNull
    private CustomRequestCache customRequestCache;

    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()
            // Register our CustomRequestCache, that saves unauthorized access attempts, so
            // the user is redirected after login.
            .requestCache().requestCache(customRequestCache)
            // Restrict access to our application.
            .and().authorizeRequests()
            // Allow all flow internal requests.
            .requestMatchers(securityUtils::isFrameworkInternalRequest).permitAll()
            // Allow all actuator requests.
            .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
            // Allow all requests by logged in users.
            .anyRequest().authenticated()
            // Configure the login page.
            .and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
            .failureUrl(LOGIN_FAILURE_URL)
            // Configure logout
            .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
            // Vaadin Flow static resources
            "/VAADIN/**",
            // the standard favicon URI
            "/favicon.ico",
            // the robots exclusion standard
            "/robots.txt",
            // web application manifest
            "/manifest.webmanifest",
            "/sw.js",
            "/offline-page.html",
            // icons and images
            "/icons/**",
            "/images/**",

            // (development mode) static resources
            "/frontend/**",
            // (development mode) webjars
            "/webjars/**",
            // (development mode) H2 debugging console
            "/h2-console/**",

            // (production mode) static resources
            "/frontend-es5/**", "/frontend-es6/**"
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // ldap auth
        auth.ldapAuthentication()
            .contextSource()
            .url(ldapUrls + ldapBaseDn)
            .managerDn(ldapSecurityPrincipal)
            .managerPassword(ldapPrincipalPassword)
            .and()
            .userDnPatterns(ldapUserDnPattern);
        // in memory auth
        auth.inMemoryAuthentication()
            .withUser("user")
            .password(localUserPassword)
            .roles("USER")
            .and()
            .withUser("admin")
            .password(localAdminPassword)
            .roles("ADMIN");
    }

}

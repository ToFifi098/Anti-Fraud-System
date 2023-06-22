package antifraud.api.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class Configuration extends WebSecurityConfigurerAdapter {

    @Autowired RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    UserDetailsService userDetailsService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .antMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/auth/user").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasRole("ADMINISTRATOR")
                .antMatchers( HttpMethod.GET, "/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                .antMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                .antMatchers(HttpMethod.POST, "/api/antifraud/transaction/").hasRole("MERCHANT")
                .antMatchers(HttpMethod.PUT, "/api/auth/access").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.PUT, "/api/auth/access/").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.PUT, "/api/auth/role/").hasRole("ADMINISTRATOR")
                .antMatchers("/api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .antMatchers("/api/antifraud/suspicious-ip/*").hasRole("SUPPORT")
                .antMatchers("/api/antifraud/stolencard").hasRole("SUPPORT")
                .antMatchers("/api/antifraud/stolencard/").hasRole("SUPPORT")
                .antMatchers("/api/antifraud/stolencard/*").hasRole("SUPPORT")
                .antMatchers(HttpMethod.GET,"/api/antifraud/history").hasRole("SUPPORT")
                .antMatchers(HttpMethod.GET,"/api/antifraud/history/*").hasRole("SUPPORT")
                .antMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole("SUPPORT")
                .antMatchers("/actuator/shutdown").permitAll() // needs to run test
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session

    }

    @Bean
    public PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }

}

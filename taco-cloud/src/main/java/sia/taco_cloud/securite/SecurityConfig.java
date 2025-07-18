package sia.taco_cloud.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import sia.taco_cloud.repository.UserRepository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        List<UserDetails> usersList = new ArrayList<>();
        usersList.add(new User(
                "buzz", passwordEncoder.encode("password"),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
        usersList.add(new User(
                "woody", passwordEncoder.encode("password"),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
        return new InMemoryUserDetailsManager(usersList);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
         var user = userRepository.findByUsername(username);
         if (user != null) {
             return user;
         }
         throw new UsernameNotFoundException(MessageFormat.format("user with name {0} not found", username));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/design", "/orders").hasRole("USER")
                        .requestMatchers("/", "/**").permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                )
                .oauth2Login((auth) -> auth.loginPage("/login"))
                .logout(logout -> logout.logoutUrl("/home"));
        return http.build();
    }

    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/login");
    }
}

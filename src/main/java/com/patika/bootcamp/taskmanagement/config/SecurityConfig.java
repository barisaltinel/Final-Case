package com.patika.bootcamp.taskmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails projectManager = User.withUsername("project_manager")
                .password(passwordEncoder.encode("managerpass"))
                .roles("PROJECT_MANAGER")
                .build();

        UserDetails teamLeader = User.withUsername("team_leader")
                .password(passwordEncoder.encode("leaderpass"))
                .roles("TEAM_LEADER")
                .build();

        UserDetails teamMember = User.withUsername("team_member")
                .password(passwordEncoder.encode("memberpass"))
                .roles("TEAM_MEMBER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(projectManager, teamLeader, teamMember, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/projects/**").hasAnyRole("PROJECT_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/projects/**").hasRole("PROJECT_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/projects/**").hasRole("PROJECT_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tasks/**").hasAnyRole("PROJECT_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/tasks/**").hasAnyRole("PROJECT_MANAGER", "TEAM_LEADER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/tasks/{id}/state").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/tasks/{id}/cancel").hasAnyRole("PROJECT_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/tasks/{id}/block").hasAnyRole("PROJECT_MANAGER", "TEAM_LEADER")
                .requestMatchers(HttpMethod.POST, "/api/attachments/**").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/attachments/**").hasAnyRole("TEAM_MEMBER", "TEAM_LEADER", "PROJECT_MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/comments/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic();
        return http.build();
    }
}
package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.RoleService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Role;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Autowired
  UserServiceDB userService;
  @Autowired
  RoleService roleService;
  @Autowired
  JwtAuthenticationFilter jwtAuthFilter;
  @Autowired
  AuthenticationProvider authenticationProvider;

  @Bean
  public static PasswordEncoder getEncoder(){return new BCryptPasswordEncoder();}
  @Bean
  public SecurityFilterChain mainConfig(HttpSecurity http) throws Exception{
      http
          .csrf()
          .disable()
          .authorizeHttpRequests()
          .requestMatchers("/authenticate","/register")
          .permitAll()
          .anyRequest()
          .authenticated()
          .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .authenticationProvider(authenticationProvider)
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();

  }
  @Bean
  public void seedUsers(){
    Optional<Role> userRoleQuery = roleService.findRoleByName("ROLE_USER");
    Optional<Role> managerRoleQuery = roleService.findRoleByName("ROLE_MANAGER");
    Optional<Role> adminRoleQuery = roleService.findRoleByName("ROLE_ADMIN");
    List<Role> rolesToAdd = new ArrayList<>();
    List<Role> userRoles = new ArrayList<>();
    List<Role> managerRoles = new ArrayList<>();
    List<Role> adminRoles = new ArrayList<>();
    if(userRoleQuery.isEmpty()){
      Role userRole = new Role("ROLE_USER");
      rolesToAdd.add(userRole);
    }
    if(managerRoleQuery.isEmpty()){
      Role managerRole = new Role("ROLE_MANAGER");
      rolesToAdd.add(managerRole);
    }
    if(adminRoleQuery.isEmpty()){
      Role adminRole = new Role("ROLE_ADMIN");
      Role managerRole = new Role("ROLE_MANAGER");
      rolesToAdd.add(adminRole);
    }
    if (!rolesToAdd.isEmpty())
      roleService.saveRoleList(rolesToAdd);
    userRoles.add(roleService.findRoleByName("ROLE_USER").get());
    managerRoles.add(roleService.findRoleByName("ROLE_MANAGER").get());
    adminRoles.add(roleService.findRoleByName("ROLE_MANAGER").get());
    adminRoles.add(roleService.findRoleByName("ROLE_ADMIN").get());

    Optional<User> userQuery = userService.findUserByEmail("user@user.com");
    Optional<User> managerQuery = userService.findUserByEmail("manager@manager.com");
    Optional<User> adminQuery = userService.findUserByEmail("admin@admin.com");

    List<User> usersToAdd = new ArrayList<>();
    if(userQuery.isEmpty()){
      User newUser = new User("User","User","user@user.com","1234");
      newUser.setRoles(userRoles);
      usersToAdd.add(newUser);
    }
    if(managerQuery.isEmpty()){
      User newManager = new User("Manager","Manager","manager@manager.com","1234");
      newManager.setRoles(managerRoles);
      usersToAdd.add(newManager);
    }
    if(adminQuery.isEmpty()){
      User newAdmin = new User("Admin","Admin","admin@admin.com","1234");
      newAdmin.setRoles(adminRoles);
      usersToAdd.add(newAdmin);
    }
    if(!usersToAdd.isEmpty())
      userService.saveUserList(usersToAdd);
  }

}

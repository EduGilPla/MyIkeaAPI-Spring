package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Role;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.repositories.UserRepository;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
  private final UserRepository userRepository;
  @Bean
  public UserDetailsService userDetailsService(){
    return new UserDetailsService() {
      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> User = userRepository.findByEmail(username);
        if(User.isEmpty())
          throw new UsernameNotFoundException("No se encontró el usuario");
        User foundUser = User.get();
        return buildUserDetails(foundUser);
      }
      private UserDetails buildUserDetails(User user){
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
            user.getPassword(),
            buildUserAuthority(user.getRoles()));
      }
      public List<GrantedAuthority> buildUserAuthority(List<Role> roles){
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for(Role role : roles){
          grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return grantedAuthorities;
      }
    };
  }
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}

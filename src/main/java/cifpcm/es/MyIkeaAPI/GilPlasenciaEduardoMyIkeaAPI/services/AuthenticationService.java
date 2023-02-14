package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Role;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.repositories.UserRepository;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.AuthenticationRequest;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.AuthenticationResponse;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  public AuthenticationResponse register(RegisterRequest request) {
    User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
    user.setRoles(Arrays.asList(new Role("ROLE_USER")));
    userRepository.save(user);
    var jwtToken = jwtService.generateToken(buildUserDetails(user));
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
    var jwtToken = jwtService.generateToken(buildUserDetails(user));
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
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
}

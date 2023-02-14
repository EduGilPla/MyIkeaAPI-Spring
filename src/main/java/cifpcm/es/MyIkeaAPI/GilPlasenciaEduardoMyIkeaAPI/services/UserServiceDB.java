package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Role;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.repositories.RoleRepository;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.repositories.UserRepository;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.AuthenticationRequest;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.AuthenticationResponse;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Primary
@Service
@RequiredArgsConstructor
public class UserServiceDB implements UserDetailsService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  public List<User> getUserList(){
    return userRepository.findAll();
  }
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
  public boolean registerUser(User userDto){
    if(emailExists(userDto.getEmail())){
      return false;
    }
    Optional<Role> roleQuery = roleRepository.findByName("ROLE_USER");
    if (roleQuery.isEmpty())
      return false;
    Role defaultRole = roleQuery.get();
    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    userDto.setRoles(Arrays.asList(defaultRole));
    userRepository.save(userDto);
    return true;
  }
  public void saveUserList(List<User> userList){
    for(User user : userList){
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    userRepository.saveAll(userList);
  }
  public boolean saveUserCart(User user){
    try{
      userRepository.save(user);
      return true;
    }
    catch (Exception exception){
      return false;
    }
  }
  public boolean emailExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }
  public Optional<User> findUserById(int id){
    return userRepository.findById(id);
  }
  public void deleteUser(User toDelete) throws Exception{
    try {
      userRepository.delete(toDelete);
    }
    catch (Exception exception){
      throw exception;
    }
  }
}

package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;


import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.AuthenticationRequest;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.AuthenticationResponse;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.RegisterRequest;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.AuthenticationService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AuthController {
  @Autowired
  UserServiceDB userService;
  @Autowired
  AuthenticationService authenticationService;
  private final String ErrorAttributeName = "error";
  @GetMapping("/login")
  public String Login(){
    return "/authentication/login";
  }
  @GetMapping("/register")
  public String Register(Model ViewData){
    ViewData.addAttribute("user", new User());
    return "/authentication/register";
  }
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> Register(@RequestBody RegisterRequest request){
    return ResponseEntity.ok(authenticationService.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> Register(@RequestBody AuthenticationRequest request){
    return ResponseEntity.ok(authenticationService.authenticate(request));
  }
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/users")
  public String ListUsers(Model ViewData, Authentication authentication){
    List<User> userList = userService.getUserList();
    Optional<User> currentAdminUser = userService.findUserByEmail(authentication.getName());
    userList.remove(currentAdminUser.get());
    ViewData.addAttribute("userList",userList);
    return "/authentication/users";
  }
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/users/delete/{id}")
  public String deleteUser(@PathVariable String id, Authentication authentication, Model ViewData){
    Optional<User> deleteQuery = userService.findUserById(Integer.parseInt(id));
    User currentAdminUser = userService.findUserByEmail(authentication.getName()).get();
    List<User> userList = userService.getUserList();
    if(deleteQuery.isEmpty()){
      String USER_NOT_FOUND_ERROR = "El usuario con id " + id + "no existe en la base de datos.";
      userList.remove(currentAdminUser);
      ViewData.addAttribute("userList",userList);
      ViewData.addAttribute(ErrorAttributeName,USER_NOT_FOUND_ERROR);
      return "/authentication/users";
    }
    User userToDelete = deleteQuery.get();
    if(userToDelete.getEmail() == currentAdminUser.getEmail()){
      String CANT_DELETE_YOUR_USER_ERROR = "El usuario con id " + id + " es el administrador loggeado actualmente. No se puede eliminar.";
      userList.remove(currentAdminUser);
      ViewData.addAttribute("userList",userList);
      ViewData.addAttribute(ErrorAttributeName,CANT_DELETE_YOUR_USER_ERROR);
      return "/authentication/users";
    }
    try {
      userService.deleteUser(userToDelete);
      return "redirect:/users";
    }
    catch (Exception exception){
      ViewData.addAttribute(ErrorAttributeName,exception.getMessage());
      return "/authentication/users";
    }
  }
}

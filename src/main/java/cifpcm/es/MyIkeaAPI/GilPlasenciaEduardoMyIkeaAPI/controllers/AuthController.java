package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;


import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.AuthenticationRequest;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.AuthenticationResponse;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.security.RegisterRequest;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.AuthenticationService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<List<User>> ListUsers(Model ViewData, Authentication authentication){
    List<User> userList = userService.getUserList();
    Optional<User> currentAdminUser = userService.findUserByEmail(authentication.getName());
    userList.remove(currentAdminUser.get());
    return new ResponseEntity<>(userList, HttpStatus.OK);
  }
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/users/delete/{id}")
  public ResponseEntity<User> deleteUser(@PathVariable String id, Authentication authentication, Model ViewData){
    Optional<User> deleteQuery = userService.findUserById(Integer.parseInt(id));
    User currentAdminUser = userService.findUserByEmail(authentication.getName()).get();
    if(deleteQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    User userToDelete = deleteQuery.get();
    if(userToDelete.getEmail() == currentAdminUser.getEmail()){
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    try {
      userService.deleteUser(userToDelete);
      return new ResponseEntity<>(userToDelete,HttpStatus.OK);
    }
    catch (Exception exception){
      return new ResponseEntity<>(userToDelete,HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

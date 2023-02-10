package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;


import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {
  @Autowired
  UserServiceDB userService;
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
  public String Register(@Valid @ModelAttribute("user") User newUser, BindingResult bindingResult, Model ViewData){
    if(bindingResult.hasErrors()){
      return "/authentication/register";
    }
    if(!userService.registerUser(newUser)){
      String USER_ALREADY_EXISTS_ERROR = "El usuario " + newUser.getEmail() + " ya existe.";
      ViewData.addAttribute(ErrorAttributeName,USER_ALREADY_EXISTS_ERROR);
      return "/common/welcome";
    }
    String CORRECT_REGISTER_MESSAGE = "¡El usuario " + newUser.getEmail() + " se ha registrado correctamente!";
    ViewData.addAttribute("registerCorrecto",CORRECT_REGISTER_MESSAGE);
    return "/common/welcome";
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

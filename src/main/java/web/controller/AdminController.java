package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import web.model.Role;
//import web.model.Roles;
import web.model.User;
import web.service.RolesService;
import web.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RolesService roleService;

    @Autowired
    public AdminController(UserService userService, RolesService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping
    public String allUsers(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("userAuth", user);
        model.addAttribute("newUser", new User());
        model.addAttribute("users", userService.allread());
        return "/admin";
    }

    @PostMapping("/new")
    public String newPerson(@ModelAttribute("newuser") User user, @RequestParam("roles") ArrayList<Long> roles){
        user.setRoles(roles.stream().map(roleService::getRole).collect(Collectors.toSet()));
        userService.save(user);
        return "redirect:/admin";
    }

//    @PostMapping(value = "")
//    public String createUser(@RequestParam String firstName, @RequestParam String lastName,
//                             @RequestParam String username, @RequestParam String password,
//                             @RequestParam List<Long> roles) {
//        Set<Role> userRoles = new HashSet<>();
//        for(Long role: roles){
//            userRoles.add(roleService.getRole(role));
//        }
//        User user = new User(firstName, lastName, username, password);
//        user.setRoles(userRoles);
//        userService.save(user);
//        return "redirect:/admin";
//    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id")long id, Model model){
        model.addAttribute("user",userService.show(id));
        model.addAttribute("roles", roleService.allread());
        return "/change";
    }
    @PutMapping("/{id}")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam List<Long> roles) {
        Set<Role> userRoles = new HashSet<>();
        for(Long role: roles){
            userRoles.add(roleService.getRole(role));
        }
        user.setRoles(userRoles);
        userService.update(user);
        return "redirect:/admin";
    }


    @DeleteMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}

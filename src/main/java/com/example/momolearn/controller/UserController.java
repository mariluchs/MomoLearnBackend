package com.example.momolearn.controller;

import com.example.momolearn.model.User;
import com.example.momolearn.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserRepository users;
  public UserController(UserRepository users) { this.users = users; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User create(@RequestBody User u) {
    u.setId(null);
    u.setCreatedAt(Instant.now());
    return users.save(u);
  }

  @GetMapping public List<User> list() { return users.findAll(); }

  @GetMapping("/{userId}")
  public User get(@PathVariable String userId) {
    return users.findById(userId).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));
  }

  @PutMapping("/{userId}")
  public User update(@PathVariable String userId, @RequestBody User patch) {
    User cur = get(userId);
    if (patch.getName() != null) cur.setName(patch.getName());
    if (patch.getEmail() != null) cur.setEmail(patch.getEmail());
    return users.save(cur);
  }

  @DeleteMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String userId) { users.deleteById(userId); }
}

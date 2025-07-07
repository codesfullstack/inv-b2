package com.inv.spring.data.mongodb.controller;
import com.inv.spring.data.mongodb.model.User;
import com.inv.spring.data.mongodb.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.security.MessageDigest;
@CrossOrigin(origins = "http://localhost:3000", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
RequestMethod.DELETE, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> addUser(@RequestBody User user) {
      Map<String, String> response = new HashMap<>();
      try {
          Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
          if (existingUser.isPresent()) {
              response.put("message", "User with this email already exists");
              return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            String encryptedPassword = encryptPassword(user.getPassword());
            user.setPassword(encryptedPassword);
            User newUser = userRepository.save(user);
            String token = Jwts.builder()
            .setSubject(newUser.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
            .signWith(SECRET_KEY)
            .compact();
            response.put("_id", newUser.getId());
            response.put("name", newUser.getName());
            response.put("email", newUser.getEmail());
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
          } catch (Exception e) {
            response.put("message", "Error occurred while registering user");
              return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
          }
          @GetMapping("/get-users")
          public ResponseEntity<List<User>> getAllUsers() {
            try {
                List<User> users = userRepository.findAll();
                if (users.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                  }
                  return new ResponseEntity<>(users, HttpStatus.OK);
                } catch (Exception e) {
                  return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
              }
              @GetMapping("/get-user/{id}")
              public ResponseEntity<User> getUserById(@PathVariable("id") String id) {
                Optional<User> userData = userRepository.findById(id);
                return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
              }
              @PutMapping("/update-user/{id}")
              public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user) {
                Optional<User> userData = userRepository.findById(id);
                if (userData.isPresent()) {
                    User _user = userData.get();
                    _user.setName(user.getName());
                    _user.setEmail(user.getEmail());
                    _user.setPassword(user.getPassword());
                    return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
                  } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                  }
                }
                @DeleteMapping("/delete-user/{id}")
                public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("id") String id) {
                  Map<String, String> response = new HashMap<>();
                  try {
                      Optional<User> existingUser = userRepository.findById(id);
                      if (existingUser.isPresent()) {
                          userRepository.deleteById(id);
                          response.put("message", "Usuario eliminado con Ã©xito");
                          return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                          response.put("message", "No se encontrÃ³ el usuario con ID: " + id);
                          return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                        }
                      } catch (Exception e) {
                        response.put("message", "Error al eliminar el usuario");
                        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                      }
                    }
                    @PostMapping("/auth")
                    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody User user) {
                      Map<String, String> response = new HashMap<>();
                      try {
                          Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
                          if (existingUser.isPresent()) {
                              User storedUser = existingUser.get();
                              if (verifyPassword(user.getPassword(), storedUser.getPassword())) {
                                  String token = generateToken(storedUser.getId());
                                  response.put("_id", storedUser.getId());
                                  response.put("name", storedUser.getName());
                                  response.put("email", storedUser.getEmail());
                                  response.put("token", token);
                                  return new ResponseEntity<>(response, HttpStatus.OK);
                                } else {
                                  response.put("message", "Invalid email or password");
                                  return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                                }
                              } else {
                                response.put("message", "User not found");
                                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                              }
                            } catch (Exception e) {
                              response.put("message", "Error occurred while authenticating user");
                                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                              }
                            }
                            @PutMapping("/update-profile/{id}")
                            public ResponseEntity<Map<String, String>> updateUserProfile(@PathVariable("id") String id,
                            @RequestBody User updateUser) {
                              Map<String, String> response = new HashMap<>();
                              try {
                                  Optional<User> userData = userRepository.findById(id);
                                  if (userData.isPresent()) {
                                      User user = userData.get();
                                      user.setName(updateUser.getName() != null ? updateUser.getName() : user.getName());
                                      user.setEmail(updateUser.getEmail() != null ? updateUser.getEmail() : user.getEmail());
                                      if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
                                          String encryptedPassword = encryptPassword(updateUser.getPassword());
                                          user.setPassword(encryptedPassword);
                                        }
                                        User updatedUser = userRepository.save(user);
                                        response.put("_id", updatedUser.getId());
                                        response.put("name", updatedUser.getName());
                                        response.put("email", updatedUser.getEmail());
                                        return new ResponseEntity<>(response, HttpStatus.OK);
                                      } else {
                                        response.put("message", "User not found");
                                        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                                      }
                                    } catch (Exception e) {
                                      response.put("message", "Error occurred while updating user profile");
                                        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                                      }
                                    }
                                    public static String encryptPassword(String password) {
                                      try {
                                          MessageDigest digest = MessageDigest.getInstance("SHA-256");
                                          byte[] encodedHash = digest.digest(password.getBytes());
                                          StringBuilder hexString = new StringBuilder();
                                          for (byte b : encodedHash) {
                                              String hex = Integer.toHexString(0xff & b);
                                              if (hex.length() == 1)
                                                hexString.append('0');
                                                hexString.append(hex);
                                              }
                                              return hexString.toString();
                                            } catch (NoSuchAlgorithmException e) {
                                              e.printStackTrace();
                                              return null;
                                            }
                                          }
                                          public static boolean verifyPassword(String enteredPassword, String storedPassword) {
                                            String hashedEnteredPassword = encryptPassword(enteredPassword);
                                            return hashedEnteredPassword.equals(storedPassword);
                                          }
                                          public static String generateToken(String userId) {
                                            return Jwts.builder()
                                            .setSubject(userId)
                                            .setIssuedAt(new Date())
                                            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                                            .signWith(SECRET_KEY)
                                            .compact();
                                          }
                                        }

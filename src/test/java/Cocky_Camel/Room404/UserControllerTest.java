package Cocky_Camel.Room404;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testCreateUser_ok() throws Exception {
        User user = new User("laura@test.com", "Laura", "abc123", null, User.Role.User, false);
        user.setId(1);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode("abc123")).thenReturn("encoded-abc123");

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"laura@test.com\",\"nickname\":\"Laura\",\"password\":\"abc123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuario creado correctamente")));
    }

    @Test 
    public void testCreateUser_badRequest() throws Exception {
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\",\"nickname\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUserById_ok() throws Exception {
        User user = new User("juan@test.com", "Juan", "1234", null, User.Role.User, false);
        user.setId(5);

        when(userRepository.findById(5)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    public void testGetUserById_notFound() throws Exception {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser_ok() throws Exception {
        when(userRepository.existsById(10)).thenReturn(true);

        mockMvc.perform(delete("/api/user/10"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("borrado")));
    }

    @Test
    public void testDeleteUser_notFound() throws Exception {
        when(userRepository.existsById(100)).thenReturn(false);

        mockMvc.perform(delete("/api/user/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testLogin_ok() throws Exception {
        User user = new User("cperez@test.com", "Carlos", "1234", null, User.Role.User, false);
        when(userRepository.findByEmailIgnoreCase("cperez@test.com")).thenReturn(user);
        when(passwordEncoder.matches("1234", "1234")).thenReturn(true);
        when(jwtUtil.generateToken("cperez@test.com")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/user/login/cperez@test.com/1234"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Login correcto")));
    }

    @Test
    public void testLogin_badCredentials() throws Exception {
        User user = new User("cperez@test.com", "Carlos", "1234", null, User.Role.User, false);
        when(userRepository.findByEmailIgnoreCase("cperez@test.com")).thenReturn(user);

        mockMvc.perform(post("/api/user/login/cperez@test.com/wrongpass"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUser_ok() throws Exception {
        User existing = new User("ana@test.com", "Ana", "abc", null, User.Role.User, false);
        existing.setId(2);
        when(userRepository.findById(2)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("xyz")).thenReturn("encoded-xyz");

        mockMvc.perform(put("/api/user/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ana.update@test.com\",\"nickname\":\"Ana Actualizada\",\"password\":\"xyz\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("actualizado correctamente")));
    }

    @Test
    public void testUpdateUser_notFound() throws Exception {
        when(userRepository.findById(77)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/user/77")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"noexiste@test.com\",\"password\":\"y\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testResetPassword_acceptsPasswordKey() throws Exception {
        User existing = new User("reset@test.com", "Reset", "oldhash", null, User.Role.User, false);
        when(userRepository.findByEmailIgnoreCase("reset@test.com")).thenReturn(existing);
        when(passwordResetService.isTokenValid("reset@test.com", "123456")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("encoded-newpass");

        mockMvc.perform(post("/api/user/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"reset@test.com\",\"token\":\"123456\",\"password\":\"newpass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Contrasena actualizada correctamente")));

        verify(passwordResetService).clearToken("reset@test.com");
        verify(userRepository).save(existing);
    }
}
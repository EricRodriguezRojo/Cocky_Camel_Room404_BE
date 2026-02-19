package Cocky_Camel.hospital;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NurseController.class)
public class NurseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NurseRepository nurseRepository;

    // 1. Crear enfermero
    @Test
    public void testCreateNurse_ok() throws Exception {
        Nurse nurse = new Nurse("Laura", "laura123", "abc123");
        nurse.setId(1);

        when(nurseRepository.save(any(Nurse.class))).thenReturn(nurse);

        mockMvc.perform(post("/nurse")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Laura\",\"user\":\"laura123\",\"password\":\"abc123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("creado correctamente")));
    }

    
    
    // 2. Crear enfermero con datos vacíos
    @Test 
    public void testCreateNurse_badRequest() throws Exception {
        mockMvc.perform(post("/nurse")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"user\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // 3. Buscar enfermero por ID existente
    @Test
    public void testGetNurseById_ok() throws Exception {
        Nurse nurse = new Nurse("Juan", "juan123", "1234");
        nurse.setId(5);

        when(nurseRepository.findById(5)).thenReturn(Optional.of(nurse));

        mockMvc.perform(get("/nurse/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    // 4. Buscar enfermero por ID inexistente
    @Test
    public void testGetNurseById_notFound() throws Exception {
        when(nurseRepository.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/nurse/99"))
                .andExpect(status().isNotFound());
    }

    // 5. Eliminar enfermero existente
    @Test
    public void testDeleteNurse_ok() throws Exception {
        when(nurseRepository.existsById(10)).thenReturn(true);

        mockMvc.perform(delete("/nurse/10"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("borrado")));
    }

    // 6. Eliminar enfermero inexistente
    @Test
    public void testDeleteNurse_notFound() throws Exception {
        when(nurseRepository.existsById(100)).thenReturn(false);

        mockMvc.perform(delete("/nurse/100"))
                .andExpect(status().isNotFound());
    }

    
    // 7. Login correcto
    @Test
    public void testLogin_ok() throws Exception {
        Nurse nurse = new Nurse("Carlos", "cperez", "1234");
        when(nurseRepository.findByUserIgnoreCase("cperez")).thenReturn(nurse);

        mockMvc.perform(post("/nurse/login/cperez/1234"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Login correcto")));
    }

    // 8. Login incorrecto
    @Test
    public void testLogin_badCredentials() throws Exception {
        Nurse nurse = new Nurse("Carlos", "cperez", "1234");
        when(nurseRepository.findByUserIgnoreCase("cperez")).thenReturn(nurse);

        mockMvc.perform(post("/nurse/login/cperez/wrongpass"))
                .andExpect(status().isUnauthorized());
    }

    // 9. Actualizar enfermero existente
    @Test
    public void testUpdateNurse_ok() throws Exception {
        Nurse existing = new Nurse("Ana", "ana1", "abc");
        existing.setId(2);
        when(nurseRepository.findById(2)).thenReturn(Optional.of(existing));

        mockMvc.perform(put("/nurse/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Ana Actualizada\",\"user\":\"ana2\",\"password\":\"xyz\"}"))
                .andExpect(status().isOk());
    }

    
    // 10. Actualizar enfermero inexistente	
    @Test
    public void testUpdateNurse_notFound() throws Exception {
        when(nurseRepository.findById(77)).thenReturn(Optional.empty());

        mockMvc.perform(put("/nurse/77")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"NoExiste\",\"user\":\"x\",\"password\":\"y\"}"))
                .andExpect(status().isNotFound());
    }
}

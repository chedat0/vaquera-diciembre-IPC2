/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package otros;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


/**
 *
 * @author jeffm
 */
public class JacksonConfig {
    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Registrar módulo para java.time
        mapper.registerModule(new JavaTimeModule());
        
        // No serializar fechas como timestamps (usar formato ISO)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // printing para debugging
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        return mapper;
    }
    
     public static ObjectMapper createCompactMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Registrar módulo para java.time
        mapper.registerModule(new JavaTimeModule());
        
        // No serializar fechas como timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}

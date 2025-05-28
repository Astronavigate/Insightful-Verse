package tech.eagloxis.service.iviep;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface CodeService {
    String runJava(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

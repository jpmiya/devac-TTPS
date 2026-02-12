package org.example.devac.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Servicio que consulta la API de Georef Argentina para obtener
 * barrio (departamento/municipio) y ciudad (localidad) a partir de coordenadas.
 *
 * API docs: https://datosgobar.github.io/georef-ar-api/
 * Endpoint: GET https://apis.datos.gob.ar/georef/api/ubicacion?lat=X&lon=Y
 */
@Service
public class GeorefService {

    private static final String GEOREF_URL = "https://apis.datos.gob.ar/georef/api/ubicacion";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Resultado de la consulta a Georef.
     */
    public static class UbicacionResult {
        private final String barrio;      // departamento.nombre o municipio.nombre
        private final String ciudad;      // localidad_censal.nombre o provincia.nombre

        public UbicacionResult(String barrio, String ciudad) {
            this.barrio = barrio;
            this.ciudad = ciudad;
        }

        public String getBarrio() { return barrio; }
        public String getCiudad() { return ciudad; }
    }

    /**
     * Dado un par de coordenadas (lat, lon), consulta la API de Georef
     * y devuelve barrio y ciudad. Si falla, devuelve null.
     *
     * @param lat latitud (ej: -34.603633)
     * @param lon longitud (ej: -58.3837587)
     * @return UbicacionResult con barrio y ciudad, o null si falla
     */
    public UbicacionResult resolverUbicacion(double lat, double lon) {
        try {
            String url = String.format("%s?lat=%s&lon=%s", GEOREF_URL, lat, lon);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Georef API respondió con status: " + response.statusCode());
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode ubicacion = root.get("ubicacion");

            if (ubicacion == null) {
                return null;
            }

            // Barrio: usamos municipio.nombre (si existe), sino departamento.nombre
            String barrio = null;
            if (ubicacion.has("municipio") && ubicacion.get("municipio").has("nombre")) {
                barrio = ubicacion.get("municipio").get("nombre").asText(null);
            }
            if (barrio == null && ubicacion.has("departamento") && ubicacion.get("departamento").has("nombre")) {
                barrio = ubicacion.get("departamento").get("nombre").asText(null);
            }

            // Ciudad: usamos localidad_censal si existe, sino provincia
            String ciudad = null;
            if (ubicacion.has("localidad_censal") && ubicacion.get("localidad_censal").has("nombre")) {
                ciudad = ubicacion.get("localidad_censal").get("nombre").asText(null);
            }
            if (ciudad == null && ubicacion.has("provincia") && ubicacion.get("provincia").has("nombre")) {
                ciudad = ubicacion.get("provincia").get("nombre").asText(null);
            }

            return new UbicacionResult(barrio, ciudad);

        } catch (Exception e) {
            System.err.println("Error al consultar Georef API: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parsea un string de coordenadas con formato "lat,lon" y resuelve la ubicación.
     *
     * @param coordenadas string con formato "lat,lon" (ej: "-34.603633,-58.3837587")
     * @return UbicacionResult o null si el formato es inválido o la API falla
     */
    public UbicacionResult resolverUbicacion(String coordenadas) {
        if (coordenadas == null || coordenadas.isBlank()) {
            return null;
        }

        try {
            String[] parts = coordenadas.split(",");
            if (parts.length != 2) {
                return null;
            }
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());
            return resolverUbicacion(lat, lon);
        } catch (NumberFormatException e) {
            System.err.println("Coordenadas inválidas: " + coordenadas);
            return null;
        }
    }
}

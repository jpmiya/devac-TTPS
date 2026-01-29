# Guía de uso de MinIO

## ✅ Implementación completada

### 1. Docker Compose
MinIO está configurado en `docker-compose.yml`:
- **Puerto 9000:** API de MinIO
- **Puerto 9001:** Consola web de administración
- **Credenciales:** minioadmin / minioadmin123

### 2. Dependencia Maven
Agregada en `pom.xml`:
```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.7</version>
</dependency>
```

### 3. MinioService creado
Ubicación: `src/main/java/org/example/devac/services/MinioService.java`

Funcionalidades:
- ✅ Subir archivos
- ✅ Obtener archivos
- ✅ Eliminar archivos
- ✅ Generar URLs públicas
- ✅ Crear bucket automáticamente
- ✅ Configurar permisos públicos de lectura

### 4. Modelo Mascota actualizado
- Campo `foto` renombrado a `fotoUrl`
- Almacena la URL completa del archivo en MinIO

### 5. MascotaController actualizado
Endpoint modificado para aceptar archivos:

```http
POST /mascota/register
Content-Type: multipart/form-data

FormData:
  - mascota: { "duenoId": 1, "nombre": "Firulais", ... } (JSON)
  - foto: [archivo de imagen] (opcional)
```

## 🚀 Cómo usar

### 1. Iniciar los servicios
```bash
sudo docker compose down -v
sudo docker compose up --build
```

### 2. Acceder a MinIO Console
Abre en tu navegador: http://localhost:9001

**Credenciales:**
- Username: `minioadmin`
- Password: `minioadmin123`

Aquí puedes ver los archivos subidos en el bucket `mascotas`.

### 3. Registrar una mascota con foto desde el frontend

**Con JavaScript (fetch):**
```javascript
const formData = new FormData();

// Agregar datos de la mascota como JSON
const mascotaData = {
  duenoId: 1,
  nombre: "Firulais",
  tamaño: "Mediano",
  color: "Marrón",
  tipo: "Perro",
  raza: "Mestizo",
  descripcion: "Perdido en el parque",
  coordenadas: "-34.603722,-58.381592",
  fechaDePerdida: "2026-01-20"
};

formData.append('mascota', new Blob([JSON.stringify(mascotaData)], {
  type: 'application/json'
}));

// Agregar archivo de imagen
const fileInput = document.querySelector('#fotoInput');
if (fileInput.files[0]) {
  formData.append('foto', fileInput.files[0]);
}

// Enviar al backend
fetch('http://localhost:8080/mascota/register', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('Mascota registrada:', data);
  console.log('URL de la foto:', data.fotoUrl);
});
```

**Con Angular:**
```typescript
registrarMascota(mascotaData: any, foto: File | null) {
  const formData = new FormData();
  
  formData.append('mascota', new Blob([JSON.stringify(mascotaData)], {
    type: 'application/json'
  }));
  
  if (foto) {
    formData.append('foto', foto);
  }
  
  return this.http.post('http://localhost:8080/mascota/register', formData);
}
```

### 4. Mostrar la foto en el frontend

La respuesta incluye el campo `fotoUrl`:
```json
{
  "id": 1,
  "nombre": "Firulais",
  "fotoUrl": "http://localhost:9000/mascotas/1_1738162800000.jpg",
  ...
}
```

Simplemente usa esa URL en un `<img>`:
```html
<img [src]="mascota.fotoUrl" alt="Foto de {{ mascota.nombre }}">
```

## 🔧 Configuración

### Variables de entorno (`.env`)
```
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123
```

### Cambiar a localhost (desarrollo local sin Docker)
Si corres la app SIN Docker pero MinIO SÍ con Docker:

En `MinioService.java`, el endpoint ya se ajusta automáticamente:
```java
String publicEndpoint = endpoint.replace("minio:9000", "localhost:9000");
```

## 📝 Notas importantes

1. **Bucket público:** El bucket `mascotas` está configurado para lectura pública. Cualquiera con la URL puede ver las imágenes.

2. **Nombres únicos:** Los archivos se guardan con el formato: `{mascotaId}_{timestamp}.{extension}`

3. **URLs:** Las URLs generadas apuntan a `localhost:9000` en desarrollo. En producción, configura un dominio apropiado.

4. **Eliminación:** Actualmente NO se eliminan las fotos de MinIO al eliminar una mascota. Considera implementar esto si es necesario.

## 🐛 Troubleshooting

**Error: "Connection refused"**
- Verifica que MinIO esté corriendo: `sudo docker ps | grep minio`
- Verifica que el puerto 9000 esté libre: `sudo lsof -i :9000`

**Las fotos no se ven**
- Verifica que el bucket sea público
- Accede directamente a la URL en el navegador
- Revisa la consola de MinIO en http://localhost:9001

**Error al subir archivos grandes**
- Configura el tamaño máximo en `application.properties`:
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

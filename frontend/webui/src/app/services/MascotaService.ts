import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type EstadoMascota = 'PERDIDO_PROPIO' | 'ENCONTRADO' | 'ADOPCION' | string;

export interface UsuarioRef {
  id: number;
  nombre?: string;
  email?: string;
  barrio?: string;
  ciudad?: string;
  coordenadas?: string;   // "lat,lon"
}

export interface MascotaRequest {
  duenoId: number;
  nombre: string;
  tamanio?: string;          // sin ñ
  color?: string;
  fechaDePerdida: string;    // camelCase
  estado: EstadoMascota;
  foto?: string;
  descripcion?: string;
  tipo?: string;
  raza?: string;
}

export interface Mascota {
  id: number;
  nombre: string;

  tipo?: string;
  raza?: string;
  color?: string;
  tamaño?: string;
  foto?: string;
  fotoUrl?: string;
  descripcion?: string;

  estado?: EstadoMascota;

  // normalmente llega como string "2026-01-23"
  fecha_de_perdida?: string;

  dueno?: UsuarioRef;
}

@Injectable({ providedIn: 'root' })
export class MascotasService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  findAllLost(): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.baseUrl}/mascota/findAllLost`, {
      withCredentials: true,
    });
  }

  /**
   * Backend espera:
   *  - multipart/form-data
   *  - part "mascota" = JSON
   *  - part "foto" opcional
   */
  register(req: MascotaRequest, file?: File): Observable<Mascota> {
    // Si querés conservar tu payload con nulls explícitos (sirve para evitar undefined)
    const payload: any = {
      duenoId: req.duenoId,
      nombre: req.nombre,
      tamanio: req.tamanio ?? null,
      color: req.color ?? null,
      fechaDePerdida: req.fechaDePerdida,
      estado: req.estado,
      foto: req.foto ?? null,
      descripcion: req.descripcion ?? null,
      tipo: req.tipo ?? null,
      raza: req.raza ?? null,
    };

    const fd = new FormData();

    // GOTCHA: filename para que Spring no se ponga exquisito con @RequestPart
    fd.append(
      'mascota',
      new Blob([JSON.stringify(payload)], { type: 'application/json' }),
      'mascota.json'
    );

    if (file) {
      fd.append('foto', file, file.name);
    }

    return this.http.post<Mascota>(`${this.baseUrl}/mascota/register`, fd, {
      withCredentials: true,
    });
  }
}

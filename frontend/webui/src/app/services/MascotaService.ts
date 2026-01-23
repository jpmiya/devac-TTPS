import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroments/enviroment';

export type EstadoMascota = 'PERDIDO_PROPIO' | 'ENCONTRADO' | 'ADOPCION' | string;

export interface UsuarioRef {
  id: number;
  nombre?: string;
  email?: string;
}

export interface Mascota {
  id: number;
  nombre: string;

  tipo?: string;
  raza?: string;
  color?: string;
  tamaño?: string;
  foto?: string;
  coordenadas?: string;
  descripcion?: string;

  estado?: EstadoMascota;

  // fecha: normalmente te llega como string "2026-01-23"
  fecha_de_perdida?: string;

  dueno?: UsuarioRef;
}


@Injectable({ providedIn: 'root' })
export class MascotasService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  findAllLost(): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.baseUrl}/mascota/findAllLost`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {EstadoMascota, UsuarioRef} from './MascotaService';

export interface UsuarioRegister {
  nombreYApellido: string;
  email: string;
  password: string;
  telefono: string;
  barrio: string;
  ciudad: string;
  coordenadas?: string;
  posicion?: number;
  puntos?: number;
  casosEnZona?: number;
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

export interface Usuario {
  id: number;
  nombreYApellido: string;
  email: string;
  telefono?: string;
  barrio?: string;
  ciudad?: string;
  posicion?: number;
  puntos?: number;
  casosEnZona?: number;
}


export interface UsuarioLogin {
  email: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  register(usuario: UsuarioRegister): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.baseUrl}/usuario/register`, usuario);
  }

  login(email: string, password: string) {
    return this.http.post(
      `${this.baseUrl}/usuario/login`,
      { email, password },
      {
        withCredentials: true,
        responseType: 'text'
      }
    );
  }

  getMascotasDeUsuario(userId: number): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.baseUrl}/usuario/${userId}/mascotas`, { withCredentials: true });
  }

  getMe(): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.baseUrl}/usuario/me`, {
      withCredentials: true
    });
  }




}

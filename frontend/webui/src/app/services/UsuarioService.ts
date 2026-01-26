import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UsuarioRegister {
  nombreYApellido: string;
  email: string;
  password: string;
  telefono: string;
  barrio: string;
  ciudad: string;
  posicion?: number;
  puntos?: number;
  casosEnZona?: number;
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

  login(email: string, password: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/usuario/login`, {
      email: email,
      password: password,
    },
      {responseType: 'text'})
  }


}

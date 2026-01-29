import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type MeDTO = {
  id: number;
  nombreYApellido: string;
  email: string;
  telefono: string;
  barrio: string;
  ciudad: string;
  posicion: number;
  puntos: number;
  casosEnZona: number;
  rol: string;           // "USUARIO", "ADMIN", etc.
  medallas: string[];    // ["BRONCE", ...]
};

export type UpdateMeDTO = Partial<Pick<MeDTO,
  'nombreYApellido' | 'email' | 'telefono' | 'barrio' | 'ciudad'
>> & {
  password?: string; // opcional si tu endpoint lo permite
};

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private http = inject(HttpClient);
  private readonly API = 'http://localhost:8080';

  getMe(): Observable<MeDTO> {
    return this.http.get<MeDTO>(`${this.API}/usuario/me`, {
      withCredentials: true
    });
  }

  updateMe(id: number, payload: UpdateMeDTO): Observable<void> {
    return this.http.put<void>(`${this.API}/usuario/edit/${id}`, payload, {
      withCredentials: true
    });
  }
}

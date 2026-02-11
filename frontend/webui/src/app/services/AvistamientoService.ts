// src/app/services/AvistamientoService.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AvistamientoRequest {
  usuarioId: number;
  mascotaId: number;
  fecha: string;         // ISO (YYYY-MM-DD o YYYY-MM-DDTHH:mm)
  foto?: string | null;  // por ahora string (url/base64), si después querés multipart lo cambiamos
  coordenadas?: string;
  comentario?: string;
}

@Injectable({ providedIn: 'root' })
export class AvistamientoService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  create(payload: AvistamientoRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/avistamiento/create`, payload, {
      withCredentials: true,
    });
  }


  getAll() {
    return this.http.get<any[]>(`${this.baseUrl}/avistamiento/all`, {
      withCredentials: true, // no molesta, aunque no uses jwt todavía
    });
  }

}

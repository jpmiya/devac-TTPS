import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';


export type EstadoMascota = 'PERDIDO_PROPIO' | 'ENCONTRADO' | 'ADOPCION' | string;

export interface UsuarioRef {
  id: number;
  nombre?: string;
  email?: string;
}


//IMPORTANTE esta se usa para el request de crear una mascota porque con el otro
//se traba con los campos

export interface MascotaRequest {
  duenoId: number;
  nombre: string;
  tamanio?: string;          // sin ñ
  color?: string;
  fechaDePerdida: string;    // camelCase
  estado: EstadoMascota;
  foto?: string;
  coordenadas?: string;
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

  register(req: MascotaRequest) {
    const payload: any = {
      duenoId: req.duenoId,
      nombre: req.nombre,
      tamanio: req.tamanio ?? null,
      color: req.color ?? null,
      fechaDePerdida: req.fechaDePerdida,  // <-- clave
      estado: req.estado,
      foto: req.foto ?? null,
      coordenadas: req.coordenadas ?? null,
      descripcion: req.descripcion ?? null,
      tipo: req.tipo ?? null,
      raza: req.raza ?? null,
    };

    return this.http.post<Mascota>(`${this.baseUrl}/mascota/register`, payload);
  }

}

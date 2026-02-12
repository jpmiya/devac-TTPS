export type EstadoMascota = 'PERDIDO_PROPIO' | 'ENCONTRADO' | 'ADOPCION' | string;

export interface Mascota {
  id: number;
  nombre: string;
  tamaño?: string;          // ojo: viene como "tamaño" desde JSON si tu backend lo serializa así
  color?: string;
  fechaDePerdida?: string;  // LocalDate llega como "YYYY-MM-DD"
  fecha_de_perdida?: string;
  estado?: EstadoMascota;
  foto?: string;            // URL
  descripcion?: string;
  tipo?: string;
  raza?: string;

  dueno?: any;
}

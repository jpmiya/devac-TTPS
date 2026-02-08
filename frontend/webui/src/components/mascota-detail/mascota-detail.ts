import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

type MascotaDetail = {
  id: number;
  nombre?: string;
  tipo?: string;
  raza?: string;
  tamanio?: string;
  color?: string;
  fechaDePerdida?: any;
  fecha_de_perdida?: any; // por si tu backend mezcla
  estado?: string;
  coordenadas?: string;
  descripcion?: string;
  fotoUrl?: string;
  dueno?: {
    id?: number;
    nombreYApellido?: string;
    telefono?: string;
    email?: string;
  };
};

@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mascota-detail.component.html',
  styleUrls: ['./mascota-detail.component.css']
})
export class MascotaDetailComponent implements OnInit {
  loading = false;
  error = '';
  mascota: MascotaDetail | null = null;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private location: Location
  ) {}

  goBack(): void {
    this.location.back();
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id || Number.isNaN(id)) {
      this.error = 'ID inválido.';
      return;
    }

    this.loading = true;
    this.http.get<MascotaDetail>(`http://localhost:8080/mascota/${id}`, { withCredentials: true })
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (m) => {
          this.mascota = m;
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'No se pudo cargar el detalle.';
          this.cdr.detectChanges();
        }
      });
  }

  get fecha(): string {
    const m = this.mascota;
    const raw = m?.fechaDePerdida ?? m?.fecha_de_perdida;
    if (!raw) return 'N/A';

    // si viene [yyyy,mm,dd]
    if (Array.isArray(raw) && raw.length >= 3) {
      const [y, mo, d] = raw;
      return `${y}-${String(mo).padStart(2,'0')}-${String(d).padStart(2,'0')}`;
    }
    return String(raw);
  }

  get estadoLabel(): string {
    const e = this.mascota?.estado;
    if (e === 'PERDIDO_PROPIO' || e === 'PERDIDO_AJENO') return 'Perdido';
    if (e === 'RECUPERADO') return 'Recuperado';
    if (e === 'ADOPTADO') return 'Adoptado';
    return e ?? 'N/A';
  }

  get tamanioDisplay(): string {
    const m: any = this.mascota;
    return (m?.tamanio ?? m?.tamaño ?? '').toString().trim() || 'N/A';
  }

}

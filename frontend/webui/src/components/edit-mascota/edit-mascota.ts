import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { MascotaRequest } from '../../app/services/MascotaService';

type MascotaResponse = {
  id: number;
  duenoId?: number;
  nombre?: string;
  tipo?: string;
  raza?: string;
  tamanio?: string;
  color?: string;
  fechaDePerdida?: any;
  estado?: any;
  descripcion?: string;
  fotoUrl?: string;
};

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './edit-mascota.component.html',
  styleUrls: ['./edit-mascota.component.css']
})
export class EditMascotaComponent implements OnInit {
  loading = false;
  loadingUser = true;
  loadingText = 'Cargando...';

  error = '';
  ok = '';

  mascotaId!: number;

  meId: number | null = null;
  isOwner = false;
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  currentFotoUrl: string | null = null;

  // snapshot para reset
  private originalFormJson = '';

  form: MascotaRequest = {
    duenoId: 0,
    nombre: '',
    fechaDePerdida: new Date().toISOString().slice(0, 10),
    estado: 'PERDIDO_PROPIO',
    tipo: 'Perro',
    raza: '',
    tamanio: '',
    color: '',
    descripcion: ''
  };

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private location: Location
  ) {
  }

  goBack(): void {
    this.location.back();
  }

  cancel(): void {
    this.router.navigate(['/lost-dogs']);
  }

  reset(): void {
    if (!this.originalFormJson) return;
    this.form = JSON.parse(this.originalFormJson);
    this.selectedFile = null;
    this.previewUrl = null;
    this.ok = '';
    this.error = '';
    this.cdr.detectChanges();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      const url = URL.createObjectURL(this.selectedFile);
      this.previewUrl = url;
      this.cdr.detectChanges();
    }
  }

  ngOnInit(): void {
    const idStr = this.route.snapshot.paramMap.get('id');
    this.mascotaId = Number(idStr);

    if (!this.mascotaId || Number.isNaN(this.mascotaId)) {
      this.error = 'ID inválido.';
      this.loadingUser = false;
      return;
    }

    // 1) cargar mascota (para mostrar pantalla)
    this.loadMascota();

    // 2) cargar usuario (si está logueado) para saber si es dueño
    this.http.get<any>('http://localhost:8080/usuario/me', {withCredentials: true}).subscribe({
      next: (user) => {
        this.meId = user?.id ?? null;
        this.loadingUser = false;
        this.recomputeOwner();
        this.cdr.detectChanges();
      },
      error: () => {
        // no logueado: puede ver, pero no editar
        this.meId = null;
        this.loadingUser = false;
        this.isOwner = false;
        this.cdr.detectChanges();
      }
    });
  }

  private duenoIdFromMascota: number | null = null;

  private recomputeOwner(): void {
    this.isOwner = !!(this.meId && this.duenoIdFromMascota && this.meId === this.duenoIdFromMascota);
  }

  private loadMascota(): void {
    this.loading = true;
    this.loadingText = 'Cargando aviso...';
    this.error = '';
    this.ok = '';
    this.cdr.detectChanges();

    this.http.get<MascotaResponse>(`http://localhost:8080/mascota/${this.mascotaId}`, {withCredentials: true})
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (m) => {
          const fecha = normalizeDate(m.fechaDePerdida) ?? new Date().toISOString().slice(0, 10);

          // dueño para check de owner
          this.duenoIdFromMascota = typeof m.duenoId === 'number' ? m.duenoId : null;
          this.recomputeOwner();

          // estado normalizado a string (por si viene raro)
          const estadoStr = normalizeEnum(m.estado) ?? 'PERDIDO_PROPIO';

          // rellenar form
          this.form.duenoId = this.duenoIdFromMascota ?? 0;
          this.form.nombre = m.nombre ?? '';
          this.form.tipo = (m.tipo as any) ?? 'Perro';
          this.form.raza = m.raza ?? '';
          this.form.tamanio = (m.tamanio as any) ?? '';
          this.form.color = m.color ?? '';
          this.form.fechaDePerdida = fecha;
          this.form.estado = (m.estado as any) ?? 'PERDIDO_PROPIO';
          this.form.descripcion = m.descripcion ?? '';

          this.currentFotoUrl = m.fotoUrl ?? null;
          this.originalFormJson = JSON.stringify(this.form);
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'No se pudo cargar la mascota.';
          this.cdr.detectChanges();
        }
      });
  }

  submit(): void {
    if (!this.isOwner) {
      this.error = 'No sos el dueño de esta mascota.';
      return;
    }

    this.loading = true;
    this.loadingText = 'Guardando cambios...';
    this.error = '';
    this.ok = '';
    this.cdr.detectChanges();

    const formData = new FormData();

    // ✅ CLAVE: Blob, NO File
    const mascotaJson = JSON.stringify(this.form);
    const mascotaBlob = new Blob(
      [mascotaJson],
      {type: 'application/json'}
    );

    formData.append('mascota', mascotaBlob, 'mascota.json');


    if (this.selectedFile) {
      formData.append('foto', this.selectedFile);
    }

    this.http.put(
      `http://localhost:8080/mascota/${this.mascotaId}`,
      formData,
      {withCredentials: true}
    )
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          this.ok = 'Cambios guardados!';
          this.originalFormJson = JSON.stringify(this.form);
          this.cdr.detectChanges();
          setTimeout(() => this.router.navigate(['/lost-dogs']), 1200);
        },
        error: (err) => {
          const msg =
            typeof err?.error === 'string'
              ? err.error
              : err?.error?.message ?? 'Error inesperado';

          this.error = `Error (${err.status}): ${msg}`;
          console.error(err);
          this.cdr.detectChanges();
        }
      });
  }
}

  function normalizeDate(d: any): string | null {
  if (!d) return null;
  if (Array.isArray(d) && d.length >= 3) {
    const [y, m, day] = d;
    return `${y}-${String(m).padStart(2,'0')}-${String(day).padStart(2,'0')}`;
  }
  return String(d);
}

// Normaliza enum a string "LIKE_THIS"
function normalizeEnum(v: any): string | null {
  if (v == null) return null;
  if (typeof v === 'string') return v.trim();
  // si viene como objeto {name: "..."} o algo raro
  if (typeof v === 'object') {
    if (typeof v.name === 'string') return v.name.trim();
    if (typeof v.value === 'string') return v.value.trim();
  }
  return String(v).trim();
}

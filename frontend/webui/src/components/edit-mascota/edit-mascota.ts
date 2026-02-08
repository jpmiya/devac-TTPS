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
  coordenadas?: string;
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
    coordenadas: '',
    descripcion: ''
  };

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private location: Location
  ) {}

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
      this.redirectWithError('ID de mascota inválido.');
      return;
    }

    // 1) verificar sesión
    this.http.get<any>('http://localhost:8080/usuario/me', { withCredentials: true }).subscribe({
      next: (user) => {
        this.meId = user.id;
        this.loadingUser = false;
        this.cdr.detectChanges();
        // 2) cargar mascota y verificar ownership
        this.loadMascotaAndCheckOwner();
      },
      error: () => {
        this.redirectWithError('Tenés que estar logueado para editar una mascota.');
      }
    });
  }

  private redirectWithError(msg: string): void {
    // Guardamos el mensaje en sessionStorage para mostrarlo en home (opcional)
    alert(msg);
    this.router.navigate(['/']);
  }


  private duenoIdFromMascota: number | null = null;

  private recomputeOwner(): void {
    this.isOwner = !!(this.meId && this.duenoIdFromMascota && this.meId === this.duenoIdFromMascota);
  }

  private loadMascotaAndCheckOwner(): void {
    this.loading = true;
    this.loadingText = 'Cargando aviso...';
    this.error = '';
    this.ok = '';
    this.cdr.detectChanges();

    this.http.get<MascotaResponse>(`http://localhost:8080/mascota/${this.mascotaId}`, { withCredentials: true })
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (m) => {
          // Verificar ownership ANTES de mostrar el formulario
          this.duenoIdFromMascota = typeof m.duenoId === 'number' ? m.duenoId : null;
          this.recomputeOwner();

          if (!this.isOwner) {
            this.redirectWithError('No tenés permiso para editar esta mascota.');
            return;
          }

          const fecha = normalizeDate(m.fechaDePerdida) ?? new Date().toISOString().slice(0, 10);

          this.form.duenoId = this.duenoIdFromMascota ?? 0;
          this.form.nombre = m.nombre ?? '';
          this.form.tipo = (m.tipo as any) ?? 'Perro';
          this.form.raza = m.raza ?? '';
          this.form.tamanio = (m.tamanio as any) ?? '';
          this.form.color = m.color ?? '';
          this.form.fechaDePerdida = fecha;
          this.form.estado = (m.estado as any) ?? 'PERDIDO_PROPIO';
          this.form.coordenadas = m.coordenadas ?? '';
          this.form.descripcion = m.descripcion ?? '';

          this.currentFotoUrl = m.fotoUrl ?? null;
          this.originalFormJson = JSON.stringify(this.form);
          this.cdr.detectChanges();
        },
        error: (err) => {
          const status = err?.status;
          if (status === 404) {
            this.redirectWithError('La mascota no existe.');
          } else if (status === 403) {
            this.redirectWithError('No tenés permiso para editar esta mascota.');
          } else {
            this.redirectWithError('No se pudo cargar la mascota.');
          }
        }
      });
  }

  /** @deprecated — reemplazado por loadMascotaAndCheckOwner */
  private loadMascota(): void { this.loadMascotaAndCheckOwner(); }


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

    const mascotaJson = JSON.stringify(this.form);
    const mascotaBlob = new Blob([mascotaJson], { type: 'application/json' });
    formData.append('mascota', mascotaBlob);

    if (this.selectedFile) {
      formData.append('foto', this.selectedFile, this.selectedFile.name);
    }

    // Ajustá método/URL según tu backend:
    // si tenés PUT /mascota/{id}
    this.http.put<any>(`http://localhost:8080/mascota/${this.mascotaId}`, formData, { withCredentials: true })
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
        error: () => {
          this.error = 'No se pudieron guardar los cambios.';
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

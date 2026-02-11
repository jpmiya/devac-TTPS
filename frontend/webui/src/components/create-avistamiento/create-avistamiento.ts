// create-avistamiento.component.ts
import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import { AvistamientoService, AvistamientoRequest } from '../../app/services/AvistamientoService';
import { environment } from '../../environments/environment';

type MascotaLite = { id: number; nombre?: string; estado?: string };

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-avistamiento.component.html',
  styleUrls: ['./create-avistamiento.component.css'],
})
export class CreateAvistamientoComponent implements OnInit {
  private http = inject(HttpClient);
  private avistamientoSvc = inject(AvistamientoService);
  private cdr = inject(ChangeDetectorRef);
  private location = inject(Location);
  private router = inject(Router);

  loading = true;
  saving = false;
  loadingText = 'Cargando…';

  error = '';
  ok = '';

  mascotas: MascotaLite[] = [];
  private meId: number | null = null;

  // snapshot
  private originalJson = '';

  form: AvistamientoRequest = {
    usuarioId: 0,
    mascotaId: 0,
    fecha: new Date().toISOString().slice(0, 10),
    foto: '',
    coordenadas: '',
    comentario: '',
  };

  ngOnInit(): void {
    this.load();
  }

  goBack(): void {
    this.location.back();
  }

  reset(): void {
    if (!this.originalJson) return;
    this.form = JSON.parse(this.originalJson);
    this.error = '';
    this.ok = '';
    this.cdr.detectChanges();
    this.selectedFile = null;

  }

  private load(): void {
    this.loading = true;
    this.error = '';
    this.ok = '';
    this.loadingText = 'Cargando usuario…';
    this.cdr.detectChanges();

    // 1) /usuario/me -> meId
    this.http.get<any>(`${environment.apiUrl}/usuario/me`, { withCredentials: true })
      .subscribe({
        next: (me) => {
          this.meId = me?.id ?? null;
          if (!this.meId) {
            this.error = 'No estás logueado.';
            this.loading = false;
            this.cdr.detectChanges();
            return;
          }

          this.form.usuarioId = this.meId;

          // 2) cargar mascotas perdidas para seleccionar
          this.loadingText = 'Cargando mascotas…';
          this.cdr.detectChanges();

          this.http.get<any[]>(`${environment.apiUrl}/mascota/findAllLost`, { withCredentials: true })
            .pipe(finalize(() => {
              this.loading = false;
              this.originalJson = JSON.stringify(this.form);
              this.cdr.detectChanges();
            }))
            .subscribe({
              next: (list) => {
                this.mascotas = (list ?? []).map(m => ({
                  id: m.id,
                  nombre: m.nombre,
                  estado: m.estado
                }));
                this.cdr.detectChanges();
              },
              error: () => {
                this.error = 'No pude cargar las mascotas.';
                this.cdr.detectChanges();
              }
            });
        },
        error: () => {
          this.error = 'No estás logueado (401).';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  submit(): void {
    this.error = '';
    this.ok = '';

    if (!this.meId) { this.error = 'No estás logueado.'; return; }
    if (!this.form.mascotaId || this.form.mascotaId === 0) { this.error = 'Seleccioná una mascota.'; return; }

    this.saving = true;
    this.cdr.detectChanges();

    const av = {
      usuarioId: this.meId, // después con JWT lo sacás
      mascotaId: this.form.mascotaId,
      fecha: this.form.fecha,
      coordenadas: (this.form.coordenadas ?? '').trim(),
      comentario: (this.form.comentario ?? '').trim(),
    };

    const formData = new FormData();
    const blob = new Blob([JSON.stringify(av)], { type: 'application/json' });
    formData.append('avistamiento', blob, 'avistamiento.json');

    if (this.selectedFile) {
      formData.append('foto', this.selectedFile);
    }

    this.http.post(`${environment.apiUrl}/avistamiento/create`, formData, { withCredentials: true })
      .pipe(finalize(() => {
        this.saving = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          this.ok = 'Avistamiento creado ✅';
          this.originalJson = JSON.stringify(this.form);
          this.selectedFile = null;
          this.cdr.detectChanges();
          setTimeout(() => this.router.navigate(['/lost-dogs']), 900);
        },
        error: (err) => {
          const msg =
            typeof err?.error === 'string'
              ? err.error
              : err?.error?.error ?? err?.error?.message ?? 'Error inesperado';

          this.error = `Error (${err.status}): ${msg}`;
          console.error(err);
          this.cdr.detectChanges();
        }
      });
  }


  selectedFile: File | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
    this.cdr.detectChanges();
  }

}

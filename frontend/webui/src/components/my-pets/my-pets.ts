import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { UsuarioService, Usuario } from '../../app/services/UsuarioService';
import { Mascota } from '../../app/services/MascotaService';

@Component({
  selector: 'app-my-pets',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-pets.component.html',
  styleUrls: ['./my-pets.component.css'],
})
export class MyPetsComponent implements OnInit {
  private usuarioSvc = inject(UsuarioService);
  private location = inject(Location);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  me: Usuario | null = null;
  mascotas: Mascota[] = [];

  loading = false;
  error: string | null = null;

  ngOnInit(): void {
    this.load();
  }

  goBack(): void {
    this.location.back();
  }

  private load(): void {
    this.loading = true;
    this.error = null;
    this.mascotas = [];
    this.cdr.detectChanges(); // fuerza render del overlay

    // 1) primero traigo /usuario/me
    this.usuarioSvc.getMe()
      .pipe(finalize(() => {
        // OJO: no apagamos loading acá todavía, porque falta cargar mascotas.
        // Igual forzamos render por si cambia algo.
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (me) => {
          this.me = me;
          this.cdr.detectChanges();

          // 2) con el id traigo /usuario/{id}/mascotas
          this.usuarioSvc.getMascotasDeUsuario(me.id)
            .pipe(finalize(() => {
              this.loading = false;
              this.cdr.detectChanges(); // fuerza render al terminar
            }))
            .subscribe({
              next: (list) => {
                this.mascotas = list ?? [];
                this.cdr.detectChanges();
              },
              error: (e) => {
                this.error = 'No pude cargar tus mascotas.';
                this.cdr.detectChanges();
              }
            });
        },
        error: (e) => {
          this.loading = false;
          this.error = 'No estás logueado (401).';
          this.cdr.detectChanges();

          // Si querés, redirigí a login:
          // setTimeout(() => this.router.navigate(['/login']), 1200);
        }
      });
  }

  editPet(id: number | undefined): void {
    if (!id) return;
    this.router.navigate(['/mascota', id, 'edit']);
  }

  deletePet(id: number | undefined): void {
    if (!id) return;

    const ok = confirm('¿Seguro que querés borrar esta mascota? Esta acción no se puede deshacer.');
    if (!ok) return;

    this.loading = true;
    this.error = null;
    this.cdr.detectChanges();

    this.usuarioSvc.deleteMascota(id)
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          // sacarla de la lista sin recargar todo
          this.mascotas = this.mascotas.filter(m => m.id !== id);
          this.cdr.detectChanges();
        },
        error: (e) => {
          const msg =
            typeof e?.error === 'string'
              ? e.error
              : e?.error?.message ?? 'Error inesperado';
          this.error = `No pude borrar la mascota. (${e.status}) ${msg}`;
          this.cdr.detectChanges();
        }
      });
  }


}

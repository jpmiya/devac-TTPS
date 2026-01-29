import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { MascotasService, MascotaRequest } from '../../app/services/MascotaService';
import { finalize } from 'rxjs/operators';


@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './create-mascota.component.html',
  styleUrls: ['./create-mascota.component.css']
})
export class CreateMascotaComponent implements OnInit {
  loading = false;
  loadingUser = true;
  error = '';
  ok = '';

  form: MascotaRequest = {
    duenoId: 0,
    nombre: '',
    fechaDePerdida: new Date().toISOString().slice(0,10),
    estado: 'PERDIDO_PROPIO',
    tipo: 'Perro',
  };

  constructor(
    private mascotasService: MascotasService,
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Obtener el usuario actual para sacar el duenoId
    this.http.get<any>('http://localhost:8080/usuario/me').subscribe({
      next: (user) => {
        this.form.duenoId = user.id;
        this.loadingUser = false;
        console.log('Usuario actual:', user);
        this.cdr.detectChanges();
      },
      error: (e) => {
        console.error('Error obteniendo usuario:', e);
        this.error = 'Debes estar logeado para crear un aviso';
        this.loadingUser = false;
        this.cdr.detectChanges();
        // Redirigir al login después de 2 segundos
        setTimeout(() => this.router.navigate(['/login']), 2000);
      }
    });
  }

  submit() {
    console.log('submit() -> duenoId:', this.form.duenoId);
    console.log('submit() -> form completo:', this.form);
    this.loading = true;
    this.error = '';
    this.ok = '';
    this.cdr.detectChanges();

    this.mascotasService.register(this.form)
      .pipe(finalize(() => {
        console.log('finalize() -> loading false');
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (res) => {
          console.log('next()', res);
          this.ok = 'Aviso creado exitosamente!';
          this.cdr.detectChanges();
          // Redirigir a lost-dogs después de 2 segundos
          setTimeout(() => this.router.navigate(['/lost-dogs']), 2000);
        },
        error: (e) => {
          console.log('error()', e);
          this.error = 'Falló el registro. Por favor intenta nuevamente.';
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

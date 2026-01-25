import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MascotasService, MascotaRequest } from '../../app/services/MascotaService';
import { finalize } from 'rxjs/operators';


@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-mascota.component.html',
  styleUrls: ['./create-mascota.component.css']
})
export class CreateMascotaComponent {
  loading = false;
  error = '';
  ok = '';

  form: MascotaRequest = {
    duenoId: 1,
    nombre: '',
    fechaDePerdida: new Date().toISOString().slice(0,10),
    estado: 'PERDIDO_PROPIO',
    tipo: 'Perro',
  };

  constructor(
    private mascotasService: MascotasService,
    private cdr: ChangeDetectorRef
  ) {}

  submit() {
    console.log('submit() -> loading true');
    this.loading = true;
    this.error = '';
    this.ok = '';
    this.cdr.detectChanges(); // <- CLAVE

    this.mascotasService.register(this.form)
      .pipe(finalize(() => {
        console.log('finalize() -> loading false');
        this.loading = false;
        this.cdr.detectChanges(); // <- CLAVE
      }))
      .subscribe({
        next: (res) => {
          console.log('next()', res);
          this.ok = 'Aviso creado.';
          this.cdr.detectChanges(); // <- CLAVE
        },
        error: (e) => {
          console.log('error()', e);
          this.error = 'Falló el registro.';
          this.cdr.detectChanges(); // <- CLAVE
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

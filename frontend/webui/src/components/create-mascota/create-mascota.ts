import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
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
  selectedFile: File | null = null;

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
    private cdr: ChangeDetectorRef,
    private location: Location
  ) {}

  goBack(): void {
    this.location.back();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  ngOnInit(): void {
    // Obtener el usuario actual para sacar el duenoId
    this.http.get<any>('http://localhost:8080/usuario/me', { withCredentials: true })
      .subscribe({
        next: (user) => {
          this.form.duenoId = user.id;
          this.loadingUser = false;
          this.cdr.detectChanges();
        },
      error: (e) => {
        this.error = 'Debes estar logeado para crear un aviso';
        this.loadingUser = false;
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/login']), 2000);
      }
    });
  }

  submit() {
    this.loading = true;
    this.error = '';
    this.ok = '';
    this.cdr.detectChanges();

    // Crear FormData con la estructura que espera el backend
    const formData = new FormData();

    // El backend espera un campo "mascota" con el JSON completo
    const mascotaJson = JSON.stringify(this.form);
    const mascotaBlob = new Blob([mascotaJson], { type: 'application/json' });
    formData.append('mascota', mascotaBlob, 'mascota.json');

    // Agregar la foto si fue seleccionada
    if (this.selectedFile) {
      formData.append('foto', this.selectedFile, this.selectedFile.name);
    }

    // Enviar directamente con HttpClient
    this.http.post<any>('http://localhost:8080/mascota/register', formData, { withCredentials: true })
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (res) => {
          this.ok = 'Aviso creado exitosamente!';
          this.cdr.detectChanges();
          setTimeout(() => this.router.navigate(['/lost-dogs']), 2000);
        },
        error: (e) => {
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

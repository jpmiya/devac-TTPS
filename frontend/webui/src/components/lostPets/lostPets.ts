import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { MascotasService, Mascota, UsuarioRef, EstadoMascota} from '../../app/services/MascotaService';








@Component({
  selector: 'app-lost-dogs',
  templateUrl: './lost-dogs.component.html',
  styleUrls: ['./lost-dogs.component.css'],
  standalone: true,
  imports: [CommonModule, RouterLink]
})
export class LostDogsComponent implements OnInit {
  dogs: Mascota[] = [];
  loading = false;
  error = '';
  isLoggedIn = false;

  constructor(
    private http: HttpClient,
    private mascotasService: MascotasService,
    private cdr: ChangeDetectorRef
  ) {}


  ngOnInit(): void {
    // Verificar si el usuario está logeado
    this.http.get('http://localhost:8080/usuario/me').subscribe({
      next: () => {
        this.isLoggedIn = true;
        this.cdr.detectChanges();
      },
      error: (e) => {
        this.isLoggedIn = false;
      }
    });

    this.loading = true;
    this.error = '';
    this.cdr.detectChanges(); // fuerza render del overlay

    this.mascotasService.findAllLost()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges(); // fuerza render al terminar
      }))
      .subscribe({
        next: (data) => {
          this.dogs = data ?? [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.error = 'No se pudo cargar la lista.';
          this.cdr.detectChanges();
        }
      });
  }


  get missingDogsCount(): number {
    return this.dogs.filter(d => d.estado === 'PERDIDO_PROPIO').length;
  }

  get foundDogsCount(): number {
    return this.dogs.filter(d => d.estado !== 'PERDIDO_PROPIO').length;
  }

  onImageError(event: Event, dog: any): void {
    (event.target as HTMLImageElement).src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200"%3E%3Crect fill="%23ddd" width="200" height="200"/%3E%3Ctext fill="%23999" x="50%25" y="50%25" text-anchor="middle" dy=".3em"%3EError%3C/text%3E%3C/svg%3E';
  }
}



import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { MascotasService, Mascota, UsuarioRef, EstadoMascota} from '../../app/services/MascotaService';








@Component({
  selector: 'app-lost-dogs',
  templateUrl: './lost-dogs.component.html',
  styleUrls: ['./lost-dogs.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class LostDogsComponent implements OnInit {
  dogs: Mascota[] = [];
  loading = false;
  error = '';

  constructor(
    private mascotasService: MascotasService,
    private cdr: ChangeDetectorRef
  ) {}


  ngOnInit(): void {
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
          console.log('findAllLost data:', data);
          this.dogs = data ?? [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('findAllLost error:', err);
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
}



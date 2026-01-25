import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
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

  constructor(private mascotasService: MascotasService) {}

  ngOnInit(): void {
    this.loading = true;

    this.mascotasService.findAllLost().subscribe({
      next: (data) => {
        this.dogs = data ?? [];
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudo cargar la lista.';
        this.loading = false;
      }
    });

    this.loading = false;
  }

  get missingDogsCount(): number {
    return this.dogs.filter(d => d.estado === 'PERDIDO_PROPIO').length;
  }

  get foundDogsCount(): number {
    return this.dogs.filter(d => d.estado !== 'PERDIDO_PROPIO').length;
  }
}



import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { MascotasService, Mascota } from '../../app/services/MascotaService';

@Component({
  selector: 'app-adoption-dogs',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './adoption-dogs.component.html',
  styleUrls: ['./adoption-dogs.component.css']
})
export class AdoptionDogsComponent implements OnInit {
  pets: Mascota[] = [];
  loading = false;
  error = '';

  private mascotasService = inject(MascotasService);
  private cdr = inject(ChangeDetectorRef);
  private location = inject(Location);

  ngOnInit(): void {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.mascotasService.findAllAdopted()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (data) => {
          this.pets = data ?? [];
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'No se pudo cargar la lista de adopción.';
          this.cdr.detectChanges();
        }
      });
  }

  goBack(): void {
    this.location.back();
  }

  get locationLabel(): string {
    return 'Barrio';
  }

  getLocation(pet: Mascota): string {
    const owner: any = pet.dueno as any;
    return owner?.barrio || (pet as any)?.barrioDueno || (pet as any)?.barrio || 'Barrio desconocido';
  }

  getPhone(pet: Mascota): string {
    const owner: any = pet.dueno as any;
    return owner?.telefono || (pet as any)?.telefonoDueno || 'Contacto no disponible';
  }
}

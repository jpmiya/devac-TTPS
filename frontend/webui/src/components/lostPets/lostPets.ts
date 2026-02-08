import { Component, OnInit, ChangeDetectorRef, inject, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { MascotasService, Mascota, UsuarioRef, EstadoMascota} from '../../app/services/MascotaService';
import { AuthService } from '../../app/services/AuthService';
import * as L from 'leaflet';

// Fix Leaflet default marker icon paths
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl:       'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl:     'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});








@Component({
  selector: 'app-lost-dogs',
  templateUrl: './lost-dogs.component.html',
  styleUrls: ['./lost-dogs.component.css'],
  standalone: true,
  imports: [CommonModule, RouterLink]
})
export class LostDogsComponent implements OnInit {
  @ViewChild('lostDogsMap') mapEl!: ElementRef<HTMLDivElement>;

  dogs: Mascota[] = [];
  loading = false;
  error = '';
  showMap = false;
  auth = inject(AuthService);
  private location = inject(Location);
  private map: L.Map | null = null;

  goBack(): void {
    this.location.back();
  }

  constructor(
    private http: HttpClient,
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
          this.dogs = data ?? [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.error = 'No se pudo cargar la lista.';
          this.cdr.detectChanges();
        }
      });
  }


  toggleMap(): void {
    this.showMap = !this.showMap;
    this.cdr.detectChanges();

    if (this.showMap) {
      setTimeout(() => this.buildMap(), 100);
    } else if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private buildMap(): void {
    if (!this.mapEl) return;
    if (this.map) {
      this.map.remove();
      this.map = null;
    }

    this.map = L.map(this.mapEl.nativeElement).setView([-34.9215, -57.9545], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19,
    }).addTo(this.map);

    const bounds: L.LatLng[] = [];

    for (const dog of this.dogs) {
      const coords = dog.dueno?.coordenadas;
      if (!coords) continue;

      const parts = coords.split(',');
      if (parts.length !== 2) continue;

      const lat = parseFloat(parts[0].trim());
      const lon = parseFloat(parts[1].trim());
      if (isNaN(lat) || isNaN(lon)) continue;

      const latlng = L.latLng(lat, lon);
      bounds.push(latlng);

      const popup = `
        <strong>${dog.nombre || 'Sin nombre'}</strong><br>
        ${dog.dueno?.barrio || ''} ${dog.dueno?.ciudad || ''}<br>
        <em>${dog.estado || ''}</em>
      `;

      L.marker(latlng).addTo(this.map!).bindPopup(popup);
    }

    if (bounds.length > 0) {
      this.map.fitBounds(L.latLngBounds(bounds), { padding: [40, 40], maxZoom: 14 });
    }
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



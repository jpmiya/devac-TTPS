import {
  Component, Input, Output, EventEmitter,
  AfterViewInit, OnDestroy, ElementRef, ViewChild, ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

// Fix Leaflet default marker icon paths (webpack breaks them)
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl:       'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl:     'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

export interface LatLon {
  lat: number;
  lon: number;
}

@Component({
  selector: 'app-location-picker',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="map-wrapper">
      <div class="map-toolbar">
        <button type="button" class="btn-geo" (click)="geolocate()" [disabled]="locating">
          {{ locating ? '⏳ Obteniendo...' : '📍 Usar mi ubicación' }}
        </button>
        <span class="coords-label" *ngIf="selectedCoords">
          {{ selectedCoords.lat.toFixed(6) }}, {{ selectedCoords.lon.toFixed(6) }}
        </span>
      </div>
      <div #mapContainer class="map-container"></div>
    </div>
  `,
  styles: [`
    .map-wrapper {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .map-toolbar {
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
    }

    .btn-geo {
      background: var(--gradient-brand, linear-gradient(135deg, #667eea, #764ba2));
      color: white;
      border: none;
      padding: 8px 16px;
      border-radius: var(--radius-sm, 10px);
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      transition: opacity 0.2s;
    }

    .btn-geo:hover:not(:disabled) {
      opacity: 0.9;
    }

    .btn-geo:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .coords-label {
      font-size: 12px;
      color: #6b7280;
      font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
    }

    .map-container {
      width: 100%;
      height: 300px;
      border-radius: var(--radius-sm, 10px);
      border: 1px solid rgba(17, 24, 39, 0.12);
      overflow: hidden;
      z-index: 0;
    }
  `]
})
export class LocationPickerComponent implements AfterViewInit, OnDestroy {
  @ViewChild('mapContainer') mapEl!: ElementRef<HTMLDivElement>;

  /** Coordenadas iniciales (si ya tiene ubicación guardada) */
  @Input() initialCoords: LatLon | null = null;

  /** Zoom inicial */
  @Input() zoom = 13;

  /** Si es readonly (solo muestra, no deja clickear) */
  @Input() readonly = false;

  /** Emite cada vez que se selecciona una ubicación */
  @Output() locationSelected = new EventEmitter<LatLon>();

  /** Emite el string "lat,lon" listo para mandar al backend */
  @Output() coordsString = new EventEmitter<string>();

  selectedCoords: LatLon | null = null;
  locating = false;

  private map!: L.Map;
  private marker: L.Marker | null = null;

  constructor(private cdr: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    const center: L.LatLngExpression = this.initialCoords
      ? [this.initialCoords.lat, this.initialCoords.lon]
      : [-34.9215, -57.9545]; // default: La Plata

    this.map = L.map(this.mapEl.nativeElement).setView(center, this.zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19,
    }).addTo(this.map);

    // Si ya hay coordenadas iniciales, poner marker
    if (this.initialCoords) {
      this.placeMarker(this.initialCoords.lat, this.initialCoords.lon);
    }

    // Click para seleccionar ubicación
    if (!this.readonly) {
      this.map.on('click', (e: L.LeafletMouseEvent) => {
        this.placeMarker(e.latlng.lat, e.latlng.lng);
        this.emitCoords(e.latlng.lat, e.latlng.lng);
      });
    }

    // Fix render issues when map is in a hidden/dynamic container
    setTimeout(() => this.map.invalidateSize(), 200);
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  geolocate(): void {
    if (!navigator.geolocation) {
      alert('Tu navegador no soporta geolocalización.');
      return;
    }

    this.locating = true;
    this.cdr.detectChanges();

    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const lat = pos.coords.latitude;
        const lon = pos.coords.longitude;

        this.map.setView([lat, lon], 15);
        this.placeMarker(lat, lon);
        this.emitCoords(lat, lon);

        this.locating = false;
        this.cdr.detectChanges();
      },
      (err) => {
        alert('No se pudo obtener tu ubicación: ' + err.message);
        this.locating = false;
        this.cdr.detectChanges();
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  }

  private placeMarker(lat: number, lon: number): void {
    if (this.marker) {
      this.marker.setLatLng([lat, lon]);
    } else {
      this.marker = L.marker([lat, lon]).addTo(this.map);
    }
    this.selectedCoords = { lat, lon };
  }

  private emitCoords(lat: number, lon: number): void {
    this.locationSelected.emit({ lat, lon });
    this.coordsString.emit(`${lat},${lon}`);
  }
}

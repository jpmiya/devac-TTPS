import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { finalize, filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { MascotasService, Mascota, UsuarioRef, EstadoMascota} from '../../app/services/MascotaService';
import { AuthService } from '../../app/services/AuthService';
import { environment } from '../../environments/environment';
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
  imports: [CommonModule, RouterLink, FormsModule]
})
export class LostDogsComponent implements OnInit, OnDestroy {
  @ViewChild('lostDogsMap') mapEl!: ElementRef<HTMLDivElement>;

  dogs: Mascota[] = [];
  loading = false;
  error = '';
  showMap = false;
  
  // Paginación
  currentPage = 1;
  itemsPerPage = 12;
  
  // Filtros
  searchText = '';
  filterType: 'all' | 'today' | 'week' | 'found' | 'missing' = 'all';
  
  auth = inject(AuthService);
  private location = inject(Location);
  private router = inject(Router);
  private map: L.Map | null = null;
  private routerSubscription?: Subscription;

  goBack(): void {
    this.location.back();
  }

  constructor(
    private http: HttpClient,
    private mascotasService: MascotasService,
    private cdr: ChangeDetectorRef
  ) {}


  ngOnInit(): void {
    // Cargar datos inicialmente
    this.loadDogs();

    // Recargar cuando volvemos a esta ruta (ej: después de editar)
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        if (event.url === '/lost-dogs' || event.url.startsWith('/lost-dogs')) {
          this.currentPage = 1; // Reset a la primera página
          this.loadDogs();
        }
      });
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private loadDogs(): void {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges(); // fuerza render del overlay

    // Usar findAll() para obtener TODAS las mascotas (perdidas + recuperadas)
    this.mascotasService.findAll()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges(); // fuerza render al terminar
      }))
      .subscribe({
        next: (data) => {
          this.dogs = data ?? [];
          this.hydrateOwnersFromDetail();
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

  private hydrateOwnersFromDetail(): void {
    for (const dog of this.dogs as any[]) {
      if (dog?.dueno || !dog?.id) continue;

      this.http.get<any>(`${environment.apiUrl}/mascota/${dog.id}`, { withCredentials: true })
        .subscribe({
          next: (detail) => {
            if (detail?.dueno) {
              dog.dueno = detail.dueno;
              this.cdr.detectChanges();
            }
          },
          error: () => {
            // si falla detalle, dejamos fallback actual
          }
        });
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

    // Iconos personalizados para perdidos y encontrados
    const lostIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    const foundIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    const bounds: L.LatLng[] = [];

    // Usar filteredDogs para que el mapa respete los filtros activos
    for (const dog of this.filteredDogs) {
      // Priorizar coordenadas de la mascota, si no están usar las del dueño
      const coords = dog.coordenadas || dog.dueno?.coordenadas;
      if (!coords) continue;

      const parts = coords.split(',');
      if (parts.length !== 2) continue;

      const lat = parseFloat(parts[0].trim());
      const lon = parseFloat(parts[1].trim());
      if (isNaN(lat) || isNaN(lon)) continue;

      const latlng = L.latLng(lat, lon);
      bounds.push(latlng);

      const isMissing = this.isMissing(dog);
      const statusText = this.getStatusLabel(dog);
      
      // Popup mejorado con más información
      const popup = `
        <div style="min-width: 200px;">
          <h3 style="margin: 0 0 8px 0; color: ${isMissing ? '#d32f2f' : '#388e3c'};">
            ${dog.nombre || 'Sin nombre'}
          </h3>
          <p style="margin: 4px 0;"><strong>Estado:</strong> ${statusText}</p>
          ${dog.raza ? `<p style="margin: 4px 0;"><strong>Raza:</strong> ${dog.raza}</p>` : ''}
          ${dog.color ? `<p style="margin: 4px 0;"><strong>Color:</strong> ${dog.color}</p>` : ''}
          ${dog.fecha_de_perdida ? `<p style="margin: 4px 0;"><strong>Fecha:</strong> ${dog.fecha_de_perdida}</p>` : ''}
          <p style="margin: 4px 0;"><strong>Zona:</strong> ${this.getCityLabel(dog)}</p>
          ${dog.descripcion ? `<p style="margin: 8px 0 4px 0; font-size: 0.9em; color: #666;">${dog.descripcion.substring(0, 100)}${dog.descripcion.length > 100 ? '...' : ''}</p>` : ''}
          <a href="/mascota/${dog.id}" style="display: inline-block; margin-top: 8px; padding: 6px 12px; background: #1976d2; color: white; text-decoration: none; border-radius: 4px; font-size: 0.9em;">
            Ver detalles
          </a>
        </div>
      `;

      const marker = L.marker(latlng, { 
        icon: isMissing ? lostIcon : foundIcon 
      }).addTo(this.map!);
      
      marker.bindPopup(popup, { maxWidth: 300 });
    }

    if (bounds.length > 0) {
      this.map.fitBounds(L.latLngBounds(bounds), { padding: [40, 40], maxZoom: 14 });
    } else {
      // Si no hay marcadores, centrar en La Plata
      this.map.setView([-34.9215, -57.9545], 12);
    }
  }

  get missingDogsCount(): number {
    return this.dogs.filter(d => 
      d.estado === 'PERDIDO_PROPIO' || 
      d.estado === 'PERDIDO'
    ).length;
  }

  get foundDogsCount(): number {
    return this.dogs.filter(d => 
      d.estado === 'RECUPERADO' || 
      d.estado === 'ADOPCION'
    ).length;
  }

  // Filtros
  get filteredDogs(): Mascota[] {
    let result = [...this.dogs];

    // Filtro por texto de búsqueda
    if (this.searchText.trim()) {
      const search = this.searchText.toLowerCase();
      result = result.filter(dog => {
        const nombre = dog.nombre?.toLowerCase() || '';
        const raza = dog.raza?.toLowerCase() || '';
        const barrio = this.getCityLabel(dog).toLowerCase();
        const descripcion = dog.descripcion?.toLowerCase() || '';
        
        return nombre.includes(search) || 
               raza.includes(search) || 
               barrio.includes(search) ||
               descripcion.includes(search);
      });
    }

    // Filtro por tipo
    if (this.filterType === 'found') {
      result = result.filter(d => d.estado === 'RECUPERADO' || d.estado === 'ADOPCION');
    } else if (this.filterType === 'missing') {
      result = result.filter(d => this.isMissing(d));
    } else if (this.filterType === 'today') {
      const today = new Date().toISOString().slice(0, 10);
      result = result.filter(d => d.fecha_de_perdida === today);
    } else if (this.filterType === 'week') {
      const weekAgo = new Date();
      weekAgo.setDate(weekAgo.getDate() - 7);
      const weekAgoStr = weekAgo.toISOString().slice(0, 10);
      result = result.filter(d => (d.fecha_de_perdida || '') >= weekAgoStr);
    }

    return result;
  }

  onSearchChange(text: string): void {
    this.searchText = text;
    this.currentPage = 1; // Reset a la primera página al buscar
    this.updateMapIfVisible();
    this.cdr.detectChanges();
  }

  onFilterChange(filter: string): void {
    this.filterType = filter as any;
    this.currentPage = 1; // Reset a la primera página al filtrar
    this.updateMapIfVisible();
    this.cdr.detectChanges();
  }

  clearFilters(): void {
    this.searchText = '';
    this.filterType = 'all';
    this.currentPage = 1;
    this.updateMapIfVisible();
    this.cdr.detectChanges();
  }

  private updateMapIfVisible(): void {
    if (this.showMap && this.map) {
      setTimeout(() => this.buildMap(), 50);
    }
  }

  // Paginación
  get totalPages(): number {
    return Math.ceil(this.filteredDogs.length / this.itemsPerPage);
  }

  get paginatedDogs(): Mascota[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredDogs.slice(start, end);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.cdr.detectChanges();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.cdr.detectChanges();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  get canGoPrev(): boolean {
    return this.currentPage > 1;
  }

  get canGoNext(): boolean {
    return this.currentPage < this.totalPages;
  }

  isMissing(dog: Mascota): boolean {
    return dog.estado === 'PERDIDO_PROPIO' || dog.estado === 'PERDIDO';
  }

  getStatusLabel(dog: Mascota): string {
    if (this.isMissing(dog)) return 'PERDIDO';
    if (dog.estado === 'RECUPERADO') return 'RECUPERADO';
    if (dog.estado === 'ADOPCION') return 'EN ADOPCIÓN';
    return dog.estado || 'DESCONOCIDO';
  }

  getCityLabel(dog: Mascota): string {
    const owner: any = dog.dueno as any;
    return (
      owner?.barrio ||
      (dog as any)?.barrioDueno ||
      (dog as any)?.barrio ||
      'Barrio desconocido'
    );
  }

  getContactPhone(dog: Mascota): string {
    const owner: any = dog.dueno as any;
    return (
      owner?.telefono ||
      owner?.telefonoContacto ||
      owner?.celular ||
      owner?.phone ||
      'Contacto no disponible'
    );
  }

  onImageError(event: Event, dog: any): void {
    (event.target as HTMLImageElement).src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200"%3E%3Crect fill="%23ddd" width="200" height="200"/%3E%3Ctext fill="%23999" x="50%25" y="50%25" text-anchor="middle" dy=".3em"%3EError%3C/text%3E%3C/svg%3E';
  }
}



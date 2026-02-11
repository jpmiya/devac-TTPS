import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { AvistamientoService } from '../../app/services/AvistamientoService';
import { Router } from '@angular/router';


type Avistamiento = {
  id: number;
  fecha?: any;

  fotoUrl?: string;
  coordenadas?: string;
  comentario?: string;

  usuarioId?: number;
  usuarioNombre?: string;

  mascotaId?: number;
  mascotaNombre?: string;
};

@Component({
  standalone: true,
  imports: [CommonModule],
  templateUrl: './all-avistamientos.component.html',
  styleUrls: ['./all-avistamientos.component.css'],
})
export class AvistamientosComponent implements OnInit {
  private avSvc = inject(AvistamientoService);
  private cdr = inject(ChangeDetectorRef);
  private location = inject(Location);
  private router = inject(Router);

  loading = false;
  error = '';
  items: Avistamiento[] = [];

  ngOnInit(): void {
    this.load();
  }

  goBack(): void {
    this.location.back();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.items = [];
    this.cdr.detectChanges();

    this.avSvc.getAll()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (list: any[]) => {
          this.items = (list ?? []) as Avistamiento[];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(err);
          this.error = 'No pude cargar los avistamientos.';
          this.cdr.detectChanges();
        }
      });
  }

  formatDate(d: any): string {
    if (!d) return '—';
    // si viene [y,m,d]
    if (Array.isArray(d) && d.length >= 3) {
      const [y, m, day] = d;
      return `${day}/${String(m).padStart(2,'0')}/${y}`;
    }
    // si viene ISO
    const s = String(d);
    if (s.includes('T')) return s.slice(0, 10);
    return s;
  }



  goCrear(): void {
    console.log('CLICK CREAR');
    this.router.navigate(['/avistamiento/create']);
  }

}

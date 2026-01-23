import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-lostPets',
  templateUrl: './lostPets.html',
  styleUrls: ['./lostPets.css'],
})





//DESPUES LO SIGO

export class LostDogsComponent implements OnInit {

  dogs: any[] = [];
  loading = false;
  error = '';

  private API = 'http://localhost:8080/mascota/findAllLost';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadDogs();
  }

  loadDogs(): void {
    this.loading = true;
    this.error = '';

    this.http.get<any[]>(this.API).subscribe({
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
  }
}


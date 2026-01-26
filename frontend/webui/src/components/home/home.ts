import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html'
})
export class HomeComponent implements OnInit {
  isLoggedIn = false;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log('HOME init, isLoggedIn=', this.isLoggedIn);

    this.http.get('http://localhost:8080/usuario/me').subscribe({
      next: () => {
        this.isLoggedIn = true;
        console.log('ME 200 -> isLoggedIn=', this.isLoggedIn);
      },
      error: (e) => {
        this.isLoggedIn = false;
        console.log('ME ERROR -> isLoggedIn=', this.isLoggedIn, e);
      }
    });
  }


  logout(): void {
    this.http.post('http://localhost:8080/usuario/logout', {}).subscribe({
      next: () => {
        this.isLoggedIn = false;
        this.router.navigate(['/login']);
      },
      error: () => {
        // si algo falla igual lo mandamos al login
        this.isLoggedIn = false;
        this.router.navigate(['/login']);
      }
    });
  }
}

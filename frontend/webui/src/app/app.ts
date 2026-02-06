import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../components/navbar/navbar.component';
import { AuthService } from './services/AuthService';
import { filter, map, mergeMap } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  hideNavbar = false;

  ngOnInit(): void {
    this.auth.checkSession();

    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd),
      map(() => this.route),
      map(route => { while (route.firstChild) route = route.firstChild; return route; }),
      mergeMap(route => route.data)
    ).subscribe(data => {
      this.hideNavbar = !!data['hideNavbar'];
    });
  }
}

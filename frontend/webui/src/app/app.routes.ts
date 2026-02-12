import { Routes } from '@angular/router';
import { LogInComponent } from '../components/login/login';
import { HomeComponent } from '../components/home/home';
import { LostDogsComponent } from '../components/lostPets/lostPets';
import { RegisterComponent } from '../components/register/register';
import { CreateMascotaComponent } from '../components/create-mascota/create-mascota';
import { EditUserComponent } from '../components/edit-user/edit-user';
import {ProfileComponent} from '../components/user-profile/user-profile';
import {MyPetsComponent} from '../components/my-pets/my-pets';
import {EditMascotaComponent} from '../components/edit-mascota/edit-mascota';
import {MascotaDetailComponent} from '../components/mascota-detail/mascota-detail';
import { AdoptionDogsComponent } from '../components/adoption-dogs/adoption-dogs';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LogInComponent, data: { hideNavbar: true } },
  { path: 'register', component: RegisterComponent, data: { hideNavbar: true } },
  { path: 'lost-dogs', component: LostDogsComponent },
  { path: 'adoption-dogs', component: AdoptionDogsComponent },
  { path: 'create-mascota', component: CreateMascotaComponent },
  {path: 'mascota/:id/edit', component:EditMascotaComponent},
  { path: 'edit-user', component: EditUserComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'my-pets', component: MyPetsComponent },
  { path: 'mascota/:id', component: MascotaDetailComponent },
];

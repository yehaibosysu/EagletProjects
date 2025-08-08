import { Routes } from '@angular/router';
import { startPageGuard } from '@core';

import { LayoutBasicComponent } from '../layout';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LandingComponent } from './landing/landing.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'passport', loadChildren: () => import('./passport/routes').then(m => m.routes) },
  {
    path: 'secure',
    component: LayoutBasicComponent,
    canActivate: [startPageGuard],
    canActivateChild: [],
    data: {},
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent }
    ]
  },
  { path: 'exception', loadChildren: () => import('./exception/routes').then(m => m.routes) },
  { path: '**', redirectTo: 'exception/404' }
];

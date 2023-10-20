import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'equipement',
        data: { pageTitle: 'Equipements' },
        loadChildren: () => import('./equipement/equipement.module').then(m => m.EquipementModule),
      },
      {
        path: 'park',
        data: { pageTitle: 'Parks' },
        loadChildren: () => import('./park/park.module').then(m => m.ParkModule),
      },
      {
        path: 'report',
        data: { pageTitle: 'Reports' },
        loadChildren: () => import('./report/report.module').then(m => m.ReportModule),
      },
      {
        path: 'media',
        data: { pageTitle: 'Media' },
        loadChildren: () => import('./media/media.module').then(m => m.MediaModule),
      },
      {
        path: 'events',
        data: { pageTitle: 'Events' },
        loadChildren: () => import('./events/events.module').then(m => m.EventsModule),
      },
      {
        path: 'rating',
        data: { pageTitle: 'Ratings' },
        loadChildren: () => import('./rating/rating.module').then(m => m.RatingModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}

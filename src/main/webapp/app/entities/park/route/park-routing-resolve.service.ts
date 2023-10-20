import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPark } from '../park.model';
import { ParkService } from '../service/park.service';

@Injectable({ providedIn: 'root' })
export class ParkRoutingResolveService implements Resolve<IPark | null> {
  constructor(protected service: ParkService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPark | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((park: HttpResponse<IPark>) => {
          if (park.body) {
            return of(park.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}

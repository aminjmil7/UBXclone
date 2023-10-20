import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPark, NewPark } from '../park.model';

export type PartialUpdatePark = Partial<IPark> & Pick<IPark, 'id'>;

type RestOf<T extends IPark | NewPark> = Omit<T, 'dateInstall' | 'dateOpen' | 'dateClose'> & {
  dateInstall?: string | null;
  dateOpen?: string | null;
  dateClose?: string | null;
};

export type RestPark = RestOf<IPark>;

export type NewRestPark = RestOf<NewPark>;

export type PartialUpdateRestPark = RestOf<PartialUpdatePark>;

export type EntityResponseType = HttpResponse<IPark>;
export type EntityArrayResponseType = HttpResponse<IPark[]>;

@Injectable({ providedIn: 'root' })
export class ParkService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/parks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(park: NewPark): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(park);
    return this.http.post<RestPark>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(park: IPark): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(park);
    return this.http
      .put<RestPark>(`${this.resourceUrl}/${this.getParkIdentifier(park)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(park: PartialUpdatePark): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(park);
    return this.http
      .patch<RestPark>(`${this.resourceUrl}/${this.getParkIdentifier(park)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPark>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPark[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getParkIdentifier(park: Pick<IPark, 'id'>): number {
    return park.id;
  }

  comparePark(o1: Pick<IPark, 'id'> | null, o2: Pick<IPark, 'id'> | null): boolean {
    return o1 && o2 ? this.getParkIdentifier(o1) === this.getParkIdentifier(o2) : o1 === o2;
  }

  addParkToCollectionIfMissing<Type extends Pick<IPark, 'id'>>(
    parkCollection: Type[],
    ...parksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const parks: Type[] = parksToCheck.filter(isPresent);
    if (parks.length > 0) {
      const parkCollectionIdentifiers = parkCollection.map(parkItem => this.getParkIdentifier(parkItem)!);
      const parksToAdd = parks.filter(parkItem => {
        const parkIdentifier = this.getParkIdentifier(parkItem);
        if (parkCollectionIdentifiers.includes(parkIdentifier)) {
          return false;
        }
        parkCollectionIdentifiers.push(parkIdentifier);
        return true;
      });
      return [...parksToAdd, ...parkCollection];
    }
    return parkCollection;
  }

  protected convertDateFromClient<T extends IPark | NewPark | PartialUpdatePark>(park: T): RestOf<T> {
    return {
      ...park,
      dateInstall: park.dateInstall?.toJSON() ?? null,
      dateOpen: park.dateOpen?.toJSON() ?? null,
      dateClose: park.dateClose?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPark: RestPark): IPark {
    return {
      ...restPark,
      dateInstall: restPark.dateInstall ? dayjs(restPark.dateInstall) : undefined,
      dateOpen: restPark.dateOpen ? dayjs(restPark.dateOpen) : undefined,
      dateClose: restPark.dateClose ? dayjs(restPark.dateClose) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPark>): HttpResponse<IPark> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPark[]>): HttpResponse<IPark[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

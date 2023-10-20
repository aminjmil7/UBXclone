import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEvents, NewEvents } from '../events.model';

export type PartialUpdateEvents = Partial<IEvents> & Pick<IEvents, 'id'>;

type RestOf<T extends IEvents | NewEvents> = Omit<T, 'eventDate'> & {
  eventDate?: string | null;
};

export type RestEvents = RestOf<IEvents>;

export type NewRestEvents = RestOf<NewEvents>;

export type PartialUpdateRestEvents = RestOf<PartialUpdateEvents>;

export type EntityResponseType = HttpResponse<IEvents>;
export type EntityArrayResponseType = HttpResponse<IEvents[]>;

@Injectable({ providedIn: 'root' })
export class EventsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/events');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(events: NewEvents): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(events);
    return this.http
      .post<RestEvents>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(events: IEvents): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(events);
    return this.http
      .put<RestEvents>(`${this.resourceUrl}/${this.getEventsIdentifier(events)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(events: PartialUpdateEvents): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(events);
    return this.http
      .patch<RestEvents>(`${this.resourceUrl}/${this.getEventsIdentifier(events)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEvents>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEvents[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEventsIdentifier(events: Pick<IEvents, 'id'>): number {
    return events.id;
  }

  compareEvents(o1: Pick<IEvents, 'id'> | null, o2: Pick<IEvents, 'id'> | null): boolean {
    return o1 && o2 ? this.getEventsIdentifier(o1) === this.getEventsIdentifier(o2) : o1 === o2;
  }

  addEventsToCollectionIfMissing<Type extends Pick<IEvents, 'id'>>(
    eventsCollection: Type[],
    ...eventsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const events: Type[] = eventsToCheck.filter(isPresent);
    if (events.length > 0) {
      const eventsCollectionIdentifiers = eventsCollection.map(eventsItem => this.getEventsIdentifier(eventsItem)!);
      const eventsToAdd = events.filter(eventsItem => {
        const eventsIdentifier = this.getEventsIdentifier(eventsItem);
        if (eventsCollectionIdentifiers.includes(eventsIdentifier)) {
          return false;
        }
        eventsCollectionIdentifiers.push(eventsIdentifier);
        return true;
      });
      return [...eventsToAdd, ...eventsCollection];
    }
    return eventsCollection;
  }

  protected convertDateFromClient<T extends IEvents | NewEvents | PartialUpdateEvents>(events: T): RestOf<T> {
    return {
      ...events,
      eventDate: events.eventDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEvents: RestEvents): IEvents {
    return {
      ...restEvents,
      eventDate: restEvents.eventDate ? dayjs(restEvents.eventDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEvents>): HttpResponse<IEvents> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEvents[]>): HttpResponse<IEvents[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

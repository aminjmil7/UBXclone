import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEquipement, NewEquipement } from '../equipement.model';

export type PartialUpdateEquipement = Partial<IEquipement> & Pick<IEquipement, 'id'>;

export type EntityResponseType = HttpResponse<IEquipement>;
export type EntityArrayResponseType = HttpResponse<IEquipement[]>;

@Injectable({ providedIn: 'root' })
export class EquipementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/equipements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(equipement: NewEquipement): Observable<EntityResponseType> {
    return this.http.post<IEquipement>(this.resourceUrl, equipement, { observe: 'response' });
  }

  update(equipement: IEquipement): Observable<EntityResponseType> {
    return this.http.put<IEquipement>(`${this.resourceUrl}/${this.getEquipementIdentifier(equipement)}`, equipement, {
      observe: 'response',
    });
  }

  partialUpdate(equipement: PartialUpdateEquipement): Observable<EntityResponseType> {
    return this.http.patch<IEquipement>(`${this.resourceUrl}/${this.getEquipementIdentifier(equipement)}`, equipement, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEquipement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEquipement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEquipementIdentifier(equipement: Pick<IEquipement, 'id'>): number {
    return equipement.id;
  }

  compareEquipement(o1: Pick<IEquipement, 'id'> | null, o2: Pick<IEquipement, 'id'> | null): boolean {
    return o1 && o2 ? this.getEquipementIdentifier(o1) === this.getEquipementIdentifier(o2) : o1 === o2;
  }

  addEquipementToCollectionIfMissing<Type extends Pick<IEquipement, 'id'>>(
    equipementCollection: Type[],
    ...equipementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const equipements: Type[] = equipementsToCheck.filter(isPresent);
    if (equipements.length > 0) {
      const equipementCollectionIdentifiers = equipementCollection.map(equipementItem => this.getEquipementIdentifier(equipementItem)!);
      const equipementsToAdd = equipements.filter(equipementItem => {
        const equipementIdentifier = this.getEquipementIdentifier(equipementItem);
        if (equipementCollectionIdentifiers.includes(equipementIdentifier)) {
          return false;
        }
        equipementCollectionIdentifiers.push(equipementIdentifier);
        return true;
      });
      return [...equipementsToAdd, ...equipementCollection];
    }
    return equipementCollection;
  }
}

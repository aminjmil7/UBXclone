import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EventsFormService, EventsFormGroup } from './events-form.service';
import { IEvents } from '../events.model';
import { EventsService } from '../service/events.service';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';

@Component({
  selector: 'jhi-events-update',
  templateUrl: './events-update.component.html',
})
export class EventsUpdateComponent implements OnInit {
  isSaving = false;
  events: IEvents | null = null;

  parksSharedCollection: IPark[] = [];
  equipementsSharedCollection: IEquipement[] = [];

  editForm: EventsFormGroup = this.eventsFormService.createEventsFormGroup();

  constructor(
    protected eventsService: EventsService,
    protected eventsFormService: EventsFormService,
    protected parkService: ParkService,
    protected equipementService: EquipementService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePark = (o1: IPark | null, o2: IPark | null): boolean => this.parkService.comparePark(o1, o2);

  compareEquipement = (o1: IEquipement | null, o2: IEquipement | null): boolean => this.equipementService.compareEquipement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ events }) => {
      this.events = events;
      if (events) {
        this.updateForm(events);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const events = this.eventsFormService.getEvents(this.editForm);
    if (events.id !== null) {
      this.subscribeToSaveResponse(this.eventsService.update(events));
    } else {
      this.subscribeToSaveResponse(this.eventsService.create(events));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvents>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(events: IEvents): void {
    this.events = events;
    this.eventsFormService.resetForm(this.editForm, events);

    this.parksSharedCollection = this.parkService.addParkToCollectionIfMissing<IPark>(this.parksSharedCollection, events.park);
    this.equipementsSharedCollection = this.equipementService.addEquipementToCollectionIfMissing<IEquipement>(
      this.equipementsSharedCollection,
      events.equipement
    );
  }

  protected loadRelationshipsOptions(): void {
    this.parkService
      .query()
      .pipe(map((res: HttpResponse<IPark[]>) => res.body ?? []))
      .pipe(map((parks: IPark[]) => this.parkService.addParkToCollectionIfMissing<IPark>(parks, this.events?.park)))
      .subscribe((parks: IPark[]) => (this.parksSharedCollection = parks));

    this.equipementService
      .query()
      .pipe(map((res: HttpResponse<IEquipement[]>) => res.body ?? []))
      .pipe(
        map((equipements: IEquipement[]) =>
          this.equipementService.addEquipementToCollectionIfMissing<IEquipement>(equipements, this.events?.equipement)
        )
      )
      .subscribe((equipements: IEquipement[]) => (this.equipementsSharedCollection = equipements));
  }
}

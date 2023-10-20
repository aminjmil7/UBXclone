import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RatingFormService, RatingFormGroup } from './rating-form.service';
import { IRating } from '../rating.model';
import { RatingService } from '../service/rating.service';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';

@Component({
  selector: 'jhi-rating-update',
  templateUrl: './rating-update.component.html',
})
export class RatingUpdateComponent implements OnInit {
  isSaving = false;
  rating: IRating | null = null;

  parksSharedCollection: IPark[] = [];
  equipementsSharedCollection: IEquipement[] = [];

  editForm: RatingFormGroup = this.ratingFormService.createRatingFormGroup();

  constructor(
    protected ratingService: RatingService,
    protected ratingFormService: RatingFormService,
    protected parkService: ParkService,
    protected equipementService: EquipementService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePark = (o1: IPark | null, o2: IPark | null): boolean => this.parkService.comparePark(o1, o2);

  compareEquipement = (o1: IEquipement | null, o2: IEquipement | null): boolean => this.equipementService.compareEquipement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rating }) => {
      this.rating = rating;
      if (rating) {
        this.updateForm(rating);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rating = this.ratingFormService.getRating(this.editForm);
    if (rating.id !== null) {
      this.subscribeToSaveResponse(this.ratingService.update(rating));
    } else {
      this.subscribeToSaveResponse(this.ratingService.create(rating));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRating>>): void {
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

  protected updateForm(rating: IRating): void {
    this.rating = rating;
    this.ratingFormService.resetForm(this.editForm, rating);

    this.parksSharedCollection = this.parkService.addParkToCollectionIfMissing<IPark>(this.parksSharedCollection, rating.park);
    this.equipementsSharedCollection = this.equipementService.addEquipementToCollectionIfMissing<IEquipement>(
      this.equipementsSharedCollection,
      rating.equipement
    );
  }

  protected loadRelationshipsOptions(): void {
    this.parkService
      .query()
      .pipe(map((res: HttpResponse<IPark[]>) => res.body ?? []))
      .pipe(map((parks: IPark[]) => this.parkService.addParkToCollectionIfMissing<IPark>(parks, this.rating?.park)))
      .subscribe((parks: IPark[]) => (this.parksSharedCollection = parks));

    this.equipementService
      .query()
      .pipe(map((res: HttpResponse<IEquipement[]>) => res.body ?? []))
      .pipe(
        map((equipements: IEquipement[]) =>
          this.equipementService.addEquipementToCollectionIfMissing<IEquipement>(equipements, this.rating?.equipement)
        )
      )
      .subscribe((equipements: IEquipement[]) => (this.equipementsSharedCollection = equipements));
  }
}
